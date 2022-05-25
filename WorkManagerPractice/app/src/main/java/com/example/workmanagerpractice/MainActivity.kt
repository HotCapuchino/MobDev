package com.example.workmanagerpractice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.work.*

class MainActivity : AppCompatActivity() {
    val cryptoTickers: Array<String> = listOf("BTC", "ETH", "SOL", "UST").toTypedArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val workRequests: ArrayList<OneTimeWorkRequest> = ArrayList()

        val workCitiesData = workDataOf("tickers" to cryptoTickers)
        for (i in 0..cryptoTickers.size) {
            workRequests.add(
                OneTimeWorkRequest.Builder(CryptoWorker::class.java).
                setInputData(workCitiesData)
                .build()
            )
        }

        WorkManager.getInstance(this)
            .beginWith(workRequests[0]).also {
                for (i in 0 until workRequests.size) {
                    it.then(workRequests[i])
                }
                it.enqueue()
            }
    }
}