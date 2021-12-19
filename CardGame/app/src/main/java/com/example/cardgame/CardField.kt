package com.example.cardgame

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import kotlin.random.Random

class CardField(context: Context?): View(context) {
    private val cards: ArrayList<Card> = ArrayList()
    private val openedColor: Int = Color.rgb(255, 215, 0)
    private val closedColor: Int = Color.rgb(187, 108, 138)
    private val rowAmount = 4
    private val columnAmount = 4
    private val cardSize = 200f
    private val gap = 25f
    private val p = Paint()
    private var gameOver = false
    private var wasInitialized = false

    private fun initialize(canvas: Canvas?) {
        var opened = 0
        var closed = 0
        val shouldBeOpened: Int = (this.rowAmount * this.columnAmount * 0.2).toInt()
        val shouldBeClosed: Int = (this.rowAmount * this.columnAmount * 0.5).toInt()

        var paddingTop = 0f
        var paddingLeft = 0f

        if (canvas != null) {
            paddingTop = (canvas.height - (this.rowAmount * this.cardSize + this.rowAmount * this.gap)) / 2
            paddingLeft = (canvas.width - (this.columnAmount * this.cardSize + this.columnAmount * this.gap)) / 2
        }

        for (i in 0 until this.rowAmount) {
            for (j in 0 until this.columnAmount) {
                var color: Int
                if (Random.nextInt(0, 2) == 1) {
                    color = this.openedColor
                    opened++
                } else {
                    color = this.closedColor
                    closed++
                }
                val newCard = Card(
                    i + 1,
                    j + 1,
                    color,
                    paddingLeft + this.cardSize * j + this.gap,
                    paddingTop + this.cardSize * i + this.gap,
                    paddingLeft + this.cardSize * (j + 1f),
                    paddingTop + this.cardSize * (i + 1f))
                this.cards.add(newCard)
            }
        }

        if (opened < shouldBeOpened) {
            this.activateMissingCards(opened, shouldBeOpened, this.openedColor)
        }
        if (closed < shouldBeClosed) {
            this.activateMissingCards(closed, shouldBeClosed, this.closedColor)
        }
    }

    private fun activateMissingCards(active: Int, shouldBeActivated: Int, activationColor: Int) {
        var activeCards = active
        val visited: ArrayList<Array<Int>> = ArrayList()
        while (activeCards < shouldBeActivated) {
            val randomRow = Random.nextInt(1, this.rowAmount + 2)
            val randomColumn = Random.nextInt(1, this.columnAmount + 2)
            if (visited.find { it -> it[0] == randomRow && it[1] == randomColumn } != null) {
                continue
            }

            val card = this.cards.firstOrNull{ it -> it.row == randomRow && it.column == randomColumn }
            val cardIndex = this.cards.indexOf(card)
            if (cardIndex >= 0 && card != null) {
                card.color = activationColor
                this.cards[cardIndex] = card
                activeCards++
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        if (!wasInitialized) {
            this.initialize(canvas)
            this.wasInitialized = true
        }
        canvas?.drawColor(resources.getColor(R.color.cardview_dark_background))
        for (i in 0 until this.cards.size) {
            p.color = this.cards[i].color
            canvas?.drawRect(
                this.cards[i].topLeftX,
                this.cards[i].topLeftY,
                this.cards[i].bottomRightX,
                this.cards[i].bottomRightY,
                p
            )
        }
        this.checkForWinning()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (gameOver) {
            return false
        }
        if (event != null && event.action == MotionEvent.ACTION_UP) {
            var foundCard: Card? = null
            for (card in this.cards) {
                if (event.x <= card.bottomRightX && event.x >= card.topLeftX && event.y >= card.topLeftY && event.y <= card.bottomRightY) {
                    foundCard = card
                    break
                }
            }

            if (foundCard != null) {
                for (card in this.cards) {
                    if (card.row == foundCard.row || card.column == foundCard.column) {
                        card.color = if (card.color == openedColor) closedColor else openedColor
                    }
                }
            }
            invalidate()
        }

        return true
    }

    private fun checkForWinning() {
        var opened = 0
        var closed = 0
        for (card in this.cards) {
            if (card.color == closedColor) {
                closed++
            } else {
                opened++
            }
        }
        if (opened == this.cards.size || closed == this.cards.size) {
            Toast.makeText(context, "You've won!!!", Toast.LENGTH_LONG).show()
            gameOver = true
        }
    }
}