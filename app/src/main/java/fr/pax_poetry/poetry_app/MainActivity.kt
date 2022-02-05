package fr.pax_poetry.poetry_app


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.*
import androidx.lifecycle.Observer
import fr.pax_poetry.poetry_app.api.ClientPoetryAPI
import fr.pax_poetry.poetry_app.metier.PoemItem
import fr.pb.roomandviewmodel.PoemViewModel
import fr.pb.roomandviewmodel.WordViewModelFactory
import kotlinx.coroutines.*

class MainActivity : ReaderFragment.OnPositionPass, AppCompatActivity() {

    var clientPoetryAPI = ClientPoetryAPI()
    var writerFragment:WriterFragment=WriterFragment()
    var readerFragment:ReaderFragment=ReaderFragment()
    var poemList : List<PoemItem> = listOf<PoemItem>()
    var pageViewerPosition: Int = -1
    val repository = PoemRepository()
    private val poemViewModel: PoemViewModel by viewModels {
        WordViewModelFactory(repository)
    }

    fun multiply(a:Int,b: Int): Int
    {
        return a * b
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Send the API Client to the writer fragment
        val args = Bundle()
        args.putParcelable("ClientPoetryAPI", clientPoetryAPI)
        writerFragment.arguments = args

        getSupportFragmentManager().beginTransaction()
            .add(R.id.fragment_main, readerFragment).commit()
        getSupportFragmentManager().beginTransaction()
            .hide(readerFragment).commit()
        CoroutineScope(Dispatchers.Main).launch{poemViewModel.getRemotePoemListFromApi()}
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