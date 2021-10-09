package com.example.randomfilm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var movies: Array<String>
    private lateinit var randomMovieTextView: TextView
    lateinit var shownMovies: MutableSet<String>
    lateinit var randomFilm: Number

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        movies = resources.getStringArray(R.array.movies_array)
        randomFilm = Random().nextInt(movies.size)
        randomMovieTextView = findViewById(R.id.randomFilm)
        randomMovieTextView.text = movies[randomFilm as Int]
        shownMovies = mutableSetOf(movies[randomFilm as Int])
    }

    fun onReset(view: View) {
        randomMovieTextView.text = ""
        shownMovies = mutableSetOf()
    }

    fun onNext(view: View) {
        if (shownMovies.size == movies.size) {
            Toast.makeText(this, "Все фильмы уже были показаны, нажмите на кнопку сброса!", Toast.LENGTH_LONG).show()
            return
        }
        while (true) {
            randomFilm = Random().nextInt(movies.size)
            if (!shownMovies.contains(movies[randomFilm as Int])) {
                shownMovies.add(movies[randomFilm as Int])
                randomMovieTextView.text = movies[randomFilm as Int]
                break
            }
        }
    }
}