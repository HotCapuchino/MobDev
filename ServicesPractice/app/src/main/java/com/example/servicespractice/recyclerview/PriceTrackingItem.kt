package com.example.servicespractice.recyclerview

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.servicespractice.R

class PriceTrackingItem(view: View, callback: (position: Int) -> Unit): RecyclerView.ViewHolder(view) {
    val cryptoTickerField: TextView = view.findViewById(R.id.crypto_ticker)
    val fiatPriceField: TextView = view.findViewById(R.id.crypto_target_price)
    private val removeTrackingBtn: Button = view.findViewById(R.id.remove_tracking)
    var holderPosition: Int = -1

    private val removeTrackingButtonClick = View.OnClickListener {
        callback(holderPosition)
    }

    init {
        removeTrackingBtn.setOnClickListener(removeTrackingButtonClick)
    }
}