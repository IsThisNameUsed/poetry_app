package fr.pax_poetry.poetry_app.api


import fr.pax_poetry.poetry_app.metier.PoemItem
import fr.pax_poetry.poetry_app.metier.PoemItemDto
import retrofit2.Call
import retrofit2.http.*


interface IpoetryAPI {

    companion object {
        val ENDPOINT = "https://10.0.2.2:7095/api/"

    }

    @GET("todoitems/")
    fun getItems(): Call<List<PoemItem>>

    @GET("todoitems/{id}")
    fun getItemAt(@Path("id") id: Int): Call<PoemItem>

    @POST("todoitems")
    fun sendItem(@Body poemItem:PoemItemDto): Call<PoemItemDto>

    @DELETE
    fun deleteItem(@Path("id") id: Int)
}