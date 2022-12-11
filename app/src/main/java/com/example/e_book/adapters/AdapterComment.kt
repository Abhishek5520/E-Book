package com.example.e_book.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.e_book.MyApplication
import com.example.e_book.R
import com.example.e_book.databinding.RowCommentBinding
import com.example.e_book.models.ModelComment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdapterComment: RecyclerView.Adapter<AdapterComment.HolderComment> {

    val context: Context

    val commentArrayList: ArrayList<ModelComment>

    private lateinit var binding: RowCommentBinding

    private lateinit var firebaseAuth: FirebaseAuth

    constructor(context: Context, contentArrayList: ArrayList<ModelComment>) {
        this.context = context
        this.commentArrayList = contentArrayList

        firebaseAuth = FirebaseAuth.getInstance()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderComment {

        binding = RowCommentBinding.inflate(LayoutInflater.from(context), parent,false)

        return HolderComment(binding.root)
    }

    override fun onBindViewHolder(holder: HolderComment, position: Int) {


        val model = commentArrayList[position]

        val id = model.id
        val uid = model.uid
        val timestamp = model.timestamp
        val bookId = model.bookId
        val comment = model.comment

        val date = MyApplication.formatTimeStamp(timestamp.toLong())

        holder.commentTv.text = comment
        holder.dateTv.text = date

        if (firebaseAuth.currentUser != null && firebaseAuth.uid == uid){
            holder.deleteBtn.visibility = View.VISIBLE
        }
        else{
            holder.deleteBtn.visibility = View.GONE
        }

        loadUserDetails(model,holder)

        holder.deleteBtn.setOnClickListener {

            if (firebaseAuth.currentUser != null && firebaseAuth.uid == uid){
                deleteCommentDialog(model,holder)
            }
        }
    }

    private fun deleteCommentDialog(model: ModelComment, holder: HolderComment) {

        val  builder = AlertDialog.Builder(context)
        builder.setTitle("Delete Comment")
            .setMessage("Are you sure you want to delete this comment?")
            .setPositiveButton("DELETE"){d,e->

                val bookId = model.bookId
                val commentId = model.id

                val ref = FirebaseDatabase.getInstance().getReference("Books")
                ref.child(bookId).child("Comments").child(commentId)
                    .removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(context, "Comment deleted...", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e->
                        Toast.makeText(context, "Failed to delete comment due to ${e.message}", Toast.LENGTH_SHORT).show()
                    }

            }
            .setNegativeButton("CANCEL"){d,e->
                d.dismiss()
            }
            .show()
    }

    private fun loadUserDetails(model: ModelComment, holder: HolderComment) {

        val uid = model.uid
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    val name = "${snapshot.child("name").value}"
                    val profileImage = "${snapshot.child("profileImage").value}"


                    holder.nameTv.text = name
                    try {
                        Glide.with(context)
                            .load(profileImage)
                            .placeholder(R.drawable.ic_person_gray)
                            .into(holder.profileTv)
                    }
                    catch (e:Exception){
                        holder.profileTv.setImageResource(R.drawable.ic_person_gray)
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    override fun getItemCount(): Int {
        return commentArrayList.size
    }


    inner class HolderComment(itemView: View): RecyclerView.ViewHolder(itemView){

        val profileTv = binding.profileIv
        val nameTv = binding.nameTv
        val dateTv = binding.dateTv
        val commentTv = binding.commentTv
        val deleteBtn = binding.deleteBtn

    }


}