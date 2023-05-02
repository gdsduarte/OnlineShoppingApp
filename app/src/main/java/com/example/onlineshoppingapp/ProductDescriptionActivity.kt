package com.example.onlineshoppingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.onlineshoppingapp.helpers.AppCart
import com.example.onlineshoppingapp.helpers.SharedPreferencesHelper
import com.example.onlineshoppingapp.models.Cart
import com.example.onlineshoppingapp.models.CartItem
import com.example.onlineshoppingapp.models.Product
import com.squareup.picasso.Picasso

class ProductDescriptionActivity : AppCompatActivity() {

    private lateinit var selectedProduct: Product
    private lateinit var cart: Cart
    private var productQuantity: Int = 1
    private val sharedPreferencesHelper by lazy { SharedPreferencesHelper.getInstance(this) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_description)

        // Get the product details from the intent
        selectedProduct = intent.getSerializableExtra("product") as Product

        // Load the saved cart
        val savedCart = sharedPreferencesHelper.getCart()
        if (savedCart != null) {
            AppCart.cart = savedCart
        }

        // Get the cart object from the intent if exists, or create a new one
        cart = AppCart.cart

        // Get the previously selected quantity for the current product
        val existingCartItem = cart.products.find { it.product!!.id == selectedProduct.id }
        productQuantity = existingCartItem?.quantity ?: 1

        // Set product details
        findViewById<TextView>(R.id.product_title).text = selectedProduct.title
        findViewById<TextView>(R.id.product_price).text = String.format("Price: $%.2f", selectedProduct.price)
        findViewById<TextView>(R.id.product_description).text = selectedProduct.description
        findViewById<TextView>(R.id.product_category).text = selectedProduct.category

        // Load product image using Picasso
        Picasso.get()
            .load(selectedProduct.image)
            .placeholder(R.drawable.ic_launcher_background)
            .into(findViewById<ImageView>(R.id.product_image))

        // Set rating details
        findViewById<TextView>(R.id.product_rating).text = String.format("Rating: %.1f (%d)", selectedProduct.rating.rate, selectedProduct.rating.count)

        setupQuantityControls()
    }

    private fun setupQuantityControls() {
        val decreaseQuantityButton: Button = findViewById(R.id.decreaseQuantityButton)
        val increaseQuantityButton: Button = findViewById(R.id.increaseQuantityButton)
        val quantityTextView: TextView = findViewById(R.id.quantityTextView)
        val addToCart: Button = findViewById(R.id.addToCart)

        // Display the current productQuantity
        quantityTextView.text = productQuantity.toString()

        decreaseQuantityButton.setOnClickListener {
            if (productQuantity > 1) {
                productQuantity--
                quantityTextView.text = productQuantity.toString()
            }
        }

        increaseQuantityButton.setOnClickListener {
            productQuantity++
            quantityTextView.text = productQuantity.toString()
        }

        addToCart.setOnClickListener {

            // Check if the product is already in the cart
            val existingCartItem = AppCart.cart.products.find { it.product!!.id == selectedProduct.id }

            if (existingCartItem != null) {
                // If the product is in the cart, update the quantity
                AppCart.updateQuantity(existingCartItem.productId, productQuantity)
            } else {
                // If the product is not in the cart, add it as a new item
                val cartItem = CartItem(
                    productId = selectedProduct.id,
                    quantity = productQuantity,
                    product = selectedProduct,
                    productQuantity = productQuantity
                )

                AppCart.addItem(cartItem)
            }

            // Save the updated cart to SharedPreferences
            sharedPreferencesHelper.saveCart(AppCart.cart)
            sharedPreferencesHelper.saveProductQuantity(selectedProduct.id, productQuantity)

            // Store the selected quantity in the productQuantities map
            AppCart.productQuantities[selectedProduct.id] = productQuantity

            val intent = Intent(this@ProductDescriptionActivity, ProductActivity::class.java)
            startActivity(intent)
        }
    }
}

