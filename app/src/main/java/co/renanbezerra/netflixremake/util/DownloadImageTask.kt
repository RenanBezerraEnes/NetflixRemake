package co.renanbezerra.netflixremake.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection

class DownloadImageTask(private val callback: CallBack) {
    private val handler = Handler(Looper.getMainLooper())
    private var executor = Executors.newSingleThreadExecutor()

    interface CallBack {
        fun onResult(bitmap: Bitmap)
    }

    fun execute(url: String) {
        executor.execute {
            var urlConnection: HttpsURLConnection? = null
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

                //Convert this stream in bytes of pixels
                val bitmap = BitmapFactory.decodeStream(stream)

                handler.post {
                    callback.onResult(bitmap)
                }
            }
            catch (e: IOException) {
                val messageError = e.message ?: "Error not recongnize"
                Log.e("Teste", messageError, e)
            } finally {
                urlConnection?.disconnect()
                stream?.close()
            }
        }
    }

}
