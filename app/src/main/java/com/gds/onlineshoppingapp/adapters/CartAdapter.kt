package com.gds.onlineshoppingapp.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.RecyclerView
import com.gds.onlineshoppingapp.ProductDescriptionActivity
import com.gds.onlineshoppingapp.R
import com.gds.onlineshoppingapp.models.CartItem
import com.gds.onlineshoppingapp.models.Product
import com.squareup.picasso.Picasso

class CartAdapter(
    private var cartItems: List<CartItem>,
    private val onQuantityChanged: (CartItem, Int) -> Unit,
    private val modifyCartItemLauncher: ActivityResultLauncher<Intent>
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val currentItem = cartItems[position]
        val product = currentItem.product

        if (product != null) {
            holder.bind(currentItem, product, onQuantityChanged, modifyCartItemLauncher)
        }
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemCardView: View = itemView.findViewById(R.id.itemCardView)
        private val itemImageView: ImageView = itemView.findViewById(R.id.itemImageView)
        private val itemNameTextView: TextView = itemView.findViewById(R.id.itemNameTextView)
        private val itemQuantityTextView: TextView = itemView.findViewById(R.id.itemQuantityTextView)
        private val itemPriceTextView: TextView = itemView.findViewById(R.id.itemPriceTextView)
        private val itemTotalCostTextView: TextView = itemView.findViewById(R.id.itemTotalCostTextView)

        fun bind(
            cartItem: CartItem,
            product: Product,
            onQuantityChanged: (CartItem, Int) -> Unit,
            modifyCartItemLauncher: ActivityResultLauncher<Intent>
        ) {
            itemNameTextView.text = product.title
            itemQuantityTextView.text = String.format("Quantity: %d", cartItem.quantity)
            itemPriceTextView.text = String.format("€%.2f", product.price)

            // Load the image using Picasso
            product.image.let {
                Picasso.get()
                    .load(it)
                    .into(itemImageView)
            }

            val totalCost = cartItem.quantity * product.price
            itemTotalCostTextView.text = String.format("Total: €%.2f", totalCost)

            itemView.setOnClickListener {
                onQuantityChanged(cartItem, cartItem.quantity)
            }

            itemCardView.setOnClickListener {
                val intent = Intent(itemView.context, ProductDescriptionActivity::class.java).apply {
                    putExtra("product", product)
                    putExtra("fromCart", true)
                }
                modifyCartItemLauncher.launch(intent)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateCartItems(newCartItems: List<CartItem>) {
        cartItems = newCartItems
        notifyDataSetChanged()
    }
}


