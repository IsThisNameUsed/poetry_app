package fr.pb.roomandviewmodel


import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import fr.pax_poetry.poetry_app.MainActivity
import fr.pax_poetry.poetry_app.PoemRepository
import fr.pax_poetry.poetry_app.api.ClientPoetryAPI
import fr.pax_poetry.poetry_app.metier.PoemItem
import kotlinx.coroutines.*
import java.io.IOException
import java.net.SocketTimeoutException
import kotlin.coroutines.coroutineContext

class PoemViewModel() : ViewModel() {

    var remotePoemList: MutableLiveData<MutableList<String>> = MutableLiveData<MutableList<String>>()

    fun saveOfflineData()
    {
        PoemRepository.saveOfflineData()
    }

    fun getOfflineData()
    {
        PoemRepository.getOfflineData()
    }

    suspend fun getRemotePoemListFromApi(){
        try{
            val request = CoroutineScope(Dispatchers.IO).async{PoemRepository.getPoemItems2()}
            Log.d("COR","PoemViewModel cor" + coroutineContext.toString())
            request.await()
        } catch (e: IOException) {
            if(PoemRepository.offlineRemotePoemList!=null && PoemRepository.remotePoemList.size==0 )
                remotePoemList.postValue(PoemRepository.offlineRemotePoemList)
            throw e
        }
        /*} catch(ce: SocketTimeoutException){
            ClientPoetryAPI.service.getItems().cancel()
            Log.d("VIEW MODEL", "SocketTimeoutException")
            throw ce
        }*/

        remotePoemList.postValue(PoemRepository.remotePoemList)
        MainActivity.getApplicationContext().filesDir

    }
}

class WordViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PoemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PoemViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}