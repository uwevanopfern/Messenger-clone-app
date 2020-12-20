package com.niipo.uwemessanger

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.niipo.uwemessanger.Adapters.ChatFromItem
import com.niipo.uwemessanger.Adapters.ChatToItem
import com.niipo.uwemessanger.model.ChatMessage
import com.niipo.uwemessanger.model.User
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*

class ChatLogActivity : AppCompatActivity() {

    companion object {
        val TAG = "ChatLog"
    }

    val groupieAdapter = GroupAdapter<ViewHolder>()

    var fromUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        //Parcelable helps to pass whole object of class between activity

        fromUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)

        supportActionBar?.title = fromUser!!.username

        rv_chat_log.adapter = groupieAdapter

        listenForMessage()

        btn_send_message.setOnClickListener {

            performSendMessage()
        }
    }

    private fun performSendMessage() {

        val message = edit_txt_message.text.toString()
        val fromId = FirebaseAuth.getInstance().uid

        if (message.isEmpty()) {
            Toast.makeText(this, "Type a message", Toast.LENGTH_LONG).show()
        } else {

            var user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
            val toId = user.uid

            //push method generate new tree(NODE) for us (like message -> KHHJSHEJ242JH)

            val fromRef = FirebaseDatabase.getInstance().getReference("/messages/$fromId/$toId").push()
            val toRef = FirebaseDatabase.getInstance().getReference("/messages/$toId/$fromId").push()

            val chatMessage = ChatMessage(fromRef.key!!, message, fromId!!, toId!!, System.currentTimeMillis() / 1000)

            if (fromId == toId) {

                val fromRef = FirebaseDatabase.getInstance().getReference("/messages/$fromId/$toId").push()
                fromRef.setValue(chatMessage)
                        .addOnSuccessListener {
                            Log.d(TAG, "Message sent with success: ${fromRef.key}")
                            edit_txt_message.text.clear()
                            //automatically scroll up and show new added message in recycler view after
                            rv_chat_log.scrollToPosition(groupieAdapter.itemCount - 1)
                        }

                val latestMessageFromRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
                latestMessageFromRef.setValue(chatMessage)
            } else {


                fromRef.setValue(chatMessage)
                        .addOnSuccessListener {
                            Log.d(TAG, "Message sent with success: ${fromRef.key}")
                            edit_txt_message.text.clear()
                            //automatically scroll up and show new added message in recycler view after
                            rv_chat_log.scrollToPosition(groupieAdapter.itemCount - 1)
                        }
                toRef.setValue(chatMessage)


                val latestMessageFromRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
                latestMessageFromRef.setValue(chatMessage)

                val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
                latestMessageToRef.setValue(chatMessage)
            }
        }
    }

    private fun listenForMessage() {
        val fromId = FirebaseAuth.getInstance().uid

        var user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user.uid
        val ref = FirebaseDatabase.getInstance().getReference("/messages/$fromId/$toId")
        ref.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {

                val chatMessage = p0.getValue(ChatMessage::class.java)
                Log.d(TAG, chatMessage?.text)
                if (chatMessage != null) {

                    if (chatMessage.fromId != FirebaseAuth.getInstance().uid) {
                        groupieAdapter.add(ChatFromItem(chatMessage.text, fromUser!!))
                    } else {
                        var currentLoggedInUser = LatestMessagesActivity.currentUser

                        groupieAdapter.add(ChatToItem(chatMessage.text, currentLoggedInUser!!))
                    }
                }

                rv_chat_log.scrollToPosition(groupieAdapter.itemCount - 1)
            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })
    }
}
