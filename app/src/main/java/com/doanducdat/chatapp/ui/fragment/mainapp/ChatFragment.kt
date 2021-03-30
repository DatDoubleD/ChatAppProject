package com.doanducdat.chatapp.ui.fragment.mainapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.doanducdat.chatapp.R
import com.doanducdat.chatapp.databinding.FragmentChatBinding
import com.doanducdat.chatapp.model.ChatList
import com.doanducdat.chatapp.ui.adapter.ChatListfirebaseRecyclerAdapter
import com.doanducdat.chatapp.utils.AppUtil
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase


class ChatFragment : Fragment() {
    private lateinit var binding: FragmentChatBinding
    private val appUtil: AppUtil by lazy { AppUtil() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        readChatList()



        return binding.root
    }

    private fun readChatList() {
        val query = FirebaseDatabase.getInstance().getReference("ChatList").child(appUtil.getUid())
        val option = FirebaseRecyclerOptions.Builder<ChatList>().setLifecycleOwner(this)
            .setQuery(query, ChatList::class.java)
            .build()

        val adapter:ChatListfirebaseRecyclerAdapter = ChatListfirebaseRecyclerAdapter(option)
        binding.rcvChatList.setHasFixedSize(true)
        binding.rcvChatList.adapter = adapter
    }
}