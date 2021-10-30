package com.example.randomfilm

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.util.*


class MainActivity : AppCompatActivity() {

    // views
    private lateinit var infoTextView: TextView
    private lateinit var movieNameView: TextView
    private lateinit var movieImageView: ImageView
    private lateinit var movieDirectorView: TextView
    private lateinit var movieActorsView: TextView
    private lateinit var movieBudgetView: TextView
    private lateinit var movieInfoLayout: LinearLayout
    // vatiables
    private lateinit var movies_collection: MoviesCollection
    lateinit var randomFilm: Number
    private lateinit var movies_storage: SharedPreferences
    // utilities
    private val gson = Gson()
    private val randomizer = Random()
    private val picasso = Picasso.get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        InitViews()
        movies_storage = this.getSharedPreferences("shown_movies", Context.MODE_PRIVATE)
        initMovies()
    }

    private fun InitViews() {
        infoTextView = findViewById(R.id.infoField)
        movieNameView = findViewById(R.id.filmName)
        movieImageView = findViewById(R.id.filmImage)
        movieDirectorView = findViewById(R.id.filmDirector)
        movieActorsView = findViewById(R.id.filmActors)
        movieBudgetView = findViewById(R.id.filmBudget)
        movieInfoLayout = findViewById(R.id.filmInfoLayout)
    }

    private fun initMovies() {
        val stream = resources.openRawResource(R.raw.movies)
        movies_collection = gson.fromJson(InputStreamReader(stream), MoviesCollection::class.java)
        if (movies_storage.all.keys.size == movies_collection.movies.size) {
            infoTextView.text = "Все фильмы уже были показаны!"
        } else {
           infoTextView.text = "Нажмите на кнопку 'Следуюший фильм', чтобы увидеть фильм"
        }
    }

    private fun getRandomMovie(): Movie? {
        if (movies_storage.all.keys.size == movies_collection.movies.size) {
            return null
        }
        var movie_id: Int = movies_collection.movies[randomizer.nextInt(movies_collection.movies.size)].id
        val EMPTY_SEARCH = "empty"
        while (true) {
            val search: String? = movies_storage.getString(movie_id.toString(), EMPTY_SEARCH)
            if (search.equals(EMPTY_SEARCH)) {
                return movies_collection.movies.find { it.id == movie_id }
            }
            movie_id = movies_collection.movies[randomizer.nextInt(movies_collection.movies.size)].id
        }
    }

    private fun setRandomMovie(rand_movie: Movie?) {
        if (rand_movie != null) {
            infoTextView.visibility = View.GONE
            movieInfoLayout.visibility = View.VISIBLE
            movieNameView.text = rand_movie.name
            movieDirectorView.text = rand_movie.director
            movieActorsView.text = rand_movie.actors.joinToString { it }
            movieBudgetView.text = rand_movie.budget
            try {
                picasso.load(rand_movie.image).into(movieImageView)
            } catch (e: Exception) {
                movieImageView.setImageDrawable(getDrawable(R.drawable.ic_launcher_background))
            }

            movies_storage.edit().putString(rand_movie.id.toString(), rand_movie.name).apply()
        } else {
            infoTextView.text = "Все фильмы уже были показаны!"
            infoTextView.visibility = View.VISIBLE
            movieInfoLayout.visibility = View.GONE
        }
    }

    fun onReset(view: View) {
        infoTextView.text = "Нажмите на кнопку 'Следуюший фильм', чтобы увидеть фильм"
        infoTextView.visibility = View.VISIBLE
        movieInfoLayout.visibility = View.GONE
        movies_storage.edit().clear().apply()
    }


    fun onNext(view: View) {
        setRandomMovie(getRandomMovie())
    }
}