package com.example.trabajocolaborativoadso1_3

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.trabajocolaborativoadso1_3.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
private lateinit var binding: ActivityMainBinding

    private var token: String? = null   // aquí guardaremos la "manilla"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            val response = RetrofitClient.api.getCurrentUser("Bearer $token")

            if (response.isSuccessful) {
                val user = response.body()
                val accessToken = user?.accessToken
            }
        }

        hacerLogin("emilys", "emilyspass")
    }


    private fun hacerLogin(usuario: String, clave: String) {
        // lifecycleScope.launch = ejecuta en una corrutina (sin congelar la app)
        lifecycleScope.launch {
            try {
                val resp = RetrofitClient.api.login(
                    LoginRequest(usuario, clave)
                )
                if (resp.isSuccessful) {
                    token = resp.body()?.accessToken   // ← guardamos el token
                    Log.d("API", "Token recibido: $token")
                    obtenerUsuario()                  // seguimos al GET
                } else {
                    Log.e("API", "Login falló: ${resp.code()}")
                }
            } catch (e: Exception) {
                Log.e("API", "Error de red: ${e.message}")
            }
        }
    }


    private fun obtenerUsuario() {
        val t = token ?: return
        lifecycleScope.launch {
            try {
                // ojo: el formato es "Bearer " + token
                val resp = RetrofitClient.api.getCurrentUser("Bearer $t")
                if (resp.isSuccessful) {
                    val user = resp.body()
                    Log.d("API", "Hola ${user?.firstName} - ${user?.email}")
                }
            } catch (e: Exception) {
                Log.e("API", "Error: ${e.message}")
            }
        }
    }
}