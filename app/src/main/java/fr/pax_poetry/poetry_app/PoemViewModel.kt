package fr.pb.roomandviewmodel


import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import fr.pax_poetry.poetry_app.*
import fr.pax_poetry.poetry_app.api.ServerStatusManager
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

    fun displayOfflinePoemList()
    {
        remotePoemList.postValue(poemRepository.offlineRemotePoemList)
    }

    suspend fun getRemotePoemList(newPageNumber: Int){
        Log.d("COR","PoemViewModel cor START" + coroutineContext.toString())
        val request = CoroutineScope(Dispatchers.IO).async{
            try{
                poemRepository.getPoemListFromApi(newPageNumber)
            }
            catch(e: IOException){
                Log.d("sata","SSHX" )
                throw e
            }
            finally{
                if(poemRepository.offlineRemotePoemList!=null && poemRepository.remotePoemList.size==0 )
                    displayOfflinePoemList()
                else remotePoemList.postValue(poemRepository.remotePoemList)
            }
        }
        request.join()

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