package com.example.onlineshoppingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.onlineshoppingapp.helpers.FakeStoreApiClient
import com.example.onlineshoppingapp.helpers.SharedPreferencesHelper
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    lateinit var fakeStoreApiClient: FakeStoreApiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize shared preferences helper and fake store api client
        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this)
        fakeStoreApiClient = FakeStoreApiClient()

        // Setup views
        val logUsername = findViewById<TextInputLayout>(R.id.username)
        val logPassword = findViewById<TextInputLayout>(R.id.password)
        val login = findViewById<Button>(R.id.login_btn)
        val register = findViewById<Button>(R.id.register_btn)

        // Check if user is already logged in
        if (sharedPreferencesHelper.getUserToken() != null) {
            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Setup register button
        register.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignupActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Setup login button
        login.setOnClickListener {
            val username = logUsername.editText?.text.toString()
            val password = logPassword.editText?.text.toString()

            // Validate username and password
            if (username.isEmpty() || !username.matches(Regex("^[a-zA-Z0-9_]+$"))) {
                Toast.makeText(this, "Invalid username", Toast.LENGTH_SHORT).show()
            } else if (password.isEmpty() || password.length < 6) {
                Toast.makeText(this, "Invalid password", Toast.LENGTH_SHORT).show()
            } else {
                // Login user and save token and user id in shared preferences
                CoroutineScope(Dispatchers.Main).launch {
                    val token = fakeStoreApiClient.loginUser(username, password)
                    if (token != null) {
                        sharedPreferencesHelper.saveUserToken(token.toString())
                        val user = fakeStoreApiClient.getUser(token.token, username)
                        if (user != null) {
                            sharedPreferencesHelper.saveUserId(user.id)
                            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@LoginActivity, "Login Failed", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@LoginActivity, "Error logging in user", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
