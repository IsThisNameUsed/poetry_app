package fr.pax_poetry.poetry_app

import android.content.Context
import android.util.Log
import fr.pax_poetry.poetry_app.api.ClientPoetryAPI
import fr.pax_poetry.poetry_app.metier.PoemItem
import fr.pax_poetry.poetry_app.metier.PoemItemDto
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import kotlin.coroutines.coroutineContext

class PoemRepository {

    companion object instance {
        var clientPoetryAPI = ClientPoetryAPI()
        var remotePoemList = listOf<PoemItem>()

        suspend fun getPoemItems2() {
            Log.d("COR","PoemRepo cor" + coroutineContext.toString())
            runBlocking {
                try {
                    val response = ClientPoetryAPI.service.getItems().awaitResponse()
                    if (!response.isSuccessful) throw  IOException("Unexpected code " + response)
                    remotePoemList = response.body()!!
                }catch(ce: SocketTimeoutException){
                    ClientPoetryAPI.service.getItems().cancel()
                    Log.d("API CALL", "SocketTimeoutException")
                    throw ce
                } catch (ce: ConnectException) {
                    throw ce
                } catch (e: IOException) {
                    throw e
                }
                finally {

                    Log.d("API CALL", "FINALLY")
                }
            }
        }

        suspend fun getPoemItems() {
            Log.d("COR","PoemRepo cor" + coroutineContext.toString())
            ClientPoetryAPI.service.getItems().enqueue(object : Callback<List<PoemItem>> {

                override fun onResponse(call: Call<List<PoemItem>>,response: Response<List<PoemItem>>) {
                    var body = response.body()!!
                    body?.let {
                        remotePoemList = body
                    }
                }

                override fun onFailure(call: Call<List<PoemItem>>, t: Throwable) {
                    Log.e("REG", "Error : $t")
                }
            })
        }
    }

    private fun sendTextToServer(text: String) {
        Log.d("D", "sendTextToServer")
        val poemItem = PoemItemDto(text)
        if (clientPoetryAPI == null || poemItem.poem == "")
            return

        ClientPoetryAPI?.service?.sendItem(poemItem)?.enqueue(object : Callback<PoemItemDto> {
            override fun onResponse(call: Call<PoemItemDto>, response: Response<PoemItemDto>) {

                val poemResponse = response.body()
                poemResponse?.let {
                }
            }

            override fun onFailure(call: Call<PoemItemDto>, t: Throwable) {
                Log.e("REG", "Error : $t")
            }
        })
    }
}