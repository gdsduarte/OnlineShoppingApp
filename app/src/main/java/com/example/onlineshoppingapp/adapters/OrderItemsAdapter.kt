package com.example.onlineshoppingapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.onlineshoppingapp.R
import com.example.onlineshoppingapp.models.CartItem
import com.example.onlineshoppingapp.models.Product
import com.squareup.picasso.Picasso

class OrderItemsAdapter(
    private val cartItems: List<CartItem>,
    private val onProductClickListener: OnProductClickListener) :
    RecyclerView.Adapter<OrderItemsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(cartItems[position])
    }

    override fun getItemCount(): Int = cartItems.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        private val productNameTextView: TextView = itemView.findViewById(R.id.productNameTextView)
        private val productQuantityTextView: TextView = itemView.findViewById(R.id.productQuantityTextView)
        private val productPriceTextView: TextView = itemView.findViewById(R.id.productPriceTextView)
        private val productImageView: ImageView = itemView.findViewById(R.id.productImageView)

        fun bind(cartItem: CartItem) {
            productNameTextView.text = cartItem.product?.title
            productQuantityTextView.text = "Quantity: ${cartItem.quantity}"
            productPriceTextView.text = "Price: ${cartItem.product?.price}"

            // Load the image using Picasso
            cartItem.product?.image?.let {
                Picasso.get()
                    .load(it)
                    .into(productImageView)
            }


        }

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            cartItems[adapterPosition].product?.let { product ->
                onProductClickListener.onProductClick(product, itemView.context)
            }
        }
    }

    interface OnProductClickListener {
        fun onProductClick(product: Product, context: Context)
    }
}
