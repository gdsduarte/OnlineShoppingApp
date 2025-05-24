package com.gds.onlineshoppingapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.gds.onlineshoppingapp.helpers.AppCart
import com.gds.onlineshoppingapp.helpers.SharedPreferencesHelper
import com.gds.onlineshoppingapp.models.Cart
import com.gds.onlineshoppingapp.models.CartItem
import com.gds.onlineshoppingapp.models.Product
import com.squareup.picasso.Picasso
import java.io.Serializable

class ProductDescriptionActivity : AppCompatActivity() {

    private lateinit var selectedProduct: Product
    private lateinit var cart: Cart
    private var productQuantity: Int = 1
    private val sharedPreferencesHelper by lazy { SharedPreferencesHelper.getInstance(this) }

    @SuppressLint("MissingInflatedId")
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
        findViewById<TextView>(R.id.product_price).text = String.format("Price: €%.2f", selectedProduct.price)
        findViewById<TextView>(R.id.product_description).text = selectedProduct.description
        findViewById<TextView>(R.id.product_category).text = selectedProduct.category
        findViewById<TextView>(R.id.product_rating).text = String.format("Rating: %.1f★ (%d)", selectedProduct.rating.rate, selectedProduct.rating.count)

        val backButton = findViewById<Button>(R.id.product_backButton)
        backButton.setOnClickListener {
            finish()
        }

        // Load product image using Picasso
        Picasso.get()
            .load(selectedProduct.image)
            .placeholder(R.drawable.ic_launcher_background)
            .into(findViewById<ImageView>(R.id.product_image))

        setupQuantityControls()
    }

    private fun setupQuantityControls() {
        val decreaseQuantityButton: Button = findViewById(R.id.decreaseQuantityButton)
        val increaseQuantityButton: Button = findViewById(R.id.increaseQuantityButton)
        val quantityTextView: TextView = findViewById(R.id.quantityTextView)
        val addToCart: Button = findViewById(R.id.addToCart)

        // Display the current productQuantity
        quantityTextView.text = productQuantity.toString()

        // Decrease the productQuantity by 1
        decreaseQuantityButton.setOnClickListener {
            if (productQuantity > 1) {
                productQuantity--
                quantityTextView.text = productQuantity.toString()
            }
        }

        // Increase the productQuantity by 1
        increaseQuantityButton.setOnClickListener {
            productQuantity++
            quantityTextView.text = productQuantity.toString()
        }

        // Add the product to the cart
        addToCart.setOnClickListener {

            // Check if the product is already in the cart
            val existingCartItem = AppCart.cart.products.find { it.product!!.id == selectedProduct.id }

            // Initialize cartItem with either the existing cart item or a new cart item
            val cartItem: CartItem = if (existingCartItem != null) {
                // If the product is in the cart, update the quantity
                AppCart.updateQuantity(existingCartItem.productId, productQuantity)
                existingCartItem.copy(quantity = productQuantity)
            } else {
                // If the product is not in the cart, add it as a new item
                CartItem(
                    productId = selectedProduct.id,
                    quantity = productQuantity,
                    product = selectedProduct,
                    productQuantity = productQuantity
                ).also { newItem ->
                    AppCart.addItem(newItem)
                }
            }

            // Save the updated cart to SharedPreferences
            sharedPreferencesHelper.saveCart(AppCart.cart)
            sharedPreferencesHelper.saveProductQuantity(selectedProduct.id, productQuantity)

            // Store the selected quantity in the productQuantities map
            AppCart.productQuantities[selectedProduct.id] = productQuantity

            // Return the modified cartItem to the previous activity
            val returnIntent = Intent().apply {
                putExtra("modifiedCartItem", cartItem as Serializable)
            }

            setResult(RESULT_OK, returnIntent)
            finish()
        }
    }
}


