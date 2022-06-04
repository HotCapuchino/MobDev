package com.example.servicespractice.dataclasses

import java.io.Serializable


class TrackingListItem(val cryptoTicker: String, val fiatTicker: String, val price: String) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (other is TrackingListItem) {
            return other.cryptoTicker == this.cryptoTicker && other.fiatTicker == this.fiatTicker && other.price == this.price
        }
        return false
    }

    override fun toString(): String {
        return "(Crypto ticker: ${cryptoTicker}, fiat ticker: ${fiatTicker}, target price: ${price})"
    }

    operator fun component1(): String = cryptoTicker
    operator fun component2(): String = fiatTicker
    operator fun component3(): String = price
}

data class TrackingList(val trackingList: MutableList<TrackingListItem>)