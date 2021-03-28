package com.doanducdat.chatapp.ui.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.doanducdat.chatapp.databinding.ItemContactBinding
import com.doanducdat.chatapp.model.User
import java.util.*

class ContactAdapter(
    private var appContact: MutableList<User>,
    private val onItemClickInfoUser: (User) -> Unit,
    private val onItemClickChatUser: (User) -> Unit
) :
    RecyclerView.Adapter<ContactAdapter.ContactViewHolder>(), Filterable {

    var contactList: MutableList<User>  = appContact


    inner class ContactViewHolder(binding: ItemContactBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val itemContactBinding: ItemContactBinding = binding
        fun onBind(user: User) {
            itemContactBinding.user = user
            itemContactBinding.imgInfo.setOnClickListener {
                onItemClickInfoUser(user)
            }
            itemContactBinding.imgChat.setOnClickListener {
                onItemClickChatUser
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding: ItemContactBinding = ItemContactBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ContactViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.onBind(contactList[position])
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    var filteredContact: MutableList<User> = mutableListOf()
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val searchContent: String = constraint.toString()

                if (TextUtils.isEmpty(searchContent)) {
                    contactList = appContact
                } else {
                    filteredContact.clear()
                    for (user in appContact) {
                        if (user.name.toLowerCase(Locale.ROOT).trim()
                                .contains(searchContent.toLowerCase(Locale.ROOT).trim())
                        ) {
                            filteredContact.add(user)
                        }
                    }
                    contactList = filteredContact
                }
                //return FilterResults type data -> publishResults method
                val filterResult: FilterResults = FilterResults()
                filterResult.values = contactList
                return filterResult
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                // assign results for appcontact because appContact is list that use to update UI
                contactList = results?.values as MutableList<User>
                notifyDataSetChanged()
            }
        }
    }
}