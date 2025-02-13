package co.renanbezerra.netflixremake.util

import android.os.Handler
import android.os.Looper
import android.util.Log
import co.renanbezerra.netflixremake.model.Movie
import co.renanbezerra.netflixremake.model.MovieDetail
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection

class MovieTask(private val callback: CallBack) {

    private val handler = Handler(Looper.getMainLooper())
    private var executor = Executors.newSingleThreadExecutor()

    interface CallBack {
        fun onResult(movieDetail: MovieDetail)
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

                if(statusCode == 400) {
                    stream = urlConnection.errorStream
                    buffer = BufferedInputStream(stream)
                    val jsonAsString = toString(buffer)
                } else if (statusCode > 400){
                    throw IOException("Error in the communication with the server")
                }

                stream = urlConnection.inputStream
                buffer = BufferedInputStream(stream)

                val jsonAsString = toString(buffer)

                val movieDetail = toMovieDetail(jsonAsString)

                handler.post {
                    callback.onResult(movieDetail)
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

    private fun toMovieDetail(jsonAsString: String): MovieDetail{
        val json = JSONObject(jsonAsString)

        val id = json.getInt("id")
        val title = json.getString("title")
        val desc = json.getString("desc")
        val cast = json.getString("cast")
        val coverUrl = json.getString("cover_url")
        val jsonMovies = json.getJSONArray("movie")

        val similars = mutableListOf<Movie>()

        for(i in 0 until jsonMovies.length()) {
            val jsonMovie = jsonMovies.getJSONObject(i)

            val similarId = jsonMovie.getInt("id")
            val similarCoverUrl = jsonMovie.getString("cover_url")

            val m = Movie(similarId, similarCoverUrl)
            similars.add(m)
        }
        val movie = Movie(id, coverUrl, title, desc, cast)
        return MovieDetail(movie, similars)
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