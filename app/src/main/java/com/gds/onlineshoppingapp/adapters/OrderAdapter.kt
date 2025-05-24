package com.gds.onlineshoppingapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gds.onlineshoppingapp.ProductDescriptionActivity
import com.gds.onlineshoppingapp.R
import com.gds.onlineshoppingapp.models.Cart
import com.gds.onlineshoppingapp.models.Product

class OrderAdapter(private val carts: List<Cart>) : RecyclerView.Adapter<OrderAdapter.ViewHolder>(),
    OrderItemsAdapter.OnProductClickListener {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order_card, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return carts.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(carts[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cartIdTextView: TextView = itemView.findViewById(R.id.cartIdTextView)
        private val cartDateTextView: TextView = itemView.findViewById(R.id.cartDateTextView)
        private val cartItemsRecyclerView: RecyclerView = itemView.findViewById(R.id.cartItemsRecyclerView)
        private val totalAmountTextView: TextView = itemView.findViewById(R.id.totalAmountTextView)
        private val expandRetractButton: Button = itemView.findViewById(R.id.expandRetractButton)

        fun bind(cart: Cart) {
            cartIdTextView.text = cart.id.toString()
            cartDateTextView.text = cart.date

            // Setup cart items RecyclerView
            val cartItemsAdapter = OrderItemsAdapter(cart.products, this@OrderAdapter)
            cartItemsRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
            cartItemsRecyclerView.adapter = cartItemsAdapter

            // Calculate and display the total amount for the current cart
            val totalAmount = cart.products.sumOf { it.quantity * it.product?.price!! }
            totalAmountTextView.text = itemView.context.getString(R.string.total_amount, totalAmount)

            // Set the visibility of cart items based on the isExpanded property
            cartItemsRecyclerView.visibility = if (cart.isExpanded) View.VISIBLE else View.GONE
            expandRetractButton.text = if (cart.isExpanded) "Retract" else "Expand"

            // Set click listener for the expand/retract button
            expandRetractButton.setOnClickListener {
                cart.isExpanded = !cart.isExpanded
                notifyItemChanged(adapterPosition)
            }
        }
    }

    override fun onProductClick(product: Product, context: Context) {
        val intent = Intent(context, ProductDescriptionActivity::class.java)
        intent.putExtra("product", product)
        context.startActivity(intent)
    }
}
