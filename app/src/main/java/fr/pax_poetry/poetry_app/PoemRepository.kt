package fr.pax_poetry.poetry_app

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
import kotlin.coroutines.coroutineContext

class PoemRepository {

    companion object instance {
        private val FILENAME = "poemOfflineBackup.txt"
        private val FOLDERNAME = "poemApp"

        var clientPoetryAPI = ClientPoetryAPI()
        var remotePoemList =mutableListOf<String>()
        var offlineRemotePoemList = mutableListOf<String>()


        fun saveOfflineData(){
            val context = MainActivity.getApplicationContext()
            val directory = context.filesDir
            var text = ""
            for(poem in remotePoemList)
            {
                text += poem + SaveTextUtils.separator.toString() + System.lineSeparator()
            }

            SaveTextUtils.setTextInStorage(directory,context,FILENAME,FOLDERNAME,text,false)
        }


        fun getOfflineData()
        {
            val context = MainActivity.getApplicationContext()
            val directory = context.filesDir
            var text: String?
            text = SaveTextUtils.getTextFromStorage(directory,context,FILENAME,FOLDERNAME)!!
            val sb = StringBuilder()
            for(item in text)
            {
                if(SaveTextUtils.separator.compareTo(item) != 0)
                    sb.append(item)
                else {
                    val text = SaveTextUtils.cleanStringWhithPattern(sb.toString(),"\n*")
                    offlineRemotePoemList.add(text)
                    sb.clear()
                }
            }
        }

        fun fillStringListWithItems(itemList: List<PoemItem>)
        {
            remotePoemList.clear()
            for(item in itemList)
            {
                remotePoemList.add(item.poem)
            }
        }

        suspend fun getPoemItems2() {
            Log.d("COR","PoemRepo cor" + coroutineContext.toString())
            runBlocking {
                try {
                    val response = ClientPoetryAPI.service.getItems().awaitResponse()
                    val itemList = response.body()!!
                    fillStringListWithItems(itemList)
                } catch (e: IOException) {
                    Log.d("API CALL", "IOException, no response from API")

                    throw e
                }
                finally {


                }
                /*}catch(e: SocketTimeoutException){
                    //ClientPoetryAPI.service.getItems().cancel()
                    Log.d("API CALL", "SocketTimeoutException")
                    throw e
                } catch (e: ConnectException) {
                    throw e*/
            }
        }



        suspend fun getPoemItems() {
            Log.d("COR","PoemRepo cor" + coroutineContext.toString())
            ClientPoetryAPI.service.getItems().enqueue(object : Callback<List<PoemItem>> {

                override fun onResponse(call: Call<List<PoemItem>>,response: Response<List<PoemItem>>) {
                    var body = response.body()!!
                    body?.let {
                        val itemList = response.body()!!
                        fillStringListWithItems(itemList)
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