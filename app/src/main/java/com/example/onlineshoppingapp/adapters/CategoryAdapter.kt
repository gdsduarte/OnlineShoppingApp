package com.example.onlineshoppingapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.onlineshoppingapp.R
import com.example.onlineshoppingapp.models.Category

class CategoryAdapter(
    private val categories: List<Category>,
    private val onCategoryClickListener: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(category: Category) {
            view.findViewById<TextView>(R.id.categoryName).text = category.name
            view.setOnClickListener { onCategoryClickListener(category) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int {
        return categories.size
    }
}
