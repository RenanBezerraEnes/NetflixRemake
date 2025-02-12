package co.renanbezerra.netflixremake.util

import android.os.Handler
import android.os.Looper
import android.util.Log
import co.renanbezerra.netflixremake.model.Category
import co.renanbezerra.netflixremake.model.Movie
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection

class CategoryTask(private val callback: CallBack) {

    private val handler = Handler(Looper.getMainLooper())
    private var executor = Executors.newSingleThreadExecutor()

    interface CallBack {
        fun onResult(categories: List<Category>)
        fun onFailure(message: String)
        fun onPreExecute()
    }

    fun execute(url: String) {
        callback.onPreExecute()
        executor = Executors.newSingleThreadExecutor()

        executor.execute {
            var urlConnection: HttpsURLConnection? = null
            var buffer: BufferedInputStream? = null
            var stream: InputStream? = null
            try {
                val requestUrl = URL(url)
                urlConnection = requestUrl.openConnection() as HttpsURLConnection
                urlConnection.readTimeout = 2000
                urlConnection.connectTimeout = 2000

                val statusCode = urlConnection.responseCode
                if (statusCode > 400) {
                    throw IOException("Error in the communication with the server")
                }

                stream = urlConnection.inputStream
                //val jsonAsString = stream.bufferedReader().use { it.readText() } //transform bytes to string
                //Log.i("TESTE",jsonAsString)

                buffer = BufferedInputStream(stream)
                val jsonAsString = toString(buffer)

                val categories = toCategory(jsonAsString)

                handler.post {
                    callback.onResult(categories)
                }

            } catch (e: IOException) {
                var messageError = e.message ?: "Error not recongnize"
                Log.e("Teste", messageError, e)

                handler.post {
                    callback.onFailure(messageError)
                }
            } finally {
                urlConnection?.disconnect()
                stream?.close()
                buffer?.close()
            }
        }
    }

    private fun toCategory(jsonAsString: String): List<Category> {
        val categories = mutableListOf<Category>()

        val jsonRoot = JSONObject(jsonAsString)

        val jsonCategories = jsonRoot.getJSONArray("category")

        for (i in 0 until jsonCategories.length()) {
            val jsonCategory = jsonCategories.getJSONObject(i)
            val title = jsonCategory.getString("title")
            val jsonMovies = jsonCategory.getJSONArray("movie")

            val movies = mutableListOf<Movie>()

            for (m in 0 until jsonMovies.length()) {
                val jsonMovie = jsonMovies.getJSONObject(m)
                val id = jsonMovie.getInt("id")
                val coverUrl = jsonMovie.getString("cover_url")

                movies.add(Movie(id, coverUrl))
            }

            categories.add(Category(title, movies))
        }

        return categories
    }

    private fun toString(stream: InputStream): String {
        val bytes = ByteArray(1024)
        var read: Int
        var byteArrayOPS = ByteArrayOutputStream()
        while (true) {
            read = stream.read(bytes)
            if (read <= 0) {
                break
            }
            byteArrayOPS.write(bytes, 0, read)
        }
        return String(byteArrayOPS.toByteArray())
    }
}