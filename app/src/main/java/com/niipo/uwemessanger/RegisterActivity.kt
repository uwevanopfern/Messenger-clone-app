package com.niipo.uwemessanger

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import kotlinx.android.synthetic.main.activity_register.*
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
import com.niipo.uwemessanger.model.User
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*


class RegisterActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null

    private val requestCodePhoto = 13245
    private var photoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()

        btn_register.setOnClickListener {

            createFirebaseAccount()
        }

        already_having_account.setOnClickListener {

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        btn_photo_selection.setOnClickListener {

            //Photo selector intent
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, requestCodePhoto)
        }
    }

    //Return selected photo in the intent above
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == requestCodePhoto && resultCode == Activity.RESULT_OK && data != null) {
            Log.d("RegisterActivity", "Photo is found successfully")
            //Check actually what is really the photo
            //uri represents the location of image is stored in your device
            photoUri = data.data

            //Get access of image in uri as bitmap
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, photoUri)

            //Display image in button of btn_photo_selection
            //By default image is hidden in image view lib
            select_photo_img_view.setImageBitmap(bitmap)

            //Make image visible in our image view do the following
            //This btn_photo_selection.alpha = 0f will make 0 float or set background btn invisible
            //Make button hidden
            btn_photo_selection.alpha = 0f


//            val bitmapDrawable = BitmapDrawable(bitmap)
//            btn_photo_selection.setBackgroundDrawable(bitmapDrawable)
        } else {
            Log.d("RegisterActivity", "Photo is not found , try again")
        }
    }

    private fun createFirebaseAccount() {

        var username = edit_text_username.text.toString()
        var email = edit_text_email.text.toString()
        var password = edit_text_password.text.toString()

        Log.d("RegisterActivity", username)

        when {

            username.isEmpty() -> Toast.makeText(this, "Username can not be empty", Toast.LENGTH_LONG).show()
            email.isEmpty() -> Toast.makeText(this, "Email can not be empty", Toast.LENGTH_LONG).show()
            password.isEmpty() -> Toast.makeText(this, "Password can not be empty", Toast.LENGTH_LONG).show()
            else -> mAuth!!.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            // When it is successfully created account
                            Toast.makeText(this, "User with ${it.result!!.user.email} created successfully", Toast.LENGTH_LONG).show()

                            uploadImageToFirebaseStorage()
                        }
                    }
                    .addOnFailureListener {
                        // When it is successfully created account
                        Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_LONG).show()
                    }
        }

    }

    private fun uploadImageToFirebaseStorage() {
        if (photoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(photoUri!!)
                .addOnSuccessListener {
                    Log.d("RegisterActivity", "Successfully uploaded on firebase : ${it.metadata?.path}")

                    //Download image url and have it so that will be able to save it in database
                    ref.downloadUrl.addOnSuccessListener {
                        Log.d("RegisterActivity", "File Location: $it")

                        saveUserToFirebaseDatabase(it.toString())
                    }
                }
                .addOnFailureListener {
                    //TODO : later and later
                }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {

        val uid = FirebaseAuth.getInstance().uid
                ?: "" // may return null that is ? means (!! not nullable)
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        var user = User(uid, edit_text_username.text.toString(), profileImageUrl)

        ref.setValue(user)
                .addOnSuccessListener {
                    Log.d("RegisterActivity", "User saved in firebase database")
                    val intent = Intent(this, LatestMessagesActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    //TODO
                    Log.d("RegisterActivity", "User failed in firebase database")
                }
    }
}
