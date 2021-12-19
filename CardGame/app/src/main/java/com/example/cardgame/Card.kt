package com.example.cardgame

import android.graphics.Color
import android.graphics.Point

data class Card(val row: Int, val column: Int,  var color: Int, val topLeftX: Float, val topLeftY: Float, val bottomRightX: Float, val bottomRightY: Float)