package fr.pax_poetry.poetry_app.api

import NetworkConnectionInterceptor
import android.annotation.SuppressLint
import android.content.Context
import fr.pax_poetry.poetry_app.MainActivity
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.lang.Exception
import java.lang.RuntimeException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager



class UnsafeOkHttpClient {

    companion object instance {

        // Create a trust manager that does not validate certificate chains

        // Install the all-trusting trust manager
        // Create an ssl socket factory with our all-trusting manager

        val unsafeOkHttpClient: OkHttpClient = try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts =
                arrayOf<TrustManager>(
                    object : X509TrustManager {
                        @Throws(CertificateException::class)
                        override fun checkClientTrusted(
                            chain: Array<X509Certificate>,
                            authType: String
                        ) {
                        }

                        @Throws(CertificateException::class)
                        override fun checkServerTrusted(
                            chain: Array<X509Certificate>,
                            authType: String
                        ) {
                        }
                        override fun getAcceptedIssuers(): Array<X509Certificate> {
                            return arrayOf()
                        }
                    }
                )

            // Install the all-trusting trust manager
            val sslContext =
                SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())

            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory

            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(
                sslSocketFactory,
                trustAllCerts[0] as X509TrustManager
            )
            builder.hostnameVerifier { hostname, session -> true }
            //builder.addNetworkInterceptor(NetworkConnectionInterceptor(MainActivity.getApplicationContext()))
            builder.build()


        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}