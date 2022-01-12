package fr.pax_poetry.poetry_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.*
import fr.pax_poetry.poetry_app.api.ClientPoetryAPI
import fr.pax_poetry.poetry_app.metier.PoemItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : ReaderFragment.OnPositionPass, AppCompatActivity() {

    var clientPoetryAPI = ClientPoetryAPI();
    lateinit var text_obj: TextView
    var writerFragment:WriterFragment=WriterFragment()
    var readerFragment:ReaderFragment=ReaderFragment()
    var poemList : List<PoemItem>? = listOf<PoemItem>()
    var pageViewerPosition: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Set navigation buttons
        var button = findViewById<Button>(R.id.button)
        //button.setOnClickListener{getPoemItems()}

           var reader_button = findViewById<Button>(R.id.reader_button)
           reader_button.setOnClickListener {
           supportFragmentManager.commit {
               if(pageViewerPosition>=0)
                   readerFragment.SetPageViewerPosition(pageViewerPosition)
               setReorderingAllowed(true)
               val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
               transaction.replace(R.id.fragment_main, readerFragment)
               transaction.commit()
           }
       }

       var writer_button = findViewById<Button>(R.id.writer_button)
       writer_button.setOnClickListener {
           supportFragmentManager.commit {
               setReorderingAllowed(true)
               val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
               transaction.replace(R.id.fragment_main, writerFragment)
               transaction.commit()
           }
       }

        //Send poems data to reader fragment
        getPoemItems()
    }

    private fun getPoemItems(){
        clientPoetryAPI.service.getPoemsItem().enqueue(object : Callback <List<PoemItem>> {
            override fun onResponse(call: Call<List<PoemItem>>, response: Response<List<PoemItem>>) {

                poemList = response.body()

                poemList?.let {
                    //TODO manage the case we dont obtain a list (network error)
                    readerFragment.InitializePoemList(it)
                }
            }

            override fun onFailure(call: Call<List<PoemItem>>, t: Throwable) {
                Log.e("REG", "Error : $t")
            }
        })
    }

    override fun onPositionPass(position: Int) {
        pageViewerPosition = position
    }

}