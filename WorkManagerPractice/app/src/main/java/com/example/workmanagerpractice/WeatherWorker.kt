package com.example.workmanagerpractice

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.io.InputStream
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit

class CryptoWorker(context: Context, workerParams: WorkerParameters): Worker(context, workerParams) {

    override fun doWork(): Result {
        val tickers = inputData.getStringArray("tickers")

        tickers?.let {
           for (ticker in it) {
               val cryptoURL = "https://min-api.cryptocompare.com/data/price?fsym=${ticker}&tsyms=USD"
               val stream = URL(cryptoURL).getContent() as InputStream
               val data = Scanner(stream).nextLine()
               Log.d("DATA", data.toString())
           }
       }

        return Result.success()
    }
}