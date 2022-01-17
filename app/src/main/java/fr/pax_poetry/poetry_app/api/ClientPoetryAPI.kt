package fr.pax_poetry.poetry_app.api

import android.os.Parcel
import android.os.Parcelable
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import android.util.Log
import okhttp3.ConnectionSpec
import java.util.*
import okhttp3.CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384
import okhttp3.CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384
import okhttp3.CipherSuite.TLS_ECDHE_RSA_WITH_RC4_128_SHA
import okhttp3.CipherSuite.TLS_ECDHE_RSA_WITH_NULL_SHA
import okhttp3.CipherSuite.TLS_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA
import okhttp3.CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA
import okhttp3.CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256
import okhttp3.CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256
import okhttp3.CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA
import okhttp3.CipherSuite.TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256
import okhttp3.TlsVersion
import okhttp3.OkHttpClient

class ClientPoetryAPI() : Parcelable {

    /* private val retrofit: Retrofit = if(android.os.Build.VERSION.SDK_INT >= 26) {
          Log.d("OKHTTTP", "safe Okhttp")
          Retrofit.Builder().baseUrl(IpoetryAPI.ENDPOINT).addConverterFactory(MoshiConverterFactory.create())
              .build()
      }
     else {
     Log.d("OKHTTP3", "Unsafe Okhttp")
         Retrofit.Builder().baseUrl(IpoetryAPI.ENDPOINT).client(UnsafeOkHttpClient.unsafeOkHttpClient)
         .addConverterFactory(MoshiConverterFactory.create()).build()
     }*/

     private val retrofit:Retrofit = Retrofit.Builder().baseUrl(IpoetryAPI.ENDPOINT).client(UnsafeOkHttpClient.unsafeOkHttpClient)
     .addConverterFactory(MoshiConverterFactory.create()).build()

    val service = retrofit.create(IpoetryAPI::class.java)

    constructor(parcel: Parcel) : this() {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ClientPoetryAPI> {
        override fun createFromParcel(parcel: Parcel): ClientPoetryAPI {
            return ClientPoetryAPI(parcel)
        }

        override fun newArray(size: Int): Array<ClientPoetryAPI?> {
            return arrayOfNulls(size)
        }
    }
}