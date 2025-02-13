package co.renanbezerra.netflixremake

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.renanbezerra.netflixremake.model.Movie
import co.renanbezerra.netflixremake.model.MovieDetail
import co.renanbezerra.netflixremake.util.DownloadImageTask
import co.renanbezerra.netflixremake.util.MovieTask

class MovieActivity : AppCompatActivity(), MovieTask.CallBack {

    private lateinit var txtTitle: TextView
    private lateinit var txtDescription: TextView
    private lateinit var txtCast: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: MovieAdapter
    private val movies = mutableListOf<Movie>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)

        txtTitle = findViewById(R.id.movie_txt_title)
        txtDescription = findViewById(R.id.movie_txt_desc)
        txtCast = findViewById(R.id.movie_txt_cast)
        progressBar = findViewById(R.id.movie_progressBar)

        val rv: RecyclerView = findViewById(R.id.movie_rv_similar)

        val id = intent?.getIntExtra("id", 0) ?: throw IllegalStateException("Id not found")

        val url = "https://atway.tiagoaguiar.co/fenix/netflixapp/movie/$id?apiKey=ce653dcd-037c-4f42-bcf5-539c6a516c3f"

        MovieTask(this).execute(url)


        adapter = MovieAdapter(movies, R.layout.movie_item_similar)
        rv.layoutManager = GridLayoutManager(this, 3)
        rv.adapter = adapter

        val toolBar: Toolbar = findViewById(R.id.movie_toolBar)
        setSupportActionBar(toolBar)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = null

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResult(movieDetail: MovieDetail) {
        progressBar.visibility = View.GONE
        txtTitle.text = movieDetail.movie.title
        txtDescription.text = movieDetail.movie.desc
        txtCast.text = getString(R.string.cast, movieDetail.movie.cast)
        movies.clear()

        movies.addAll(movieDetail.similars)
        adapter.notifyDataSetChanged()

        DownloadImageTask(object : DownloadImageTask.CallBack {
            override fun onResult(bitmap: Bitmap) {
                val layerDrawable: LayerDrawable = ContextCompat.getDrawable(this@MovieActivity, R.drawable.shadows) as LayerDrawable
                val movieCover = BitmapDrawable(resources, bitmap)
                layerDrawable.setDrawableByLayerId(R.id.cover_drawable, movieCover)
                val coverImg: ImageView = findViewById(R.id.movie_img)
                coverImg.setImageDrawable(layerDrawable)
            }
        }).execute(movieDetail.movie.coverUrl)
    }

    override fun onFailure(message: String) {
        progressBar.visibility = View.GONE
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onPreExecute() {
        progressBar.visibility = View.VISIBLE
    }
}