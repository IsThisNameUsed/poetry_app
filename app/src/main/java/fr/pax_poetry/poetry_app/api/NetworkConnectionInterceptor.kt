
import android.content.Context
import android.net.NetworkInfo

import android.net.ConnectivityManager
import fr.pax_poetry.poetry_app.metier.PoemItem
import okhttp3.Call
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.net.SocketTimeoutException


class NetworkConnectionInterceptor(context: Context) : Interceptor {
    private val mContext: Context

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isConnected) {
            throw NoConnectivityException()
            // Throwing our custom exception 'NoConnectivityException'
        }
        try{
            val builder: Request.Builder = chain.request().newBuilder()
            return chain.proceed(builder.build())
        }
       catch( exception: SocketTimeoutException)
       {
           exception.printStackTrace();
       }
        return chain.proceed(chain.request())
    }

    val isConnected: Boolean
        get() {
            val connectivityManager =
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = connectivityManager.activeNetworkInfo
            return netInfo != null && netInfo.isConnected
        }

    init {
        mContext = context
    }
}


class NoConnectivityException : IOException() {
    // You can send any message whatever you want from here.
    override val message: String
        get() = "No Internet Connection"
    override val cause: Throwable?
        get() = super.cause

}