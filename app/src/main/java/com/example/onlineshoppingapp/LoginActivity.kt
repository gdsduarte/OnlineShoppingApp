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

    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var fakeStoreApiClient: FakeStoreApiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val logUsername = findViewById<TextInputLayout>(R.id.username)
        val logPassword = findViewById<TextInputLayout>(R.id.password)
        val login = findViewById<Button>(R.id.login_btn)
        val register = findViewById<Button>(R.id.register_btn)

        sharedPreferencesHelper = SharedPreferencesHelper(this)
        fakeStoreApiClient = FakeStoreApiClient()

        // Check if the user is already logged in
        if (sharedPreferencesHelper.getUserToken() != null) {
            navigateToHomeActivity()
        }

        login.setOnClickListener {
            val username = logUsername.editText?.text.toString()
            val password = logPassword.editText?.text.toString()

            loginUser(username, password)
        }

        register.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignupActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun loginUser(username: String, password: String) {
        if (!isInputValid(username, password)) {
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            val token = fakeStoreApiClient.loginUser(username, password)
            if (token != null) {
                sharedPreferencesHelper.saveUserToken(token)
                val user = fakeStoreApiClient.getUser(token.token)
                if (user != null) {
                    sharedPreferencesHelper.saveUserId(user.id)
                    navigateToHomeActivity()
                } else {
                    Toast.makeText(this@LoginActivity, "Login Failed", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@LoginActivity, "Error logging in user", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun isInputValid(username: String, password: String): Boolean {
        if (username.isEmpty() || !username.matches(Regex("^[a-zA-Z0-9_]+$"))) {
            Toast.makeText(this, "Invalid username", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.isEmpty() || password.length < 6) {
            Toast.makeText(this, "Invalid password", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun navigateToHomeActivity() {
        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

}