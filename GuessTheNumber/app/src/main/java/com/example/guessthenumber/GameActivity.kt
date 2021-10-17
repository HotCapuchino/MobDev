package com.example.guessthenumber

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class GameActivity : AppCompatActivity() {

    private lateinit var questionTextView: TextView
    private var startInterval: Int = 0
    private var endInterval: Int = 0
    lateinit var yesButton: Button
    lateinit var noButton: Button
    var goldenAverage: Int = 0
    lateinit var mainMenuButton: Button

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        startInterval = intent.getIntExtra("start", 0)
        endInterval = intent.getIntExtra("end", 0)

        yesButton = findViewById(R.id.yes_btn)
        noButton = findViewById(R.id.no_btn)
        questionTextView = findViewById(R.id.question)
        mainMenuButton = findViewById(R.id.go_to_main_menu)

        Log.d("start", startInterval.toString())
        Log.d("end", startInterval.toString())

        if (startInterval == endInterval) {
            Toast.makeText(this, "Seems like you haven't entered any numbers!", Toast.LENGTH_LONG).show()
            endGame()
        } else if (endInterval - startInterval <= 2) {
            endGame()
            questionTextView.text = "Your number is ${endInterval - 1}"
        } else {
            goldenAverage = endInterval - (endInterval - startInterval) / 2
            setQuestion()
        }
    }

    private fun endGame() {
        yesButton.visibility = View.GONE
        noButton.visibility = View.GONE
        mainMenuButton.visibility = View.VISIBLE
    }

    private fun setQuestion() {
        val newText = getString(R.string.question_gr) + " " + goldenAverage.toString() + "?"
        questionTextView.text = newText
    }


    fun onConfirmationClick(view: View) {
        if (view.id == R.id.yes_btn) {
            startInterval = goldenAverage
            goldenAverage = endInterval - (endInterval - startInterval) / 2
            setQuestion()
        } else {
            endInterval = goldenAverage
            goldenAverage = endInterval - (endInterval - startInterval) / 2
            setQuestion()
        }
        Log.d("difference", (endInterval - startInterval).toString())
        if (endInterval - startInterval <= 2) {
            endGame()
            questionTextView.text = "Your number is ${endInterval - 1}"
        }
    }

    fun goToMainMenu(view: View) {
        this.finish()
        return
    }

}