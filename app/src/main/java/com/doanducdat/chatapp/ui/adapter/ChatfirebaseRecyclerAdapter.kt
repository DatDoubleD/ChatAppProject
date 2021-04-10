package com.doanducdat.chatapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.doanducdat.chatapp.BR
import com.doanducdat.chatapp.databinding.LeftItemChatBinding
import com.doanducdat.chatapp.databinding.RightItemChatBinding
import com.doanducdat.chatapp.model.Message
import com.doanducdat.chatapp.model.User
import com.doanducdat.chatapp.utils.AppUtil
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class ChatfirebaseRecyclerAdapter(
    private var option: FirebaseRecyclerOptions<Message>,
    private var partnerUser: User,
    private var myUser:User
) :
    FirebaseRecyclerAdapter<Message, ChatfirebaseRecyclerAdapter.ChatViewHolder>(option) {

    private val appUtil: AppUtil = AppUtil()

    override fun getItemViewType(position: Int): Int {
        val message: Message = getItem(position)

        return if (message.senderId == appUtil.getUid()) {
            //here is my msg -> view color: green(check that is left or right_item_chat)
            0
        } else
            1
    }

    inner class ChatViewHolder(private var viewDataBinding: ViewDataBinding) :
        RecyclerView.ViewHolder(viewDataBinding.root) {
        fun onBind(message: Message, user: User) {
            viewDataBinding.setVariable(BR.user, user)
            viewDataBinding.setVariable(BR.message, message)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        //create ChatViewHolder according to viewTYpe if msg is mine or receiver
        var viewDataBinding: ViewDataBinding? = null
        if (viewType == 0) {
            viewDataBinding =
                RightItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        }
        if (viewType == 1) {
            viewDataBinding =
                LeftItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        }
        return ChatViewHolder(viewDataBinding!!)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int, message: Message) {
        if (getItemViewType(position) == 0){
            holder.onBind(message, myUser)
        }
        if (getItemViewType(position) == 1){
            holder.onBind(message, partnerUser)
        }
    }

}