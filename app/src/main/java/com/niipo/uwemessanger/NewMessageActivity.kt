package com.niipo.uwemessanger

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.niipo.uwemessanger.Adapters.UserItem
import com.niipo.uwemessanger.model.User
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*

class NewMessageActivity : AppCompatActivity() {

    companion object {
        var USER_KEY = "USER_KEY"
    }

    val groupieAdapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.title = "Select User"

        fetchFirebaseUsers()
    }

    private fun fetchFirebaseUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                Log.d("NewMessageActivity", p0.toString())
                p0.children.forEach {
                    Log.d("NewMessageActivity", it.toString())

                    val user = it.getValue(User::class.java)
                    groupieAdapter.add(UserItem(user!!))
                }

                groupieAdapter.setOnItemClickListener { item, view ->

                    //Parcelable helps to pass whole object of class between activity
                    val userItem = item as UserItem

                    val intent = Intent(view.context, ChatLogActivity::class.java)
                    intent.putExtra(USER_KEY, userItem.user)
                    startActivity(intent)
                    finish()
                }

                rv_new_message.adapter = groupieAdapter
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}


