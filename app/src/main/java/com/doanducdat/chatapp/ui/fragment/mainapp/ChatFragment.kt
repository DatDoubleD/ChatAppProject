package com.doanducdat.chatapp.ui.fragment.mainapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.doanducdat.chatapp.R
import com.doanducdat.chatapp.databinding.FragmentChatBinding
import com.doanducdat.chatapp.model.ChatList
import com.doanducdat.chatapp.model.User
import com.doanducdat.chatapp.ui.adapter.ChatListfirebaseRecyclerAdapter
import com.doanducdat.chatapp.utils.AppUtil
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class ChatFragment : Fragment() {
    private lateinit var binding: FragmentChatBinding
    private val appUtil: AppUtil by lazy { AppUtil() }
    private lateinit var myUser: User
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        getMyUser()
        readChatList()


        return binding.root
    }
    private fun getMyUser() { // getmy user to send viewsendmsgFragment to show Image of myuser
        val dbRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("users").child(appUtil.getUid())
        dbRef.get().addOnSuccessListener {
            if (it.exists()){
                myUser = it.getValue(User::class.java)!!
            }
        }
    }
    private fun readChatList() {
        val query = FirebaseDatabase.getInstance().getReference("ChatList").child(appUtil.getUid())
        val option = FirebaseRecyclerOptions.Builder<ChatList>().setLifecycleOwner(this)
            .setQuery(query, ChatList::class.java)
            .build()

        val adapter: ChatListfirebaseRecyclerAdapter = ChatListfirebaseRecyclerAdapter(option, onItemClickChat)
        binding.rcvChatList.setHasFixedSize(true)
        binding.rcvChatList.adapter = adapter
    }

    private val onItemClickChat: (User) -> Unit = {
        val viewSendMsgFragment: ViewSendMsgFragment = ViewSendMsgFragment()
        val bundle: Bundle = bundleOf("PARTNER_USER" to it, "MY_USER" to myUser)
        viewSendMsgFragment.arguments = bundle
        requireActivity().supportFragmentManager.beginTransaction()
            .add(R.id.container_main, viewSendMsgFragment)
            .commit()
    }
}