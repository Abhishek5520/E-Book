package com.example.e_book

import android.app.ActionBar
import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.e_book.databinding.ActivityPdfDetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.FileOutputStream
import java.util.jar.Manifest

class PdfDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfDetailBinding

    private companion object{
        const val TAG = "BOOK_DETAIL_TAG"
    }

    private var bookId = ""

    private var bookTitle = ""
    private var bookUrl = ""

    private lateinit var progressDialog: ProgressDialog

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        bookId = intent.getStringExtra("bookId")!!

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        MyApplication.incrementBookViewCount(bookId)
        loadBookDetails()

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.readBookBtn.setOnClickListener {
            val intent = Intent(this, PdfViewActivity::class.java)
            intent.putExtra("bookId",bookId)
            startActivity(intent)
        }

        binding.downloadBookBtn.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                Log.d(TAG, "onCreate: STORAGE PERMISSION is already granted")
                downloadBook()
            }
            else{
                Log.d(TAG, "onCreate: STORAGE PERMISSION was not granted, Lets request it")
                requestStoragePermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    private val requestStoragePermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){isGranted:Boolean ->
        if (isGranted){
            Log.d(TAG, "onCreate: STORAGE PERMISSION is granted")
            downloadBook()
        }
        else{
            Log.d(TAG, "onCreate: STORAGE PERMISSION is denied")
            Toast.makeText(this,"Permission denied", Toast.LENGTH_SHORT).show()
        }

    }

    private fun downloadBook(){
        Log.d(TAG, "downloadBook: Downloading Book")
        progressDialog.setMessage("Downloading Book")
        progressDialog.show()

        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(bookUrl)
        storageReference.getBytes(Constants.MAX_BYTES_PDF)
            .addOnSuccessListener {bytes ->
                Log.d(TAG, "downloadBook: Book downloaded...")
                saveToDownloadFolder(bytes)
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Log.d(TAG, "downloadBook: Failed to download book due to ${e.message}")
                Toast.makeText(this,"Failed to download book due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveToDownloadFolder(bytes: ByteArray?) {
        Log.d(TAG, "saveToDownloadFolder: saving downloaded book")
        val nameWithExtension = "${System.currentTimeMillis()}.pdf"

        try {
            val downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            downloadsFolder.mkdirs()

            val filePath = downloadsFolder.path +"/"+nameWithExtension

            val out = FileOutputStream(filePath)
            out.write(bytes)
            out.close()

            Toast.makeText(this,"Saved to Downloads Folder", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "saveToDownloadFolder: Saved to Downloads Folder")
            progressDialog.dismiss()
            incrementDownloadCount()
        }
        catch (e:Exception){
            progressDialog.dismiss()
            Log.d(TAG, "incrementDownloadCount: failed to save due to ${e.message}")
            Toast.makeText(this,"Failed to save due to ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun incrementDownloadCount() {
        Log.d(TAG, "incrementDownloadCount: ")

        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child(bookId)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    var downloadsCount = "${snapshot.child("downloadsCount").value}"
                    Log.d(TAG, "onDataChange: Current Downloads Count: $downloadsCount")

                    if (downloadsCount == "" || downloadsCount == "null"){
                        downloadsCount = "0"
                    }

                    val newDownloadsCount: Long = downloadsCount.toLong()+1
                    Log.d(TAG, "onDataChange: New Downloads Count: $newDownloadsCount")

                    val hashMap: HashMap<String, Any> = HashMap()
                    hashMap["downloadsCount"] = newDownloadsCount

                    val dbRef = FirebaseDatabase.getInstance().getReference("Books")
                    dbRef.child(bookId)
                        .updateChildren(hashMap)
                        .addOnSuccessListener {
                            Log.d(TAG, "onDataChange: Downloads count incremented")
                        }
                        .addOnFailureListener {e->
                            Log.d(TAG, "onDataChange: FAILED to increment due to ${e.message}")
                        }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun loadBookDetails() {

        val ref = FirebaseDatabase.getInstance().getReference("Books")
            .ref.child(bookId)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    val categoryId = "${snapshot.child("categoryId").value}"
                    val description = "${snapshot.child("description").value}"
                    val downloadCount = "${snapshot.child("downloadsCount").value}"
                    val timestamp = "${snapshot.child("timestamp").value}"
                    bookTitle = "${snapshot.child("title").value}"
                    val uid = "${snapshot.child("uid").value}"
                    bookUrl = "${snapshot.child("url").value}"
                    var viewsCount = "${snapshot.child("viewsCount").value}"

                    if (viewsCount == "" || viewsCount == "null"){
                        viewsCount = "0"
                    }

                    val date = MyApplication.formatTimeStamp(timestamp.toLong())

                    MyApplication.loadCategory(categoryId, binding.categoryTv)

                    MyApplication.loadPdfFromUrlSinglePage("$bookUrl","$bookTitle",binding.pdfView,binding.progressBar,binding.pagesTv)

                    MyApplication.loadPdfSize("$bookUrl","$bookTitle",binding.sizeTv)

                    binding.titleTv.text = bookTitle
                    binding.descriptionTv.text = description
                    binding.viewsTv.text = viewsCount
                    binding.downloadsTv.text = downloadCount
                    binding.dateTv.text = date
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }
}