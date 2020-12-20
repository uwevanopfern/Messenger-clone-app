package com.niipo.uwemessanger.Adapters

import com.niipo.uwemessanger.R
import com.niipo.uwemessanger.model.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class ChatFromItem(val text: String, val user: User) : Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.itemView.txt_from_username.text = text
        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.img_view_from)
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}