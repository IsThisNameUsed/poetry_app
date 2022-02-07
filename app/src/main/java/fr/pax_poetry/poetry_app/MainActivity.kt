package fr.pax_poetry.poetry_app


import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.*
import fr.pax_poetry.poetry_app.api.ClientPoetryAPI
import kotlinx.coroutines.*
import java.net.SocketTimeoutException

class MainActivity : ReaderFragment.OnPositionPass, AppCompatActivity() {

    var writerFragment:WriterFragment=WriterFragment()
    var readerFragment:ReaderFragment=ReaderFragment()
    var pageViewerPosition: Int = -1

    init {
        instance = this
    }

    companion object {
        private var instance: MainActivity? = null

        fun getApplicationContext() : Context {
            return instance!!.applicationContext
        }
    }

    fun multiply(a:Int,b: Int): Int
    {
        return a * b
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getSupportFragmentManager().beginTransaction()
            .add(R.id.fragment_main, readerFragment).commit()
        getSupportFragmentManager().beginTransaction()
            .hide(readerFragment).commit()


        CoroutineScope(Dispatchers.IO).async{ readerFragment.poemViewModel.getRemotePoemListFromApi() }


        getSupportFragmentManager().beginTransaction()
            .add(R.id.fragment_main, writerFragment).commit()

        //Set navigation buttons

       var reader_button = findViewById<Button>(R.id.reader_button)

       reader_button.setOnClickListener {
           supportFragmentManager.commit {
               setReorderingAllowed(true)
               val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
               transaction.hide(writerFragment)
               transaction.commit()
           }

           supportFragmentManager.commit {
               if(pageViewerPosition>=0)
                   readerFragment.SetPageViewerPosition(pageViewerPosition)
               setReorderingAllowed(true)
               val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
               transaction.show(readerFragment)
               transaction.commit()
           }
       }

       var writer_button = findViewById<Button>(R.id.writer_button)
       writer_button.setOnClickListener {
           supportFragmentManager.commit {
               if(pageViewerPosition>=0)
                   readerFragment.SetPageViewerPosition(pageViewerPosition)
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

        var testClicK = findViewById<View>(R.id.fragment_main)
        testClicK.setOnClickListener {
            Toast.makeText(this.baseContext, "CLICK MAIN ACTIVITY", Toast.LENGTH_LONG).show()
        }
    }

    override fun onPositionPass(position: Int) {
        pageViewerPosition = position
    }

}