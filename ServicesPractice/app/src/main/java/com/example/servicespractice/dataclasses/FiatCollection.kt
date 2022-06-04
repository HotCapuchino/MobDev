package com.example.servicespractice.dataclasses

data class Fiat(val name: String, val ticker: String, val sign: String)

data class FiatCollection(val fiats: Array<Fiat>)