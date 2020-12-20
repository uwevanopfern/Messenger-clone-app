package com.niipo.uwemessanger.Adapters

import android.annotation.SuppressLint
import com.google.firebase.auth.FirebaseAuth
import com.niipo.uwemessanger.R
import com.niipo.uwemessanger.model.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class UserItem(val user: User) : Item<ViewHolder>() {

    @SuppressLint("SetTextI18n")
    override fun bind(viewHolder: ViewHolder, position: Int) {

        val loggedInUser = FirebaseAuth.getInstance().uid
        var userUuid = user.uid

        if (loggedInUser != userUuid) {
            viewHolder.itemView.txt_username.text = user.username
            Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.img_view)
        } else {
            viewHolder.itemView.txt_username.text = user.username + " (You)"
            Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.img_view)
        }


    }

    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }
}