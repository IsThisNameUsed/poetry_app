package fr.pax_poetry.poetry_app

import android.util.Log
import fr.pax_poetry.poetry_app.api.ClientPoetryAPI
import fr.pax_poetry.poetry_app.metier.PoemItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class PoemRepository {

    var clientPoetryAPI = ClientPoetryAPI()
    var remotePoemList = listOf<PoemItem>()
    var apiResponse = false

    fun getPoemItemsSync()
    {
       try {
           val response = clientPoetryAPI.service.getItems().execute()
           if (!response.isSuccessful()) throw  IOException("Unexpected code " + response)

           remotePoemList = response.body()!!
       }
       catch(e: IOException)
       {
           throw e
       }
    }

    fun getPoemItems() {
        clientPoetryAPI.service.getItems().enqueue(object : Callback<List<PoemItem>> {

            override fun onResponse(call: Call<List<PoemItem>>, response: Response<List<PoemItem>>) {

                var body = response.body()!!

                body?.let {
                    remotePoemList = body
                    apiResponse = true
                }
            }
            override fun onFailure(call: Call<List<PoemItem>>, t: Throwable) {
                Log.e("REG", "Error : $t")
            }
        })
    }
}