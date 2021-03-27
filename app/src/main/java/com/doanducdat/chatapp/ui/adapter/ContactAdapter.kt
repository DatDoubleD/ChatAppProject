package com.doanducdat.chatapp.ui.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.doanducdat.chatapp.databinding.ItemContactBinding
import com.doanducdat.chatapp.model.User

class ContactAdapter(private val appContact: MutableList<User>, private val onItemClickInfoUser:(User) -> Unit) :
    RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    inner class ContactViewHolder(binding:ItemContactBinding ) : RecyclerView.ViewHolder(binding.root) {
        private val itemContactBinding:ItemContactBinding = binding
        fun onBind(user: User) {
            itemContactBinding.user = user
            itemContactBinding.imgInfo.setOnClickListener {
                onItemClickInfoUser(user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding: ItemContactBinding = ItemContactBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return ContactViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.onBind(appContact[position])
    }

    override fun getItemCount(): Int {
        return appContact.size
    }
}