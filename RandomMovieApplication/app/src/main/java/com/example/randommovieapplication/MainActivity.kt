package com.example.randommovieapplication

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var movies: Array<String>
    private val remainingIndices = mutableListOf<Int>()

    private lateinit var movieText: TextView
    private lateinit var remainingText: TextView

    private var currentMovieIndex = -1
    private var exhaustedMessageShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val root = findViewById<android.view.View>(R.id.main)
        val paddingLeft = root.paddingLeft
        val paddingTop = root.paddingTop
        val paddingRight = root.paddingRight
        val paddingBottom = root.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )
            view.setPadding(
                paddingLeft + bars.left,
                paddingTop + bars.top,
                paddingRight + bars.right,
                paddingBottom + bars.bottom
            )
            insets
        }
        ViewCompat.requestApplyInsets(root)

        movies = resources.getStringArray(R.array.movies)

        movieText = findViewById(R.id.movieText)
        remainingText = findViewById(R.id.remainingText)

        if (savedInstanceState == null) {
            remainingIndices.addAll(movies.indices)
        } else {
            savedInstanceState.getIntArray(KEY_REMAINING)?.let {
                remainingIndices.addAll(it.toList())
            }

            currentMovieIndex =
                savedInstanceState.getInt(KEY_CURRENT, -1)

            exhaustedMessageShown =
                savedInstanceState.getBoolean(KEY_EXHAUSTED, false)
        }

        render()

        findViewById<Button>(R.id.showButton).setOnClickListener {
            showRandomMovie()
        }

        findViewById<Button>(R.id.resetButton).setOnClickListener {
            resetMovies()
        }
    }

    private fun showRandomMovie() {
        if (remainingIndices.isEmpty()) {
            exhaustedMessageShown = true
            render()
            return
        }

        val randomPosition = Random.nextInt(remainingIndices.size)

        currentMovieIndex = remainingIndices.removeAt(randomPosition)
        exhaustedMessageShown = false

        render()
    }

    private fun resetMovies() {
        remainingIndices.clear()
        remainingIndices.addAll(movies.indices)

        currentMovieIndex = -1
        exhaustedMessageShown = false

        render()
    }

    private fun render() {
        movieText.text = when {
            exhaustedMessageShown -> getString(R.string.no_movies)
            currentMovieIndex in movies.indices -> movies[currentMovieIndex]
            else -> getString(R.string.initial_message)
        }

        remainingText.text = getString(
            R.string.remaining_count,
            remainingIndices.size
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putIntArray(
            KEY_REMAINING,
            remainingIndices.toIntArray()
        )
        outState.putInt(KEY_CURRENT, currentMovieIndex)
        outState.putBoolean(KEY_EXHAUSTED, exhaustedMessageShown)

        super.onSaveInstanceState(outState)
    }

    companion object {
        private const val KEY_REMAINING = "remaining_indices"
        private const val KEY_CURRENT = "current_movie_index"
        private const val KEY_EXHAUSTED = "exhausted_message_shown"
    }
}