package com.example.servicespractice.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.servicespractice.dataclasses.TrackingListItem
import com.example.servicespractice.service.PriceService

class CryptoBroadcastReceiver(val currentPriceCallback: (price: String) -> Unit, val priceTargetCallback: (trackingListItem: TrackingListItem) -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            when (intent.action) {
                PriceService.CURRENT_PRICE_INTENT -> {
                    Log.d("BROADCAST", "CURRENT_PRICE_INTENT")
                    val currentPrice = intent.getStringExtra(PriceService.CURRENT_PRICE_INTENT_ARG)?: "Не удалось загрузить котировки"
                    currentPriceCallback(currentPrice)
                }
                PriceService.TARGET_PRICE_INTENT -> {
                    Log.d("BROADCAST", "TARGET_PRICE_INTENT")
                    val cryptoTicker = intent.getStringExtra(PriceService.TARGET_PRICE_INTENT_CRYPTO_ARG)?: ""
                    val fiatTicker = intent.getStringExtra(PriceService.TARGET_PRICE_INTENT_FIAT_ARG)?: ""
                    val targetPrice = intent.getStringExtra(PriceService.TARGET_PRICE_INTENT_TARGET_PRICE_ARG)?: ""

                    val trackingListItem = TrackingListItem(cryptoTicker, fiatTicker, targetPrice)
                    priceTargetCallback(trackingListItem)
                }
            }
        }
    }
}