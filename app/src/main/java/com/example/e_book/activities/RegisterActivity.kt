package com.example.e_book.activities

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.example.e_book.R
import com.example.e_book.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.backBtn.setOnClickListener {
            onBackPressed()
            Animatoo.animateSlideRight(this)
        }

        binding.registerBtn.setOnClickListener {

            validateData()
        }


    }

    private var name = ""
    private var email = ""
    private var password = ""

    private fun validateData() {
        name = binding.nameEt.text.toString().trim()
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()
        val cPassword = binding.confirmPasswordEt.text.toString().trim()

        if (name.isEmpty()){
            Toast.makeText(this,"Enter your name...",Toast.LENGTH_SHORT).show()
        }

        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this,"Invalid Email Pattern...",Toast.LENGTH_SHORT).show()
        }

        else if (password.isEmpty()){
            Toast.makeText(this,"Enter password...",Toast.LENGTH_SHORT).show()
        }

        else if (cPassword.isEmpty()){
            Toast.makeText(this,"Confirm password...",Toast.LENGTH_SHORT).show()
        }

        else if (password!=cPassword){
            Toast.makeText(this,"Password doesn't match...",Toast.LENGTH_SHORT).show()
        }

        else{
            createUserAccount()
        }
    }

    private fun createUserAccount() {
        progressDialog.setMessage("Creating Account...")
        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                updateUserInfo()
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(this,"Failed creating account due to ${e.message}",Toast.LENGTH_SHORT).show()
            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, LoginActivity::class.java))
        Animatoo.animateSlideRight(this)
        finish()

    }

    private fun updateUserInfo() {
        progressDialog.setMessage("Saving user info...")

        val timestamp = System.currentTimeMillis()

        val uid = firebaseAuth.uid

        val hashMap: HashMap<String,Any?> = HashMap()
        hashMap["uid"] = uid
        hashMap["email"] = email
        hashMap["name"] = name
        hashMap["profileImg"] = ""
        hashMap["userType"] = "user"
        hashMap["timestamp"] = timestamp

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this,"Account created...",Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@RegisterActivity, DashBoardUserActivity::class.java))
                Animatoo.animateFade(this@RegisterActivity)
                finish()
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(this,"Failed saving user info due to ${e.message}",Toast.LENGTH_SHORT).show()
            }
    }
}