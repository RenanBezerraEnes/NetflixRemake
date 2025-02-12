package co.renanbezerra.netflixremake.util

import android.util.Log
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection

class CategoryTask {

    fun execute(url: String) {
        val executor = Executors.newSingleThreadExecutor()

        executor.execute {
            try {
                val requestUrl = URL(url)
                val urlConnection = requestUrl.openConnection() as HttpsURLConnection
                urlConnection.readTimeout = 2000
                urlConnection.connectTimeout = 2000

                val statusCode = urlConnection.responseCode
                if (statusCode > 400) {
                    throw IOException("Error in the communication with the server")
                }

                val stream = urlConnection.inputStream
                //val jsonAsString = stream.bufferedReader().use { it.readText() } //transform bytes to string
                //Log.i("TESTE",jsonAsString)

                val buffer = BufferedInputStream(stream)
                val jsonAsString = toString(buffer)
                Log.i("TESTE", jsonAsString)
            } catch (e: IOException) {
                Log.e("Teste", e.message ?: "Error not recongnize", e)
            }
        }
    }

    private fun toString(stream: InputStream): String {
        val bytes = ByteArray(1024)
        var read: Int
        var byteArrayOPS = ByteArrayOutputStream()
        while (true) {
            read = stream.read(bytes)
            if(read <= 0){
                break
            }
            byteArrayOPS.write(bytes, 0, read)
        }
        return String(byteArrayOPS.toByteArray())
    }
}