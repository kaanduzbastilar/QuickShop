package com.kaanduzbastilar.quickshop.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kaanduzbastilar.quickshop.model.ProductModel
import com.kaanduzbastilar.quickshop.R

class RecyclerViewAdapter(private val dataArrayList: ArrayList<ProductModel>, private val context: Context) :
    RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(productModel: ProductModel)
    }

    private var onItemClickListener : OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener){
        this.onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        // Inflate Layout
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.main_card_layout, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        // Set the data to textview and imageview.
        val productModel: ProductModel = dataArrayList[position]

        holder.productsText.text = productModel.title

        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(productModel)}

    }

    override fun getItemCount(): Int {
        // this method returns the size of recyclerview
        return dataArrayList.size
    }

    // View Holder Class to handle Recycler View.
    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var productsText: TextView = itemView.findViewById(R.id.productsText)
    }
}
