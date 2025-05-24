package com.gds.onlineshoppingapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gds.onlineshoppingapp.R
import com.gds.onlineshoppingapp.models.Product
import com.squareup.picasso.Picasso

class ProductAdapter(private val products: List<Product>, private val onProductClick: (Product) -> Unit) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.product_title)
        val price: TextView = view.findViewById(R.id.product_price)
        val rating: TextView = view.findViewById(R.id.product_rating)
        val imageView: ImageView = view.findViewById(R.id.product_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.title.text = product.title
        holder.price.text = String.format("Price: €%.2f", product.price)
        holder.rating.text = String.format("Rating: %.1f★ (%d)", product.rating.rate, product.rating.count)

        Picasso.get()
            .load(product.image)
            .into(holder.imageView)

        holder.itemView.setOnClickListener {
            onProductClick(product)
        }
    }

    override fun getItemCount() = products.size
}
