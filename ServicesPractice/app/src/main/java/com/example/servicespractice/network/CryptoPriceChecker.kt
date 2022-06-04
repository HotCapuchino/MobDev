package com.example.servicespractice.network

import android.util.Log
import com.example.servicespractice.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.InputStream
import java.net.URL
import java.util.*

class CryptoPriceChecker {

    fun requestCryptoPrice(cryptoTicker: String, fiatTicker: String): String? {
        val urlReconstructed = "${MainViewModel.CRYPTO_URL}${MainViewModel.CRYPTO_PARAM_STRING}${cryptoTicker}&${MainViewModel.FIAT_PARAM_STRING}${fiatTicker}"
        val result = Scanner((URL(urlReconstructed).content as InputStream)).nextLine()
        Log.d("Request result", result)
        return if (result.isNotEmpty() && result.isNotBlank()) { parseRate(result, fiatTicker) } else { null }
    }

    private fun parseRate(jsonString: String, fiatTicker: String): String {
        try {
            return JSONObject(jsonString).getDouble(fiatTicker).toString()
        } catch (e: Exception) {
            Log.e("CryptoPriceChecker", "", e)
        }
        return ""
    }
}