package com.example.servicespractice.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.servicespractice.R
import com.example.servicespractice.dataclasses.TrackingList

class PriceTrackingAdapter(var trackingList: TrackingList, private val callback: (position: Int) -> Unit) :
    RecyclerView.Adapter<PriceTrackingItem>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PriceTrackingItem {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.crypto_recycler_item, parent, false)
        return PriceTrackingItem(view, callback)
    }

    override fun onBindViewHolder(holder: PriceTrackingItem, position: Int) {
        val trackingPosition = trackingList.trackingList[position]

        holder.cryptoTickerField.text = trackingPosition.cryptoTicker
        holder.fiatPriceField.text = trackingPosition.price + " " + trackingPosition.fiatTicker
        holder.holderPosition = position
    }

    override fun getItemCount(): Int {
        return trackingList.trackingList.size
    }
}