package fr.pax_poetry.poetry_app.api

import NetworkConnectionInterceptor
import fr.pax_poetry.poetry_app.MainActivity
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class ClientPoetryAPI() {

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
    companion object instance {
        var retrofit: Retrofit

        init {
            retrofit= Retrofit.Builder().baseUrl(IpoetryAPI.ENDPOINT)
                .client(UnsafeOkHttpClient.unsafeOkHttpClient)
                .addConverterFactory(MoshiConverterFactory.create()).build()
        }

        val service = retrofit.create(IpoetryAPI::class.java)
    }

}