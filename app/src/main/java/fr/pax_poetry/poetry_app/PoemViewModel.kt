package fr.pb.roomandviewmodel


import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import fr.pax_poetry.poetry_app.*
import kotlinx.coroutines.*
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.coroutineContext

class PoemViewModel() : ViewModel() {

    var remotePoemList: MutableLiveData<MutableList<String>> = MutableLiveData<MutableList<String>>()
    var favoritesPoemList: MutableLiveData<MutableList<String>> = MutableLiveData<MutableList<String>>()
    val applicationGraph: ApplicationGraph = DaggerApplicationGraph.create()
    val poemRepository: PoemRepository = applicationGraph.providePoemRepository()

    fun test(){
        Log.i("INFO","VIEW MODEL TEST")
    }

    fun saveOfflineData()
    {
        poemRepository.saveOfflineData()
    }

    fun getOfflineData()
    {
        poemRepository.getOfflineData()
    }

    fun getOfflinePoemList()
    {
        remotePoemList.postValue(poemRepository.offlineRemotePoemList)
    }

    suspend fun getRemotePoemList(){
        try{
            Log.d("COR","PoemViewModel cor START" + coroutineContext.toString())
            val request = CoroutineScope(Dispatchers.IO).async{poemRepository.getPoemListFromApi()}
            request.join()
            remotePoemList.postValue(poemRepository.remotePoemList)
        } catch (e: IOException) {
            if(poemRepository.offlineRemotePoemList!=null && poemRepository.remotePoemList.size==0 )
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
        poemRepository.getPoemListFromFavoritesFile(context)
        favoritesPoemList.postValue(poemRepository.favoritesPoemList)
    }
}

@Singleton
class PoemViewModelFactory @Inject constructor() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PoemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PoemViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}