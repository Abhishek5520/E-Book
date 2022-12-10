package com.example.e_book.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.bumptech.glide.Glide
import com.example.e_book.MyApplication
import com.example.e_book.R
import com.example.e_book.adapters.AdapterPdfFavorite
import com.example.e_book.databinding.ActivityProfileBinding
import com.example.e_book.models.ModelPdf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var booksArrayList: ArrayList<ModelPdf>
    private lateinit var adapterPdfFavorite: AdapterPdfFavorite

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        loadUserInfo()
        loadFavoriteBooks()

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.profileEditBtn.setOnClickListener {
            startActivity(Intent(this,ProfileEditActivity::class.java))
            Animatoo.animateSlideLeft(this)
        }
    }

    private fun loadUserInfo() {

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val email = "${snapshot.child("email").value}"
                val name = "${snapshot.child("name").value}"
                val profileImage = "${snapshot.child("profileImage").value}"
                val timestamp = "${snapshot.child("timestamp").value}"
                val uid = "${snapshot.child("uid").value}"
                val userType = "${snapshot.child("userType").value}"

                val formattedDate = MyApplication.formatTimeStamp(timestamp.toLong())

                binding.nameTv.text = name
                binding.emailTv.text = email
                binding.memberDateTv.text = formattedDate
                binding.accountTypeTv.text = userType.uppercase()

                try {
                    Glide.with(this@ProfileActivity)
                        .load(profileImage)
                        .placeholder(R.drawable.ic_person_gray)
                        .into(binding.profileIv)
                }
                catch (e:Exception){

                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun loadFavoriteBooks(){

        booksArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!).child("Favorites")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    booksArrayList.clear()
                    for (ds in snapshot.children){
                        val bookId = "${ds.child("bookId").value}"

                        val modelPdf = ModelPdf()
                        modelPdf.id = bookId

                        booksArrayList.add(modelPdf)
                    }

                    binding.favoriteBookCountTv.text = "${booksArrayList.size}"
                    adapterPdfFavorite = AdapterPdfFavorite(this@ProfileActivity, booksArrayList)
                    binding.favoriteRv.adapter = adapterPdfFavorite
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

    }

    override fun onBackPressed() {
        super.onBackPressed()
        Animatoo.animateSlideUp(this)
        finish()
    }
}