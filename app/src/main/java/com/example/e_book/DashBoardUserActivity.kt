package com.example.e_book

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.example.e_book.databinding.ActivityDashBoardUserBinding
import com.google.firebase.auth.FirebaseAuth

class DashBoardUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashBoardUserBinding

    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashBoardUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        binding.logoutBtn.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this,MainActivity::class.java))
            Animatoo.animateSlideRight(this)
            finish()
        }
    }

    private fun checkUser() {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null){
            binding.subTitleTv.text = "Not Logged In"
        }
        else{
            val email = firebaseUser.email
            binding.subTitleTv.text = email
        }
    }

    override fun onBackPressed() {
        val firebaseUser = firebaseAuth.currentUser
        super.onBackPressed()
        if(firebaseUser == null){
            startActivity(Intent(this,MainActivity::class.java))
            Animatoo.animateSlideRight(this)
            finish()
        }
        else{
            finish()
        }

    }
}