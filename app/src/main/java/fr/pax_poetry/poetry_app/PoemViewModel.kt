package fr.pb.roomandviewmodel


import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import fr.pax_poetry.poetry_app.MainActivity
import fr.pax_poetry.poetry_app.PoemRepository
import kotlinx.coroutines.*
import java.io.IOException
import kotlin.coroutines.coroutineContext

class PoemViewModel() : ViewModel() {

    var remotePoemList: MutableLiveData<MutableList<String>> = MutableLiveData<MutableList<String>>()
    var favoritesPoemList: MutableLiveData<MutableList<String>> = MutableLiveData<MutableList<String>>()

    fun saveOfflineData()
    {
        PoemRepository.saveOfflineData()
    }

    fun getOfflineData()
    {
        PoemRepository.getOfflineData()
    }

    fun getOfflinePoemList()
    {
        remotePoemList.postValue(PoemRepository.offlineRemotePoemList)
    }

    suspend fun getRemotePoemList(){
        try{
            Log.d("COR","PoemViewModel cor START" + coroutineContext.toString())
            val request = CoroutineScope(Dispatchers.IO).async{PoemRepository.getPoemListFromApi()}
            request.join()
            remotePoemList.postValue(PoemRepository.remotePoemList)
        } catch (e: IOException) {
            if(PoemRepository.offlineRemotePoemList!=null && PoemRepository.remotePoemList.size==0 )
                getOfflinePoemList()
            throw e
        }
        finally {
            Log.d("COR","PoemViewModel cor END FINALLY" + coroutineContext.toString())

        }
        /*} catch(ce: SocketTimeoutException){
            ClientPoetryAPI.service.getItems().cancel()
            Log.d("VIEW MODEL", "SocketTimeoutException")
            throw ce
        }*/
        Log.d("COR","PoemViewModel cor END" + coroutineContext.toString())
    }

    fun getFavoritesPoemList(context: Context)
    {
        PoemRepository.getPoemListFromFavoritesFile(context)
        favoritesPoemList.postValue(PoemRepository.favoritesPoemList)
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