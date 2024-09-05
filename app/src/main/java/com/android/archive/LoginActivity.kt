package com.android.archive

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.archive.constantes.ApiClient
import com.android.archive.constantes.Route
import com.android.archive.databinding.ActivityLoginBinding
import com.android.archive.models.room.User
import com.android.archive.models.volley.Singleton
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {


    @SuppressLint("CutPasteId")
    private lateinit var binding: ActivityLoginBinding
    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val modeConnexion = binding.modeConnexion

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val progressIndicator=binding.progressIndicator
        val buttonLogin = binding.buttonLogin
        buttonLogin.setOnClickListener{
            val username = binding.username.text.toString()
            val password = binding.password.text.toString()
            binding.buttonLogin.isEnabled = false
            binding.buttonLogin.text = "Loading..."
            progressIndicator.visibility = View.VISIBLE
            val user = User(0,username, password)
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                if (user.username.isEmpty() || user.password.isEmpty() || user.username.isBlank() || user.password.isBlank()){
                    showSnackbar("Please enter your credentials")
                    resetLoginButton()
                }else{
                    login(user)
                }
            }, 50)
        }
    }

    fun apiUrl(params:String,child:String):String = params + child
    
    fun login(user: User) {
        val data = JSONObject()
        val url="${ApiClient.BASE_URL}/${Route.login}"
        data.put("UserName", user.username)
        data.put("Password", user.password)
        println("eth ${data}")

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, data,
            { response ->
                // Handle the response
                val success = response.getInt("status")
                if (success==200) {
                    println("$response eeeeeeee")
                    // Navigate to HomeActivity
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish() // Close LoginActivity
                } else {
                    showSnackbar("Login failed: ${response.getString("message")}")
                }
                resetLoginButton()
            },
            { error ->
                println("Erreur : ${error.networkResponse?.statusCode}")
                error.networkResponse?.data?.let { data ->
                    val errorResponse = String(data)
                    try {
                        // Convertir la chaîne JSON en JSONObject
                        val jsonObject = JSONObject(errorResponse)
                        // Récupérer la valeur associée à la clé "message"
                        val message = jsonObject.getString("message")
                        // Afficher le message dans le Snackbar
                        showSnackbar(message)
                    } catch (e: JSONException) {
                        // Gérer le cas où le JSON n'est pas valide
                        showSnackbar("Erreur de format de réponse : $errorResponse")
                    }
                }

                resetLoginButton()
            }
        )
        Singleton.getInstance(applicationContext).addToRequestQueue(jsonObjectRequest)
    }

    private fun resetLoginButton() {
        binding.buttonLogin.isEnabled = true
        binding.buttonLogin.text = "Login"
        binding.progressIndicator.visibility = View.GONE
    }

    private fun showSnackbar(message: String) {
        // Create and customize the Snackbar
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
        snackbar.setBackgroundTint(Color.parseColor("#871911")) // Change background color
        snackbar.setTextColor(Color.WHITE)
        snackbar.show()
    }
}