package com.example.gettingquotes

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var btcUsdTextView: TextView
    private lateinit var btcEurTextView: TextView
    private lateinit var etcUsdTextView: TextView
    private lateinit var etcEurTextView: TextView
    private lateinit var button: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btcUsdTextView = findViewById(R.id.BTC_USD)
        btcEurTextView = findViewById(R.id.BTC_EUR)
        etcUsdTextView = findViewById(R.id.ETH_USD)
        etcEurTextView = findViewById(R.id.ETH_EUR)

        button = findViewById(R.id.button)

        button.setOnClickListener {
            updateCrypto()
        }
    }

    private fun updateCrypto() {
        GlobalScope.launch(Dispatchers.IO) {
            val url =
                "https://min-api.cryptocompare.com/data/pricemulti?fsyms=BTC,ETH&tsyms=USD,EUR"
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            try {
                client.newCall(request).execute().use { response ->
                    val responseData = response.body?.string()
                    val json = JSONObject(responseData)
                    val btcUsd = json.getJSONObject("BTC").getString("USD")
                    val btcEur = json.getJSONObject("BTC").getString("EUR")
                    val ethUsd = json.getJSONObject("ETH").getString("USD")
                    val ethEur = json.getJSONObject("ETH").getString("EUR")

                    withContext(Dispatchers.Main) {
                        btcUsdTextView.text = "BTC/USD: $btcUsd"
                        btcEurTextView.text = "BTC/EUR: $btcEur"
                        etcUsdTextView.text = "ETH/USD: $ethUsd"
                        etcEurTextView.text = "ETH/EUR: $ethEur"
                    }
                }
            } catch (e: IOException) {
                Toast.makeText(
                    applicationContext,
                    "Failed to retrieve data!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}