package com.example.onlineshoppingapp

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.Toast
import com.example.onlineshoppingapp.models.Token
import com.example.onlineshoppingapp.models.User
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
import org.json.JSONObject

class SignupActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val regUsername = findViewById<TextInputLayout>(R.id.regUsername)
        val regEmail = findViewById<TextInputLayout>(R.id.regEmail)
        val regPassword = findViewById<TextInputLayout>(R.id.regPassword)
        val regConfirmPassword = findViewById<TextInputLayout>(R.id.regConfirmPassword)
        val regSignup = findViewById<Button>(R.id.regRegister_btn)
        val regLogin = findViewById<Button>(R.id.regLogin_btn)

        sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)

        regSignup.setOnClickListener {
            val username = regUsername.editText?.text.toString()
            val email = regEmail.editText?.text.toString()
            val password = regPassword.editText?.text.toString()
            val confirmPassword = regConfirmPassword.editText?.text.toString()

            registerUser(username, email, password, confirmPassword)
        }

        regLogin.setOnClickListener {
            val intent = Intent(this@SignupActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun registerUser(username: String, email: String, password: String, confirmPassword: String) {

        if (!isInputValid(username, email, password, confirmPassword)) {
            return
        }

        val client = OkHttpClient()
        val requestBody = FormBody.Builder()
            .add("username", username)
            .add("email", email)
            .add("password", password)
            .build()

        val request = Request.Builder()
            .url("https://fakestoreapi.com/users")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@SignupActivity, "Error signing up", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        // get the user from the response
                        val user = Gson().fromJson(response.body?.string(), User::class.java)

                        // save the user to shared preferences
                        val editor = sharedPreferences.edit()
                        editor.putString("user", Gson().toJson(user))
                        editor.apply()

                        // Show success message
                        Toast.makeText(this@SignupActivity, "Signup successful", Toast.LENGTH_SHORT).show()

                        // Navigate to the login screen
                        val intent = Intent(this@SignupActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@SignupActivity, "Error signing up", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun isInputValid(username: String, email: String, password: String, confirmPassword: String): Boolean {
        if (username.isEmpty() || !username.matches(Regex("^[a-zA-Z0-9_]+$"))) {
            Toast.makeText(this, "Invalid username", Toast.LENGTH_SHORT).show()
            return false
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.isEmpty() || password.length < 6) {
            Toast.makeText(this, "Invalid password", Toast.LENGTH_SHORT).show()
            return false
        }

        if (confirmPassword != password) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}
