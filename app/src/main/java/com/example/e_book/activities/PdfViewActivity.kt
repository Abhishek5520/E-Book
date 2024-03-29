package com.example.e_book.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.example.e_book.Constants
import com.example.e_book.databinding.ActivityPdfViewBinding
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class PdfViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfViewBinding

    private companion object{
        const val TAG = "PDF_VIEW_TAG"
    }

    private var bookId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bookId = intent.getStringExtra("bookId")!!
        loadBookDetails()

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

    }

    private fun loadBookDetails() {
        Log.d(TAG, "loadBookDetails: Get Pdf URL from db...")

        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child(bookId)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    val pdfUrl = snapshot.child("url").value
                    Log.d(TAG, "onDataChange: PDF_URL $pdfUrl")

                    loadBookFromUrl("$pdfUrl")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Animatoo.animateSlideRight(this)
    }

    private fun loadBookFromUrl(pdfUrl: String) {
        Log.d(TAG, "loadBookFromUrl: Get Pdf from firebase storage using URL")

        val reference = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
        reference.getBytes(Constants.MAX_BYTES_PDF)
            .addOnSuccessListener {bytes ->
                Log.d(TAG, "loadBookFromUrl: pdf got from url")

                binding.pdfView.fromBytes(bytes)
                    .swipeHorizontal(false)//set false to scroll vertical, set true scroll horizontal
                    .scrollHandle(DefaultScrollHandle(this))
                    .onPageChange {page, pageCount ->
                        val currentPage = page+1
                        binding.toolbarSubtitleTv.text = "$currentPage/$pageCount"
                        Log.d(TAG, "loadBookFromUrl: $currentPage/$pageCount")
                    }
                    .onError { t->
                        Log.d(TAG, "loadBookFromUrl: ${t.message}")
                    }
                    .onPageError { page, t ->
                        Log.d(TAG, "loadBookFromUrl: ${t.message}")
                    }
                    .load()
                binding.progressBar.visibility = View.GONE
            }
            .addOnFailureListener {e ->
                Log.d(TAG, "loadBookFromUrl: Failed to get pdf due to ${e.message}")
                binding.progressBar.visibility = View.GONE
            }
    }
}

