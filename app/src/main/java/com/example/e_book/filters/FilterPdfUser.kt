package com.example.e_book.filters

import android.widget.Filter
import com.example.e_book.adapters.AdapterPdfUser
import com.example.e_book.models.ModelPdf

class FilterPdfUser: Filter {

    var filterList: ArrayList<ModelPdf>

    var adapterPdfUser: AdapterPdfUser

    constructor(filterList: ArrayList<ModelPdf>, adapterPdfUser: AdapterPdfUser) : super() {
        this.filterList = filterList
        this.adapterPdfUser = adapterPdfUser
    }


    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var constraint: CharSequence? = constraint
        val results = FilterResults()

        if (constraint != null && constraint.isNotEmpty()){
            constraint = constraint.toString().uppercase()
            var filteredModels = ArrayList<ModelPdf>()
            for (i in filterList.indices){

                if (filterList[i].title.uppercase().contains(constraint)){
                    filteredModels.add(filterList[i])
                }
            }
            results.count = filteredModels.size
            results.values = filteredModels
        }
        else{
            results.count = filterList.size
            results.values = filterList
        }

        return results
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults) {

        adapterPdfUser.pdfArrayList = results.values as ArrayList<ModelPdf>/* = java.util.ArrayList<com.example.e_book.models.ModelPdf> */

        adapterPdfUser.notifyDataSetChanged()
    }
}