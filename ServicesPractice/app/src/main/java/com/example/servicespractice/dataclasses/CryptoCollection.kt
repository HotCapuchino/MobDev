package com.example.servicespractice.dataclasses

data class Crypto(val name: String, val ticker: String)

data class CryptoCollection(val cryptos: Array<Crypto>)
