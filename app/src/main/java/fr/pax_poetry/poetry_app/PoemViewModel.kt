package fr.pb.roomandviewmodel

import androidx.lifecycle.*
import fr.pax_poetry.poetry_app.PoemRepository
import fr.pax_poetry.poetry_app.metier.PoemItem
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import retrofit2.Call

class PoemViewModel(private val repository: PoemRepository) : ViewModel() {

    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    var remotePoemList: MutableLiveData<List<PoemItem>> = MutableLiveData<List<PoemItem>>()

    suspend fun getRemotePoemListFromApi(){
        val request = CoroutineScope(Dispatchers.Default).launch{repository.getPoemItemsSync()}
        request.join()
        remotePoemList.value = repository.remotePoemList
    }
}

class WordViewModelFactory(private val repository: PoemRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PoemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PoemViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}