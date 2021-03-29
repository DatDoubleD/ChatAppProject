package com.doanducdat.chatapp.ui.fragment.mainapp

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import com.doanducdat.chatapp.R
import com.doanducdat.chatapp.databinding.FragmentViewSendMsgBinding
import com.doanducdat.chatapp.model.ChatList
import com.doanducdat.chatapp.model.Message
import com.doanducdat.chatapp.model.User
import com.doanducdat.chatapp.ui.adapter.ChatfirebaseRecyclerAdapter
import com.doanducdat.chatapp.utils.AppUtil
import com.doanducdat.chatapp.viewmodel.ProfileViewModel
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.*


class ViewSendMsgFragment : Fragment() {
    private lateinit var binding: FragmentViewSendMsgBinding
    private lateinit var partnerUser: User
    private lateinit var myUser:User
    private var chatId: String? = null
    private val appUtil by lazy { AppUtil() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewSendMsgBinding.inflate(inflater, container, false)
        getPartnerUser()
        getMyUser()
        setUpEvent()
        if (chatId == null) {
            checkChat()
        }
        return binding.root
    }

    private fun getMyUser() {
        val dbRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("users").child(appUtil.getUid())
        dbRef.get().addOnSuccessListener {
            if (it.exists()){
                myUser = it.getValue(User::class.java)!!
            }
        }
    }

    private fun setUpEvent() {
        binding.imgBack.setOnClickListener {
            closeFragment()
        }

        binding.imgInfo.setOnClickListener {
            showInforPartnetUser()
        }

        binding.btnSend.setOnClickListener {
            val message: String = binding.edtMsg.text.toString()
            if (TextUtils.isEmpty(message)) {
                Toast.makeText(requireContext(), "Enter your msg...", Toast.LENGTH_SHORT).show()
            } else {
                sendMessage(message)
            }
        }
    }

    private fun getPartnerUser() {
        val bundle: Bundle? = arguments
        if (bundle != null) {
            partnerUser = bundle.getSerializable("PARTNER_USER") as User
        }
    }

    private fun showInforPartnetUser() {
        val viewProfileFragment: ViewProfileFragment = ViewProfileFragment()
        val bundle: Bundle = bundleOf("INFO_USER" to partnerUser)
        viewProfileFragment.arguments = bundle
        requireActivity().supportFragmentManager.beginTransaction()
            .add(R.id.container_main, viewProfileFragment).commit()
    }

    private fun closeFragment() {
        requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
    }

    //
    /***
     * check chat is exit or not. Chat exits -> just send mess in that chatId and read ChatMessage
     * when send message if idchat not exit -> call method: "createChat"
     */
    private fun checkChat() {
        // create chat list with id of login current user
        val dbRef = FirebaseDatabase.getInstance().getReference("ChatList").child(appUtil.getUid())
        val query: Query = dbRef.orderByChild("member").equalTo(partnerUser.uID)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (ds in snapshot.children) {
                        val member = ds.child("member").value.toString()
                        if (partnerUser.uID == member) {
                            chatId = ds.key
                            readMessage()
                            break
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    /***
     * create chat on both user(user is me and partner user) because chat is same where all messages all saved
     * save "chatlist1" to "my id" and "chatlist2" to "partneruser id": chatId, lastmsg...
     * save "msg, receiver - sender" to "chatNote"
     */
    private fun createChat(message: String) {
        //create chatID
        var dbRef = FirebaseDatabase.getInstance().getReference("ChatList").child(appUtil.getUid())
        chatId = dbRef.push().key
        //save chatlist1 to myid
        val chatList1: ChatList =
            ChatList(chatId!!, message, System.currentTimeMillis().toString(), partnerUser.uID)
        dbRef.child(chatId!!).setValue(chatList1)

        dbRef = FirebaseDatabase.getInstance().getReference("ChatList").child(partnerUser.uID)
        //save chatlist2 to partnerUserID
        val chatList2: ChatList =
            ChatList(chatId!!, message, System.currentTimeMillis().toString(), appUtil.getUid())
        dbRef.child(chatId!!).setValue(chatList2)
        //save msg, receiver - sender to chatNOTE
        dbRef = FirebaseDatabase.getInstance().getReference("Chat").child(chatId!!)
        val myMessage: Message = Message(appUtil.getUid(), partnerUser.uID, message, type = "text")
        dbRef.push().setValue(myMessage)
    }

    /***
     * when send 2nd message it just update last msg and add new msg in chat note
     */
    private fun sendMessage(message: String) {
        if (chatId == null) {
            createChat(message)
        } else {
            //ChatNOTE and ChatListNOTE are exit -> add new messages to chatNOTE
            var dbRef: DatabaseReference =
                FirebaseDatabase.getInstance().getReference("Chat").child(chatId!!)
            //add new messages when user click sendMessage
            val myMessage: Message =
                Message(appUtil.getUid(), partnerUser.uID, message, type = "text")
            dbRef.push().setValue(myMessage)
            //update lastest msg to both user -> use to load itemview in fragmentChat of BtmNvgationBar
            val map: MutableMap<String, Any> = hashMapOf(
                "lastMessage" to message,
                "date" to System.currentTimeMillis().toString()
            )
            dbRef = FirebaseDatabase.getInstance().getReference("ChatList").child(appUtil.getUid())
                .child(chatId!!)
            dbRef.updateChildren(map)
            dbRef = FirebaseDatabase.getInstance().getReference("ChatList").child(partnerUser.uID)
                .child(chatId!!)
            dbRef.updateChildren(map)
        }
    }

    /***
     * read msg to recyclerview
     */
    private fun readMessage() {
        val query: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Chat").child(chatId!!)
        val option = FirebaseRecyclerOptions.Builder<Message>()
            .setLifecycleOwner(this).setQuery(query, Message::class.java)
            .build()
        query.keepSynced(true)
        val adapter:ChatfirebaseRecyclerAdapter = ChatfirebaseRecyclerAdapter(option, partnerUser, myUser)
        binding.rcvMessage.adapter = adapter
    }
}