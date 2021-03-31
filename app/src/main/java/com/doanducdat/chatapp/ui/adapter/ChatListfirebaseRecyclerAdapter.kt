package com.doanducdat.chatapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.doanducdat.chatapp.databinding.ChatListItemBinding
import com.doanducdat.chatapp.model.ChatList
import com.doanducdat.chatapp.model.ChatListAndInfoPartnerUser
import com.doanducdat.chatapp.model.User
import com.doanducdat.chatapp.utils.AppUtil
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatListfirebaseRecyclerAdapter(
    option: FirebaseRecyclerOptions<ChatList>,
    private val onItemClickChat:(User) -> Unit
) :
    FirebaseRecyclerAdapter<ChatList, ChatListfirebaseRecyclerAdapter.ChatListViewHolder>(option) {

    private val  appUtil = AppUtil()
    inner class ChatListViewHolder(private var chatListItemBinding: ChatListItemBinding) :
        RecyclerView.ViewHolder(chatListItemBinding.root) {

        fun onBind(itemOnChatList: ChatListAndInfoPartnerUser, partnerUser: User) {
            chatListItemBinding.itemOnChatList = itemOnChatList
            chatListItemBinding.cardViewItemChatList.setOnClickListener {
                onItemClickChat(partnerUser)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListViewHolder {
        val chatListItemBinding: ChatListItemBinding =
            ChatListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatListViewHolder(chatListItemBinding)
    }

    override fun onBindViewHolder(holder: ChatListViewHolder, position: Int, chatList: ChatList) {
        //note chatlist: last msg, date, i have query users to get infor partnerUser
        val dbRef = FirebaseDatabase.getInstance().getReference("users").child(chatList.member)
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    //get partnerUser to show: img, name
                    val partnerUser: User? = snapshot.getValue(User::class.java)
                    //show date + last msg + chatId
                    val date: String? = appUtil.getTimeAgo(chatList.date.toLong())
                    //combine both partnerUser and chatList -> Up to UI chat_list_item.xml
                    val itemOnChatList = ChatListAndInfoPartnerUser(
                        chatList.chatId,
                        partnerUser!!.name,
                        chatList.lastMessage,
                        partnerUser.image,
                        date!!)
                    holder.onBind(itemOnChatList, partnerUser)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }
}