package com.example.servicespractice.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.servicespractice.dataclasses.TrackingList
import com.example.servicespractice.dataclasses.TrackingListItem
import com.example.servicespractice.network.CryptoPriceChecker
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    val cryptoTicker = MutableLiveData<String>()
    val fiatTicker = MutableLiveData<String>()
    val cryptoPrice = MutableLiveData<String>()
    val trackingList = MutableLiveData<TrackingList>()

    fun checkIfCryptoToFiatIsTracking(desiredPrice: String): Int {
        val trackingListItem = TrackingListItem(
            this.cryptoTicker.value?: INITIAL_CRYPTO_TICKER,
            this.fiatTicker.value?: INITIAL_FIAT_TICKER,
            desiredPrice
        )
        return findPosition(trackingListItem)
    }

    fun checkIfCryptoToFiatIsTracking(trackingListItem: TrackingListItem): Int {
        return findPosition(trackingListItem)
    }

    private fun findPosition(trackingListItem: TrackingListItem): Int {
        val foundElem = this.trackingList.value?.trackingList?.find { it == trackingListItem}
        if (foundElem != null) {
            return this.trackingList.value?.trackingList?.indexOf(foundElem)?: -1
        }
        return -1
    }

    fun removeTrackingPosition(position: Int): Boolean {
        if (trackingList.value?.trackingList?.removeAt(position) != null) {
            trackingList.value = trackingList.value?.trackingList?.let { TrackingList(it) }
            Log.d("DELETING", "position deleted")
            return true
        }
        Log.d("DELETING", "position not deleted")
        return false
    }

    fun addTrackingPosition(price: String) {
        val trackingListItem = TrackingListItem(
            this.cryptoTicker.value?: INITIAL_CRYPTO_TICKER,
            this.fiatTicker.value?: INITIAL_FIAT_TICKER,
            price
        )
        Log.d("trackingListItem", trackingListItem.toString())
        trackingList.value?.trackingList?.add(trackingListItem)
        Log.d("INSERTED", trackingList.value?.trackingList.toString())
        trackingList.value = trackingList.value?.trackingList?.let { TrackingList(it) }
    }

    companion object {
        const val TAG = "MainViewModel"
        const val CRYPTO_URL = "https://min-api.cryptocompare.com/data/price?"
        const val CRYPTO_PARAM_STRING = "fsym="
        const val FIAT_PARAM_STRING = "tsyms="
        const val INITIAL_CRYPTO_TICKER = "BTC"
        const val INITIAL_FIAT_TICKER = "USD"
        // this is helper for updating recycler view
    }
}