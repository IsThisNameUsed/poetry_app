package fr.pax_poetry.poetry_app.api

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import fr.pax_poetry.poetry_app.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServerStatusManager @Inject constructor(): ViewModel(){

    var serverOnline: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    fun setServerStatus(value: Boolean){
        if(serverOnline.value != value) {
            serverOnline.postValue(value)
            if(value == false)
                CoroutineScope(Dispatchers.IO).async{monitorServerState()}
        }
    }

    suspend fun monitorServerState(){
        var result = false
        while(result==false)
            result = pingToServer()
    }

    suspend fun pingToServer(): Boolean{
        var result=false
        val request = CoroutineScope(Dispatchers.IO).async{
            result = isURLReachable(MainActivity.getApplicationContext())
        }
        request.join()
        if(!result)
            delay(5000)

        return result
    }

    fun isURLReachable(context: Context): Boolean {
        return if (MainActivity.isConnected() || !MainActivity.isConnected()) {
            try {
                val url = URL("https://stackoverflow.com/questions/1443166/android-how-to-check-if-the-server-is-available") // Change to "http://google.com" for www  test.
                val urlc: HttpURLConnection = url.openConnection() as HttpURLConnection
                urlc.setConnectTimeout(3 * 1000) // 3 s.
                urlc.connect()
                if (urlc.getResponseCode() === 200) {        // 200 = "OK" code (http connection is fine).
                    Log.wtf("Connection", "Success !")
                    true
                } else {
                    Log.wtf("Connection", "Failure !")
                    false
                }
            } catch (e1: MalformedURLException) {
                false
            } catch (e: IOException) {
                Log.wtf("Connection", "Failure !")
                false
            }
        } else false
    }
}