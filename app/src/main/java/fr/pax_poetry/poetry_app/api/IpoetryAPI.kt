package fr.pax_poetry.poetry_app.api


import fr.pax_poetry.poetry_app.metier.PoemItem
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface IpoetryAPI {

    companion object {
        val ENDPOINT = "http://10.0.2.2:5095/api/"
    }

    @GET("todoitems/")
    fun getPoemsItem(): Call<List<PoemItem>>

    @GET("todoitems/{id}")
    fun getPoemItemAt(@Path("id") id: Int): Call<PoemItem>
}