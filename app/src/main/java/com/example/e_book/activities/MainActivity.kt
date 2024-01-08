package com.example.e_book.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.example.e_book.R
import com.example.e_book.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btn1.setOnClickListener {

            startActivity(Intent(this, LoginActivity::class.java))
            Animatoo.animateSlideLeft(this)
            finish()
        }

        binding.btn2.setOnClickListener {

            startActivity(Intent(this, DashBoardUserActivity::class.java))
            Animatoo.animateFade(this)
        }

    }
}