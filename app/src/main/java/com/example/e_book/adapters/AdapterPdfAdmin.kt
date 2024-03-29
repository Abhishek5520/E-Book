package com.example.e_book.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.example.e_book.filters.FilterPdfAdmin
import com.example.e_book.MyApplication
import com.example.e_book.activities.PdfDetailActivity
import com.example.e_book.activities.PdfEditActivity
import com.example.e_book.databinding.RoePdfAdminBinding
import com.example.e_book.models.ModelPdf

class AdapterPdfAdmin : RecyclerView.Adapter<AdapterPdfAdmin.HolderPdfAdmin>, Filterable {

    private var context: Context

    public var pdfArrayList: ArrayList<ModelPdf>

    private val filterList: ArrayList<ModelPdf>

    private lateinit var binding: RoePdfAdminBinding

    private var filter : FilterPdfAdmin? = null

    constructor(context: Context, pdfArrayList: ArrayList<ModelPdf>) : super() {
        this.context = context
        this.pdfArrayList = pdfArrayList
        this.filterList = pdfArrayList
    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderPdfAdmin {

        binding = RoePdfAdminBinding.inflate(LayoutInflater.from(context), parent, false)

        return HolderPdfAdmin(binding.root)
    }

    override fun onBindViewHolder(holder: HolderPdfAdmin, position: Int) {


        val model = pdfArrayList[position]
        val pdfId = model.id
        val categoryId = model.categoryId
        val title = model.title
        val description = model.description
        val pdfUrl = model.url
        val timestamp = model.timestamp

        val formattedDate = MyApplication.formatTimeStamp(timestamp)

        holder.titleTv.text = title
        holder.descriptionTv.text = description
        holder.dateTv.text = formattedDate

        MyApplication.loadCategory(categoryId, holder.categoryTv)

        MyApplication.loadPdfSize(pdfUrl, title, holder.sizeTv)

        MyApplication.loadPdfFromUrlSinglePage(
            pdfUrl,
            title,
            holder.pdfView,
            holder.progressBar,
            null
        )

        holder.moreBtn.setOnClickListener {
            moreOptionsDialog(model, holder)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, PdfDetailActivity::class.java)
            intent.putExtra("bookId", pdfId)
            context.startActivity(intent)
            Animatoo.animateSlideUp(context)
        }
    }

    private fun moreOptionsDialog(model: ModelPdf, holder: HolderPdfAdmin) {
        val bookId = model.id
        val bookUrl = model.url
        val bookTitle = model.title

        val options = arrayOf("Edit", "Delete")

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Choose Option")
            .setItems(options){dialog, position->
                if (position == 0){
                    val intent = Intent(context, PdfEditActivity::class.java)
                    intent.putExtra("bookId", bookId)
                    context.startActivity(intent)
                    Animatoo.animateSlideLeft(context)
                }
                else if (position == 1){
                    MyApplication.deleteBook(context, bookId, bookUrl, bookTitle)

                }

            }
            .show()
    }

    override fun getItemCount(): Int {
        return pdfArrayList.size
    }

    override fun getFilter(): Filter {
        if (filter == null){
            filter = FilterPdfAdmin(filterList, this)
        }
        return filter as FilterPdfAdmin
    }

    inner class HolderPdfAdmin(itemView: View) : RecyclerView.ViewHolder(itemView){

        val pdfView = binding.pdfView
        val progressBar = binding.progressBar
        val titleTv = binding.titleTv
        val descriptionTv = binding.descriptionTv
        val categoryTv = binding.categoryTv
        val sizeTv = binding.sizeTv
        val dateTv = binding.dateTv
        val moreBtn = binding.moreBtn
    }
}