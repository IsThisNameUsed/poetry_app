package fr.pb.roomandviewmodel


import android.util.Log
import androidx.lifecycle.*
import fr.pax_poetry.poetry_app.PoemRepository
import fr.pax_poetry.poetry_app.api.ClientPoetryAPI
import fr.pax_poetry.poetry_app.metier.PoemItem
import kotlinx.coroutines.*
import java.net.SocketTimeoutException
import kotlin.coroutines.coroutineContext

class PoemViewModel() : ViewModel() {

    var remotePoemList: MutableLiveData<List<PoemItem>> = MutableLiveData<List<PoemItem>>()

    suspend fun getRemotePoemListFromApi(){
        try{
            val request = CoroutineScope(Dispatchers.IO).async{PoemRepository.getPoemItems2()}
            Log.d("COR","PoemViewModel cor" + coroutineContext.toString())
            request.await()
        } catch(ce: SocketTimeoutException){
            ClientPoetryAPI.service.getItems().cancel()
            Log.d("VIEW MODEL", "SocketTimeoutException")
            throw ce
        }
        //request.join()
        remotePoemList.postValue(PoemRepository.remotePoemList)

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