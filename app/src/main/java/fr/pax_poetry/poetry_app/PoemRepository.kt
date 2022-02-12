package fr.pax_poetry.poetry_app

import android.content.Context
import android.os.Environment
import android.util.Log
import fr.pax_poetry.poetry_app.api.ClientPoetryAPI
import fr.pax_poetry.poetry_app.metier.PoemItem
import fr.pax_poetry.poetry_app.metier.PoemItemDto
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse
import java.io.File
import java.io.IOException
import kotlin.coroutines.coroutineContext

class PoemRepository {

    companion object instance {
        private const val offlineListFilename = "poemOfflineBackup.txt"
        private const val offlineListFolderName = "poemApp"
        private const val favoritesFilename = "favori.txt"
        private const val favoritesFoldername = "favoris"

        var clientPoetryAPI = ClientPoetryAPI()
        var remotePoemList =mutableListOf<String>()
        var offlineRemotePoemList = mutableListOf<String>()
        var favoritesPoemList = mutableListOf<String>()

        fun saveOfflineData(){
            val context = MainActivity.getApplicationContext()
            val directory = context.filesDir
            var text = ""
            for(poem in remotePoemList)
            {
                text += poem + SaveTextUtils.separator.toString() + System.lineSeparator()
            }

            SaveTextUtils.setTextInStorage(directory,context,offlineListFilename,offlineListFolderName,text,false)
        }


        fun getOfflineData()
        {
            val context = MainActivity.getApplicationContext()
            val directory = context.filesDir
            var text: String?
            text = SaveTextUtils.getTextFromStorage(directory,context,offlineListFilename,offlineListFolderName)!!
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

        suspend fun getPoemListFromApi() {
            runBlocking {
                try {
                    Log.d("COR","PoemRepo cor START" + kotlin.coroutines.coroutineContext.toString())
                    val response = ClientPoetryAPI.service.getItems().awaitResponse()
                    val itemList = response.body()!!
                    fillStringListWithItems(itemList)
                    if(itemList.size>0)
                        saveOfflineData()
                } catch (e: IOException) {
                    Log.d("API CALL", "IOException, no response from API")
                    throw e
                }
                /*}catch(e: SocketTimeoutException){
                    //ClientPoetryAPI.service.getItems().cancel()
                    Log.d("API CALL", "SocketTimeoutException")
                    throw e
                } catch (e: ConnectException) {
                    throw e*/
            }
            Log.d("COR","PoemRepo cor END" + kotlin.coroutines.coroutineContext.toString())
        }

        fun getPoemListFromFavoritesFile(context: Context)
        {
            if (SaveTextUtils.isExternalStorageReadable()) {

                //External public
                val directory: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                // External - Private
                //directory = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)

                val result = SaveTextUtils.getTextFromStorage(directory, context,favoritesFilename,favoritesFoldername)
                if (result != null) {
                    favoritesPoemList.clear()
                    val sb = StringBuilder()
                    for(item in result)
                    {
                        if(SaveTextUtils.separator.compareTo(item) != 0)
                            sb.append(item)
                        else {
                            val text = SaveTextUtils.cleanStringWhithPattern(sb.toString(),"\n*")
                            favoritesPoemList.add(text)
                            sb.clear()
                        }
                    }
                }
            }
        }

        suspend fun getPoemItems2() {
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