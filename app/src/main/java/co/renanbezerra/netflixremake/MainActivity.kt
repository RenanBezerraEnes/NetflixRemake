package co.renanbezerra.netflixremake

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.renanbezerra.netflixremake.model.Category
import co.renanbezerra.netflixremake.model.Movie
import co.renanbezerra.netflixremake.util.CategoryTask

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val categories = mutableListOf<Category>()


        val adapter = CategoryAdapter(categories)
        val rv: RecyclerView = findViewById(R.id.rv_main)

        rv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rv.adapter = adapter

        CategoryTask().execute("https://atway.tiagoaguiar.co/fenix/netflixapp/home?apiKey=ce653dcd-037c-4f42-bcf5-539c6a516c3f")
    }

}