package com.example.memorina

import android.content.res.TypedArray
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginLeft
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private var cards: ArrayList<Card> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(applicationContext).apply {
            orientation = LinearLayout.VERTICAL
            weightSum = 4f
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }

        this.cards = this.createField()
        this.cards.shuffle()

        val rows = ArrayList<LinearLayout>()
        for (i in 0 until 4) {
            val newLayout = LinearLayout(applicationContext)
            newLayout.apply {
                orientation = LinearLayout.HORIZONTAL
                weightSum = 4f
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                for (j in 0 until 4) {
                    val viewToAdd = cards[i * 4 + j].view
                    val viewLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
                    viewLayoutParams.setMargins(10, 0, 0, 10)

                    viewToAdd.apply {
                        layoutParams = viewLayoutParams
                        minimumWidth = newLayout.width / 4
                        minimumHeight = newLayout.height / 4
                    }
                    addView(viewToAdd)
                }
            }
            rows.add(newLayout)
        }
        for (row in rows) {
            layout.addView(row)
        }

        setContentView(layout)
    }

    private fun createField(): ArrayList<Card> {
        val cardImages = resources.obtainTypedArray(R.array.cards_images)
        val cards = ArrayList<Card>()
        val visited = ArrayList<Int>()

        val cardAmount = 8
        for (i in 1..cardAmount) {
            var randIndex = Random.nextInt(0, cardImages.length() - 1)
            while (true) {
                if (!visited.contains(randIndex)) {
                    break
                }
                randIndex = Random.nextInt(0, cardImages.length() - 1)
            }

            val drawableResource = cardImages.getResourceId(randIndex, -1)
            val backOfTheCard = R.drawable.question_mark
            if (drawableResource >= 0) {
                visited.add(randIndex)
            }

            val firstImage = ImageView(applicationContext).apply {
                setImageResource(backOfTheCard)
                setOnClickListener(cardClickListener)
                tag = i
            }
            val secondImage = ImageView(applicationContext).apply {
                setImageResource(backOfTheCard)
                setOnClickListener(cardClickListener)
                tag = i + cardAmount
            }

            val firstCard = Card(i, i + cardAmount, firstImage, drawableResource, backOfTheCard, CardStates.CLOSED)
            val secondCard = Card(i + cardAmount, i, secondImage, drawableResource, backOfTheCard, CardStates.CLOSED)

            cards.add(firstCard)
            cards.add(secondCard)
        }

        cardImages.recycle()
        return cards
    }

    private val cardClickListener: View.OnClickListener = View.OnClickListener { v: View? ->
        if (v != null) {
            val cardId = v.tag
            val clickedCard = this.cards.find { it -> it.id == cardId }

            if (clickedCard != null) {
                if (clickedCard.state == CardStates.CLOSED) {
                    clickedCard.view.setImageResource(clickedCard.frontImage)
                } else if (clickedCard.state == CardStates.OPENED) {
                    clickedCard.view.setImageResource(clickedCard.backImage)
                }
            }
        }
    }

//    private fun fillTheField(cards: ArrayList<Card>): LinearLayout {
//
//    }
}