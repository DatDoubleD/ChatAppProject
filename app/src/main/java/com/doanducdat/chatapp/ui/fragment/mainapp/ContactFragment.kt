package com.doanducdat.chatapp.ui.fragment.mainapp

import android.content.ContentResolver
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.doanducdat.chatapp.R
import com.doanducdat.chatapp.databinding.FragmentContactBinding
import com.doanducdat.chatapp.model.MyCustomDialog
import com.doanducdat.chatapp.model.User
import com.doanducdat.chatapp.ui.adapter.ContactAdapter
import com.doanducdat.chatapp.utils.CheckPermission
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ContactFragment : Fragment() {

    private lateinit var binding: FragmentContactBinding
    private var mobileContact:MutableList<User> = mutableListOf()
    private var appContact:MutableList<User> = mutableListOf()
    private val myCustomDialog:MyCustomDialog by lazy { MyCustomDialog(requireActivity()) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContactBinding.inflate(inflater, container, false)
        if (CheckPermission.requestPermissionContact(requireActivity(), 2000)){
            myCustomDialog.startLoadingDialog(R.layout.custom_dialog, 200, 250)
            //get mobile contact -> get database with mobile contact -> i will have app contact
            getMobileContact()
        }
        return binding.root
    }

    private fun getMobileContact() {
        val contentResolver:ContentResolver = requireContext().contentResolver
        val cursor: Cursor? = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.Data.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            ),
            null, null,
            null
        )
        if (cursor!= null){
            while (cursor.moveToNext()) {
                var name: String =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                var number: String =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                number = number.replace("\\s".toRegex(), "")
                var num = number.elementAt(0).toString()
                if (num == "0")
                    number = number.replaceFirst("(?:0)+".toRegex(), "+84")

                val user: User = User()
                user.name = name
                user.phone = number
                mobileContact.add(user)
            }
            cursor.close()
        }
        getAppContact()
    }

    private fun getAppContact() {
        val dbRef = FirebaseDatabase.getInstance().getReference("users")
        val query = dbRef.orderByChild("phone")
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //compare phone of sever with phone of local device
                if (snapshot.exists()) {
                    for (dataSever in snapshot.children) { //get user in sever
                        val phoneUserSever = dataSever.child("phone").value.toString()
                        for (userDevice in mobileContact) {//get user in device
                            if (userDevice.phone == phoneUserSever) { // true -> user in device exit in database -> get them
                                val userSever: User? = dataSever.getValue(User::class.java)
                                appContact.add(userSever!!) //list contact that contact exit in device and database
                            }
                        }
                    }
                    // update UI
                    val adapter: ContactAdapter = ContactAdapter(appContact)
                    binding.rcvPhoneUser.setHasFixedSize(true)
                    binding.rcvPhoneUser.adapter = adapter
                    myCustomDialog.stopLoadingDialog()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                myCustomDialog.stopLoadingDialog()
                Toast.makeText(requireContext(), "Error! please try again later", Toast.LENGTH_SHORT).show()
            }
        })
    }

}