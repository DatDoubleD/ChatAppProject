package com.doanducdat.chatapp.ui.fragment.mainapp

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.doanducdat.chatapp.R
import com.doanducdat.chatapp.databinding.FragmentViewSendMsgBinding
import com.doanducdat.chatapp.model.ChatList
import com.doanducdat.chatapp.model.Message
import com.doanducdat.chatapp.model.User
import com.doanducdat.chatapp.ui.adapter.ChatfirebaseRecyclerAdapter
import com.doanducdat.chatapp.utils.AppUtil
import com.doanducdat.chatapp.utils.Appconstants
import com.doanducdat.chatapp.viewmodel.ProfileViewModel
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.*
import org.json.JSONObject


class ViewSendMsgFragment : Fragment() {
    private lateinit var binding: FragmentViewSendMsgBinding
    private lateinit var partnerUser: User
    private lateinit var myUser: User
    private var chatId: String? = null
    private val appUtil by lazy { AppUtil() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewSendMsgBinding.inflate(inflater, container, false)
        getPartnerUserAndMyUser()
        checkOnlineStatusPartnerUser()

        setUpEvent()
        if (chatId == null) {
            checkChat()
        }
        return binding.root
    }

    private fun checkTypingStatus(typing: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("users").child(myUser.uID)
        val map: Map<String, Any> = mapOf("typing" to typing)
        dbRef.updateChildren(map)

    }

    private fun checkOnlineStatusPartnerUser() {
        val dbRef = FirebaseDatabase.getInstance().getReference("users").child(partnerUser.uID)
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var statusOnlineOfPartnerUser = snapshot.child("online").value.toString()
                    binding.online = statusOnlineOfPartnerUser
                    //take advantages of datachange listen online status + listen typing status
                    //when typing save partnerUserID but check -> check with myUserID
                    //=> purpose is: when i typing, TYPING ANIMATION OF PARTNER_USER WILL SHOW
                    val typing: String = snapshot.child("typing").value.toString()
                    if (typing == myUser.uID) {
                        binding.animTyping.playAnimation()
                        binding.animTyping.visibility = View.VISIBLE
                    } else {
                        binding.animTyping.visibility = View.INVISIBLE
                        binding.animTyping.cancelAnimation()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
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
                getToken(message)
            }
        }
        //check status typign
        binding.edtMsg.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (TextUtils.isEmpty(s.toString())) {
                    checkTypingStatus("false")
                } else {
                    checkTypingStatus(partnerUser.uID)
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    private fun getPartnerUserAndMyUser() {
        val bundle: Bundle? = arguments
        if (bundle != null) {
            partnerUser = bundle.getSerializable("PARTNER_USER") as User
            myUser = bundle.getSerializable("MY_USER") as User
            if (bundle.getString("chatID") != null){
                chatId = bundle.getString("chatID")
            }
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
        binding.edtMsg.text.clear()
        binding.edtMsg.requestFocus()
    }

    /***
     * read msg to recyclerview, this method called in function checkChat()
     */
    private fun readMessage() {
        val query: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Chat").child(chatId!!)
        val option = FirebaseRecyclerOptions.Builder<Message>()
            .setLifecycleOwner(this).setQuery(query, Message::class.java)
            .build()
        query.keepSynced(true)
        val adapter: ChatfirebaseRecyclerAdapter =
            ChatfirebaseRecyclerAdapter(option, partnerUser, myUser)
        binding.rcvMessage.adapter = adapter
    }

    //FCM
    private fun getToken(message: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("users").child(partnerUser.uID)
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val token = snapshot.child("token").value.toString()
                    //write notification
                    val to = JSONObject()
                    val data = JSONObject()
                    //partnerUser into json -> map in service
                    data.put("partnerUser_name", myUser.name)
                    data.put("partnerUser_status", myUser.status)
                    data.put("partnerUser_image", myUser.image)
                    data.put("partnerUser_phone", myUser.phone)
                    data.put("partnerUser_birthYear", myUser.birthYear)
                    data.put("partnerUser_uID", myUser.uID)
                    data.put("partnerUser_online", myUser.online)
                    //myUser into json -> map in service
                    data.put("myUser_name", partnerUser.name)
                    data.put("myUser_status", partnerUser.status)
                    data.put("myUser_image", partnerUser.image)
                    data.put("myUser_phone", partnerUser.phone)
                    data.put("myUser_birthYear", partnerUser.birthYear)
                    data.put("myUser_uID", partnerUser.uID)
                    data.put("myUser_online", partnerUser.online)
                    //
                    data.put("title", myUser.name)
                    data.put("message", message)
                    data.put("chatID", chatId)

                    to.put("to", token)
                    to.put("data", data)
                    sendNotification(to)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun sendNotification(to: JSONObject) {
        val request: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            Appconstants.NOTIFICATION_URL,
            to,
            Response.Listener { response: JSONObject ->
                Log.d("TAG", "sendNotification: $response")
            },
            Response.ErrorListener {
                Log.d("TAG", "sendNotification: $it")

            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val map:MutableMap<String, String> = hashMapOf(
                    "Authorization" to "key=${Appconstants.SERVER_KEY}",
                    "Content_type" to "application/json")
                return map
            }

            override fun getBodyContentType(): String {
                return "application/json"
            }
        }
        val requestQueue = Volley.newRequestQueue(requireContext())
        request.retryPolicy = DefaultRetryPolicy(
            30000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        requestQueue.add(request)
    }
}