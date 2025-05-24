package com.gds.onlineshoppingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.Toast
import com.gds.onlineshoppingapp.helpers.FakeStoreApiClient
import com.gds.onlineshoppingapp.helpers.SharedPreferencesHelper
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignupActivity : AppCompatActivity() {

    lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    lateinit var fakeStoreApiClient: FakeStoreApiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Initialize shared preferences helper and fake store api client
        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this)
        fakeStoreApiClient = FakeStoreApiClient()

        // Initialize views and buttons
        val regUsername = findViewById<TextInputLayout>(R.id.regUsername)
        val regEmail = findViewById<TextInputLayout>(R.id.regEmail)
        val regPassword = findViewById<TextInputLayout>(R.id.regPassword)
        val regConfirmPassword = findViewById<TextInputLayout>(R.id.regConfirmPassword)
        val regSignup = findViewById<Button>(R.id.regRegister_btn)
        val regLogin = findViewById<Button>(R.id.regLogin_btn)

        // Login button click listener
        regLogin.setOnClickListener {
            val intent = Intent(this@SignupActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Generate a random token for the user as the Fake Store API does not have a register endpoint
        fun generateRandomToken(): String {
            val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
            return (1..32).map { allowedChars.random() }.joinToString("")
        }

        // Register button click listener
        regSignup.setOnClickListener {
            val username = regUsername.editText?.text.toString()
            val email = regEmail.editText?.text.toString()
            val password = regPassword.editText?.text.toString()
            val confirmPassword = regConfirmPassword.editText?.text.toString()

            // Validate username, email, password, and confirm password
            if (username.isEmpty() || !username.matches(Regex("^[a-zA-Z0-9_]+$"))) {
                Toast.makeText(this, "Invalid username", Toast.LENGTH_SHORT).show()
            } else if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show()
            } else if (password.isEmpty() || password.length < 6) {
                Toast.makeText(this, "Password too short", Toast.LENGTH_SHORT).show()
            } else if (confirmPassword != password) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else {
                // Register user and save user in shared preferences
                CoroutineScope(Dispatchers.Main).launch {
                    val userTokenResponse = fakeStoreApiClient.registerUser(username, email, password)
                    if (userTokenResponse != null) {
                        val token = generateRandomToken()
                        sharedPreferencesHelper.saveUserToken(token)

                        Toast.makeText(this@SignupActivity, "Signup successful", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@SignupActivity, HomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@SignupActivity, "Error signing up", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}

