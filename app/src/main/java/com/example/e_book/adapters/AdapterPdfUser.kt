package com.example.e_book.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.example.e_book.filters.FilterPdfUser
import com.example.e_book.MyApplication
import com.example.e_book.activities.PdfDetailActivity
import com.example.e_book.databinding.RowPdfUserBinding
import com.example.e_book.models.ModelPdf

class AdapterPdfUser: RecyclerView.Adapter<AdapterPdfUser.HolderPdfUser>, Filterable {

    private var context: Context

    public var pdfArrayList: ArrayList<ModelPdf>

    private var filterList: ArrayList<ModelPdf>

    private lateinit var binding: RowPdfUserBinding

    private var filter: FilterPdfUser? = null

    constructor(context: Context, pdfArrayList: ArrayList<ModelPdf>) {
        this.context = context
        this.pdfArrayList = pdfArrayList
        this.filterList = pdfArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderPdfUser {
        binding = RowPdfUserBinding.inflate(LayoutInflater.from(context),parent,false)

        return HolderPdfUser(binding.root)
    }

    override fun onBindViewHolder(holder: HolderPdfUser, position: Int) {

        val model = pdfArrayList[position]
        val bookId = model.id
        val categoryId = model.categoryId
        val title = model.title
        val description = model.description
        val uid = model.uid
        val url = model.url
        val timestamp = model.timestamp

        val date = MyApplication.formatTimeStamp(timestamp)

        holder.titleTv.text = title
        holder.descriptionTv.text = description
        holder.dateTv.text = date

        MyApplication.loadCategory(categoryId, holder.categoryTv)

        MyApplication.loadPdfSize(url, title, holder.sizeTv)

        MyApplication.loadPdfFromUrlSinglePage(url, title, holder.pdfView, holder.progressBar, null)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, PdfDetailActivity::class.java)
            intent.putExtra("bookId", bookId)
            context.startActivity(intent)
            Animatoo.animateSlideUp(context)
        }
    }

    override fun getItemCount(): Int {
        return pdfArrayList.size
    }

    override fun getFilter(): Filter {
        if (filter == null){
            filter = FilterPdfUser(filterList,this)
        }
        return filter as FilterPdfUser
    }

    inner class HolderPdfUser(itemView: View): RecyclerView.ViewHolder(itemView){
        var pdfView = binding.pdfView
        var progressBar = binding.progressBar
        var titleTv = binding.titleTv
        var descriptionTv = binding.descriptionTv
        var categoryTv = binding.categoryTv
        var sizeTv = binding.sizeTv
        var dateTv = binding.dateTv
    }




}