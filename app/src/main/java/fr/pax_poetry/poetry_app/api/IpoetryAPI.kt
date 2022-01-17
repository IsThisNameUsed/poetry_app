package fr.pax_poetry.poetry_app.api


import fr.pax_poetry.poetry_app.metier.PoemItem
import fr.pax_poetry.poetry_app.metier.PoemItemDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Body

import retrofit2.http.POST




interface IpoetryAPI {

    companion object {
        val ENDPOINT = "https://10.0.2.2:7095/api/"

    }

    @GET("todoitems/")
    fun getPoemsItem(): Call<List<PoemItem>>

    @GET("todoitems/{id}")
    fun getPoemItemAt(@Path("id") id: Int): Call<PoemItem>

    @POST("todoitems")
    fun sendText(@Body poemItem:PoemItemDto): Call<PoemItemDto>
}