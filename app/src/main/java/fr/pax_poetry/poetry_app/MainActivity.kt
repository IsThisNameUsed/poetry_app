package fr.pax_poetry.poetry_app

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.*
import kotlinx.coroutines.*
import com.google.android.material.snackbar.Snackbar


class MainActivity : ReaderFragment.OnPositionPass, AppCompatActivity() {

    private var writerFragment:WriterFragment=WriterFragment()
    private var readerFragment:ReaderFragment=ReaderFragment()
    private var pageViewerPosition: Int = -1
    private lateinit var mainView: View

    fun multiply(a:Int,b: Int): Int
    {
        return a * b
    }

    companion object {
        private var instance: MainActivity? = null
        private var isConnected: Boolean = false

        fun getApplicationContext() : Context {
            return instance!!.applicationContext
        }

        fun isConnected():Boolean{
            return isConnected
        }
    }

    init {
        instance = this
    }

    override fun onStart() {
        super.onStart()
        readerFragment.poemViewModel.getOfflineData()
    }
    override fun onStop() {
        super.onStop()
        //Todo move the poemViewModel ref in ActivityMain? Singleton?
        readerFragment.poemViewModel.saveOfflineData()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainView = findViewById<FrameLayout>(R.id.fragment_main)
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_main, readerFragment).commit()
        supportFragmentManager.beginTransaction()
            .hide(readerFragment).commit()


        /*CoroutineScope(Dispatchers.IO).async{
            Log.d("COR","MainActivity cor BEGIN" + kotlin.coroutines.coroutineContext.toString())
            readerFragment.poemViewModel.getRemotePoemList()
            Log.d("COR","MainActivity cor END" + kotlin.coroutines.coroutineContext.toString())
        }*/


        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_main, writerFragment).commit()

        //Set navigation buttons

       val readerButton = findViewById<Button>(R.id.reader_button)

       readerButton.setOnClickListener {
           supportFragmentManager.commit {
               setReorderingAllowed(true)
               val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
               transaction.hide(writerFragment)
               transaction.commit()
           }

           supportFragmentManager.commit {
               if(pageViewerPosition>=0)
                   readerFragment.setPageViewerPosition(pageViewerPosition)
               setReorderingAllowed(true)
               val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
               transaction.show(readerFragment)
               transaction.commit()
           }
       }

       val writerButton = findViewById<Button>(R.id.writer_button)
       writerButton.setOnClickListener {
           supportFragmentManager.commit {
               if(pageViewerPosition>=0)
                   readerFragment.setPageViewerPosition(pageViewerPosition)
               setReorderingAllowed(true)
               val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
               transaction.hide(readerFragment)
               transaction.commit()
           }

           supportFragmentManager.commit {
               setReorderingAllowed(true)
               val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
               transaction.show(writerFragment)
               transaction.commit()
           }
       }

        mainView.setOnClickListener {
            Toast.makeText(this.baseContext, "CLICK MAIN ACTIVITY", Toast.LENGTH_LONG).show()
        }

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            //.addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        val connectivityManager = getSystemService(ConnectivityManager::class.java)
        connectivityManager.requestNetwork(networkRequest, networkCallback)
    }


    fun showSnackbar(text:String, color:Int) {
        val snackbar = Snackbar
            .make(mainView, text, Snackbar.LENGTH_LONG)
            .setBackgroundTint(color)
        snackbar.view.textAlignment = View.TEXT_ALIGNMENT_CENTER
        
        //val view: View = snackbar.getView()

        //val tv = view.findViewById(android.support.design.R.id.snackbar_text) as TextView
        //tv.textAlignment = View.TEXT_ALIGNMENT_CENTER
        snackbar.show()
    }
    override fun onPositionPass(position: Int) {
        pageViewerPosition = position
    }


    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        // network is available for use
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            Log.e("ERR", "connection")
            showSnackbar("connected", ContextCompat.getColor(applicationContext, R.color.green))
            isConnected = true
        }

        // Network capabilities have changed for the network
        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            val hasCellular = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
            val hasWifi = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        }

        // lost network connection
        override fun onLost(network: Network) {
            super.onLost(network)
            Log.e("ERR", "no longer connection")
            showSnackbar("disconnected", ContextCompat.getColor(applicationContext, R.color.red))
            isConnected = false
        }
    }
}