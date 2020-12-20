package com.niipo.uwemessanger

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        create_account.setOnClickListener {

            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        btn_login.setOnClickListener {
            loginWithFirebase()
        }
    }

    private fun loginWithFirebase() {

        val email = edit_text_email.text.toString()
        val password = edit_text_password.text.toString()

        if (email.isEmpty()) {
            Toast.makeText(this, "Email can not be empty", Toast.LENGTH_LONG).show()
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Password can not be empty", Toast.LENGTH_LONG).show()
        }

        mAuth!!.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        // When it is successfully created account
                        Toast.makeText(this, "User signed In with success", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, LatestMessagesActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                }
                .addOnFailureListener {
                    // When it is successfully created account
                    Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_LONG).show()
                }
    }
}
