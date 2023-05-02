package com.example.onlineshoppingapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.onlineshoppingapp.R
import com.example.onlineshoppingapp.models.Product
import com.squareup.picasso.Picasso

class ProductAdapter(private val products: List<Product>, private val onProductClick: (Product) -> Unit) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.product_title)
        val imageView: ImageView = view.findViewById(R.id.product_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.title.text = product.title

        // Load the image using an image loading library such as Glide or Picasso
        // For example, using Picasso:
        Picasso.get()
            .load(product.image)
            .into(holder.imageView)

        holder.itemView.setOnClickListener {
            onProductClick(product)
        }
    }

    override fun getItemCount() = products.size
}