package co.tuister.data.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresApi
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

@SuppressLint("MissingPermission")
class ConnectivityUtil(val context: Context) {

    private var isConnected: Boolean

    init {
        isConnected = getConnectionState()
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            registerNetWorkCallback()
        }
    }

    fun isConnected(): Boolean {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            getConnectionState()
        } else {
            isConnected
        }
    }

    private fun getConnectionState(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        return connectivityManager?.activeNetworkInfo?.isConnected ?: false
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun registerNetWorkCallback() {

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

        val builder: NetworkRequest.Builder = NetworkRequest.Builder()

        connectivityManager!!.registerNetworkCallback(
            builder.build(),
            object : NetworkCallback() {
                override fun onAvailable(network: Network) {
                    isConnected = checkInternetConnection()
                }

                override fun onLost(network: Network) {
                    isConnected = false
                }
            }
        )
    }

    private fun checkInternetConnection(): Boolean {
        return try {
            val urlConnection: HttpURLConnection = URL("https://clients3.google.com/generate_204")
                .openConnection() as HttpURLConnection
            urlConnection.setRequestProperty("User-Agent", "Android")
            urlConnection.setRequestProperty("Connection", "close")
            urlConnection.connectTimeout = TIME_OUT
            urlConnection.connect()
            if (urlConnection.responseCode == RESPONSE_CODE && urlConnection.contentLength == 0) {
                return true
            }
            false
        } catch (e: IOException) {
            false
        }
    }

    companion object {
        private const val TIME_OUT = 1500
        private const val RESPONSE_CODE = 204
    }
}
