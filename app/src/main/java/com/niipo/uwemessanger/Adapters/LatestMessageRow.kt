package com.niipo.uwemessanger.Adapters

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.niipo.uwemessanger.R
import com.niipo.uwemessanger.model.ChatMessage
import com.niipo.uwemessanger.model.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.latest_message_row.view.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class LatestMessageRow(val chatMessage: ChatMessage) : Item<ViewHolder>() {

    var chatPartnerUser: User? = null

    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.itemView.txt_latest_message.text = chatMessage.text

        val chatPartnerId: String

        if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
            chatPartnerId = chatMessage.toId
        } else {
            chatPartnerId = chatMessage.fromId
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                chatPartnerUser = p0.getValue(User::class.java)

                val loggedInUser = FirebaseAuth.getInstance().uid
                var userUuid = chatPartnerUser!!.uid

                if (loggedInUser != userUuid) {
                    viewHolder.itemView.txt_latest_msg_username.text = chatPartnerUser!!.username
                } else {
                    viewHolder.itemView.txt_latest_msg_username.text = chatPartnerUser!!.username + " (You)"
                }

                Picasso.get().load(chatPartnerUser!!.profileImageUrl).into(viewHolder.itemView.img_view_profile)
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })


    }

    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }


}