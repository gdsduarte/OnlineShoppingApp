package com.example.onlineshoppingapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.onlineshoppingapp.R
import com.example.onlineshoppingapp.models.CartItem
import com.example.onlineshoppingapp.models.Product

class CartAdapter(
    private var cartItems: List<CartItem>,
    private val onQuantityChanged: (CartItem, Int) -> Unit
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
            holder.bind(currentItem, product, onQuantityChanged)
        }
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemNameTextView: TextView = itemView.findViewById(R.id.itemNameTextView)
        private val itemQuantityTextView: TextView = itemView.findViewById(R.id.itemQuantityTextView)
        private val itemPriceTextView: TextView = itemView.findViewById(R.id.itemPriceTextView)
        private val itemTotalCostTextView: TextView = itemView.findViewById(R.id.itemTotalCostTextView)

        fun bind(cartItem: CartItem, product: Product, onQuantityChanged: (CartItem, Int) -> Unit) {
            itemNameTextView.text = product.title
            itemQuantityTextView.text = String.format("Quantity: %d", cartItem.quantity)
            itemPriceTextView.text = String.format("$%.2f", product.price)

            val totalCost = cartItem.quantity * product.price
            itemTotalCostTextView.text = String.format("Total: $%.2f", totalCost) // Add this line

            itemView.setOnClickListener {
                onQuantityChanged(cartItem, cartItem.quantity)
            }
        }
    }

    fun updateCartItems(newCartItems: List<CartItem>) {
        cartItems = newCartItems
        notifyDataSetChanged()
    }
}
