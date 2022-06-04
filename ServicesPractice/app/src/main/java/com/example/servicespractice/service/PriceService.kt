package com.example.servicespractice.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.servicespractice.R
import com.example.servicespractice.dataclasses.TrackingList
import com.example.servicespractice.dataclasses.TrackingListItem
import com.example.servicespractice.network.CryptoPriceChecker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class PriceService: Service() {
    private val cryptoPriceChecker = CryptoPriceChecker()
    private val channel: NotificationChannel? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        NotificationChannel(CHANNEL_ID, "CryptoNotifications", NotificationManager.IMPORTANCE_DEFAULT)
    } else {
        null
    }
    private var trackingCrypto: String = ""
    private var trackingFiat: String = ""
    private val trackingList: TrackingList = TrackingList(mutableListOf())
    private val previousPrices: MutableMap<TrackingListItem, Double> = mutableMapOf()

    private val runnablePriceChecker: Thread = Thread {
        while (true) {
            Log.d("PRICE CHECKER", "$trackingCrypto, $trackingFiat")
            val currentPrice = cryptoPriceChecker.requestCryptoPrice(trackingCrypto, trackingFiat)
            Log.d("CRYPTO_PRICE", currentPrice.toString())
            val currentPriceIntent = Intent(CURRENT_PRICE_INTENT)
            currentPriceIntent.putExtra(CURRENT_PRICE_INTENT_ARG, currentPrice)
            sendBroadcast(currentPriceIntent)
            Thread.sleep(3_000)
        }
    }

    private val runnablePriceTarget: Thread = Thread {
        while (true) {
            Log.d("TRACKING LIST SIZE", trackingList.trackingList.size.toString())
            val elementsToDelete = TrackingList(mutableListOf())
            trackingList.trackingList.forEach {
                if (requestAndCheckPrice(it)) {
                    Log.d("TARGET PRICE", "${it.cryptoTicker} reached ${it.price} ${it.fiatTicker}")
                    val (cryptoTicker, fiatTicker, targetPrice) = it
                    val targetPriceIntent = Intent(TARGET_PRICE_INTENT).apply {
                        putExtra(TARGET_PRICE_INTENT_CRYPTO_ARG, cryptoTicker)
                        putExtra(TARGET_PRICE_INTENT_FIAT_ARG, fiatTicker)
                        putExtra(TARGET_PRICE_INTENT_TARGET_PRICE_ARG, targetPrice)
                    }
                    elementsToDelete.trackingList.add(it)
                    sendBroadcast(targetPriceIntent)
                } else {
                    Log.d("TARGET PRICE", "${it.cryptoTicker} has not reached ${it.fiatTicker}")
                }
            }
            elementsToDelete.trackingList.forEach {
                trackingList.trackingList.remove(it)
                previousPrices.remove(it)
            }
            Thread.sleep(5_000)
        }
    }

    private fun requestAndCheckPrice(item: TrackingListItem): Boolean {
        val (cryptoTicker, fiatTicker, targetPrice) = item
        val currentPrice = cryptoPriceChecker.requestCryptoPrice(cryptoTicker, fiatTicker)?.toDouble()?: 0.0
        val previousPrice = previousPrices[item]

        if (previousPrice != null) {

            val min = min(previousPrice, currentPrice)
            val max = max(previousPrice, currentPrice)
            val diff = abs(previousPrice - currentPrice)
            Log.d("MIN PRICE", min.toString())
            Log.d("MAX PRICE", max.toString())
            Log.d("MAX > TARGET", "${max > targetPrice.toDouble()}")
            Log.d("MIN < TARGET", "${min < targetPrice.toDouble()}")
            Log.d("DIFFERENCE", diff.toString())

            if ((min < targetPrice.toDouble()) && (max > targetPrice.toDouble()) && ((min + diff >= targetPrice.toDouble()) || (max - diff <= targetPrice.toDouble()))) {
                Log.d("TARGET", "REACHED!!!")
                if (channel != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)

                        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                            .setSmallIcon(R.drawable.stocks)
                            .setContentTitle("$cryptoTicker price")
                            .setContentText("$cryptoTicker reached $targetPrice $fiatTicker")
                            .setPriority(NotificationCompat.PRIORITY_HIGH)

                        builder.setVibrate(LongArray(0))
                        with(NotificationManagerCompat.from(this)) {
                            notify(7, builder.build())
                        }
                    }
                }
                return true
            } else {
                return false
            }
        }
        previousPrices[item] = currentPrice
        return false

//        if (currentPrice >= targetPrice.toDouble()) {
//            if (channel != null) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
//
//                    val builder = NotificationCompat.Builder(this, CHANNEL_ID)
//                        .setSmallIcon(R.drawable.stocks)
//                        .setContentTitle("$cryptoTicker price")
//                        .setContentText("$cryptoTicker reached $targetPrice $fiatTicker")
//                        .setPriority(NotificationCompat.PRIORITY_HIGH)
//
//                    builder.setVibrate(LongArray(0))
//                    with(NotificationManagerCompat.from(this)) {
//                        notify(7, builder.build())
//                    }
//                }
//            }
//            return true
//        }
//        return false
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val cryptoTicker = intent?.extras?.getString(CRYPTO_TICKER)?: ""
        val fiatTicker = intent?.extras?.getString(FIAT_TICKER)?: ""
        val targetPrice = intent?.extras?.getString(TARGET_PRICE)?: ""

        if (targetPrice.isEmpty()) {
            trackingCrypto = cryptoTicker
            trackingFiat = fiatTicker
        } else {
            val trackingListItem = TrackingListItem(cryptoTicker, fiatTicker, targetPrice)
            val foundElem = trackingList.trackingList.find { it == trackingListItem }
            val index = trackingList.trackingList.indexOf(foundElem)

            if (index >= 0) {
                trackingList.trackingList.removeAt(index)
            } else {
                trackingList.trackingList.add(trackingListItem)
            }
        }

        if (!runnablePriceChecker.isAlive) {
            runnablePriceChecker.start()
        }
        if (!runnablePriceTarget.isAlive) {
            runnablePriceTarget.start()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        runnablePriceChecker.join()
        runnablePriceTarget.join()
        super.onDestroy()
    }

    companion object {
        const val CHANNEL_ID = "RateCheckService"
        const val CRYPTO_TICKER = "CRYPTO_TICKER"
        const val TARGET_PRICE = "TARGET_PRICE"
        const val FIAT_TICKER = "FIAT_TICKER"
        // broadcast variables
        const val CURRENT_PRICE_INTENT = "CURRENT_PRICE_CHANGE"
        const val CURRENT_PRICE_INTENT_ARG = "FIAT_PRICE"
        const val TARGET_PRICE_INTENT = "TARGET_PRICE_INTENT"
        const val TARGET_PRICE_INTENT_CRYPTO_ARG = "CRYPTO_TICKER"
        const val TARGET_PRICE_INTENT_FIAT_ARG = "FIAT_TICKER"
        const val TARGET_PRICE_INTENT_TARGET_PRICE_ARG = "TARGET_PRICE"

        fun startService(context: Context, cryptoTicker: String, fiatTicker: String, targetPrice: String? = null) {
            context.startService(Intent(context, PriceService::class.java).apply {
                putExtra(CRYPTO_TICKER, cryptoTicker)
                putExtra(FIAT_TICKER, fiatTicker)
                putExtra(TARGET_PRICE, targetPrice)
            })
        }

        fun stopService(context: Context) {
            context.stopService(Intent(context, PriceService::class.java))
        }
    }
}