package fr.pax_poetry.poetry_app

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import android.os.Environment
import android.util.Log
import android.widget.Button
import java.io.File
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import fr.pb.roomandviewmodel.PoemViewModel
import fr.pb.roomandviewmodel.WordViewModelFactory
import kotlinx.coroutines.*
import java.io.IOException




class ReaderFragment : Fragment() {

    private val filename = "favori.txt"
    private val folderName = "favoris"

    private var readFromStorage = false
    private lateinit var mPager: ViewPager2
    private var apiPoemList = mutableListOf<String>()
    private var favoritesPoemList = mutableListOf<String>()
    private var pageViewerPoemList = mutableListOf<String>()
    private var savedPosition:Int = -1
    private lateinit var positionPasser: OnPositionPass
    private lateinit var state:Parcelable
    private lateinit var pagerAdapter:ScreenSlidePagerAdapter
    private lateinit var switchButton: Button
    private lateinit var saveButton: Button
    val poemViewModel: PoemViewModel by viewModels {
        WordViewModelFactory()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        CoroutineScope(Dispatchers.Main).launch{
            setDataObservers()
        }
        context?.let { poemViewModel.getFavoritesPoemList(it) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.reading_fragment_layout,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        // Instantiate a ViewPager and a PagerAdapter.
        mPager = view!!.findViewById(R.id.pager)
        mPager.setOnClickListener {
            Toast.makeText(context, "CLICK PAGER", Toast.LENGTH_LONG).show()
        }

        // The pager adapter provides the pages to the view pager.
        pagerAdapter = ScreenSlidePagerAdapter(childFragmentManager)

        state = pagerAdapter.saveState()
        mPager.offsetLeftAndRight(1)
        mPager.isSaveEnabled = false
        mPager.setPageTransformer(MarginPageTransformer(80))
        mPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                passData(position)
                savedPosition = position
                super.onPageSelected(position)
            }
        })

        if(savedPosition>=0)
            mPager.setCurrentItem(savedPosition, false)

        mPager.adapter = pagerAdapter

        saveButton = view!!.findViewById(R.id.save_button)
        saveButton.setOnClickListener {writeOnExternalStorage()}

        switchButton = view!!.findViewById(R.id.favoris_button)
        switchButton.setOnClickListener {
           if(!readFromStorage) {
               showFavoritesPoemList()
               readFromStorage = true
               switchButton.text = resources.getString(R.string.read_button)
               saveButton.visibility = View.INVISIBLE
               }
           else{
               getPoemItemsData()
               readFromStorage = false
               switchButton.text = resources.getString(R.string.favoris_button)
               saveButton.visibility = View.VISIBLE
           }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if(!isHidden)
        {
            getPoemItemsData()
        }
    }

    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) : FragmentStateAdapter(fm,this.lifecycle) {

        lateinit var currentItem: Cardviewfragment
        override fun getItemCount(): Int = pageViewerPoemList.size

        override fun createFragment(position: Int): Cardviewfragment {
            currentItem=Cardviewfragment(pageViewerPoemList[position])
            return currentItem
        }
    }

    private fun setDataObservers(){

        val apiListObserver = Observer<MutableList<String>> { newList ->
            apiPoemList = newList
            showApiPoemList()
        }
        poemViewModel.remotePoemList.observe(this, apiListObserver)

        val favoritesListObserver = Observer<MutableList<String>> { newList ->
            favoritesPoemList = newList
        }
        poemViewModel.favoritesPoemList.observe(this, favoritesListObserver)

    }

    private fun getPoemItemsData()
    {
        if(!MainActivity.isConnected())
        {
            Toast.makeText(context, "no available connection, load saved data", Toast.LENGTH_LONG).show()
            poemViewModel.getOfflinePoemList()
            return
        }
        else{
            CoroutineScope(Dispatchers.IO).async{getPoemItemsRemoteData()}
        }
    }

    private suspend fun getPoemItemsRemoteData(){
        try {
            val request=CoroutineScope(Dispatchers.IO).async{ poemViewModel.getRemotePoemList()}
            Log.d("COR","ReaderFragment cor START" + kotlin.coroutines.coroutineContext.toString())
            request.await()
            Log.d("COR","ReaderFragment cor END1" + kotlin.coroutines.coroutineContext.toString())
        } catch (e: IOException) {
            Toast.makeText(context, "no response from server", Toast.LENGTH_LONG).show()
            throw e
        }
       /* }catch(ce: SocketTimeoutException){
            ClientPoetryAPI.service.getItems().cancel()
            Log.d("READER FRAG", "SocketTimeoutException")
            throw ce
        }*/
        Log.d("COR","ReaderFragment cor END2" + kotlin.coroutines.coroutineContext.toString())
    }

    private fun showApiPoemList() {
        pageViewerPoemList = apiPoemList
        majPageViewerData()
    }

    private fun showFavoritesPoemList() {
        pageViewerPoemList = favoritesPoemList
        majPageViewerData()
    }

    private fun initializePoemListFromFavoris(favorisString : String){
        apiPoemList.clear()
        val sb = StringBuilder()
        for(item in favorisString)
        {
            if(SaveTextUtils.separator.compareTo(item) != 0)
                sb.append(item)
            else {
                val text = SaveTextUtils.cleanStringWhithPattern(sb.toString(),"\n*")
                apiPoemList.add(text)
                sb.clear()
            }
        }
    }

    private fun majPageViewerData() {
        mPager.adapter = pagerAdapter
    }

    fun setPageViewerPosition(savedPosition: Int){
        this.savedPosition = savedPosition
    }

    fun passData(position: Int){
        positionPasser.onPositionPass(position)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        positionPasser = context as OnPositionPass
    }

    interface OnPositionPass {
        fun onPositionPass(position: Int)
    }

    fun onCLickCardView(){
        if(readFromStorage)
        {
            val text = apiPoemList[mPager.currentItem]
            Toast.makeText(context, "DELETE: $text", Toast.LENGTH_LONG).show()
            deleteFavoriteOnExternalStorage(text)
        }
    }


    private fun deleteFavoriteOnExternalStorage(text: String){
        val directory: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        SaveTextUtils.deleteTextFromStorage(directory,context,filename,folderName, text)
        showFavoritesPoemList()
    }

    private fun writeOnExternalStorage() {
        if (SaveTextUtils.isExternalStorageWritable()) {

            val directory: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            //directory = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            val text = pageViewerPoemList[savedPosition] + SaveTextUtils.separator.toString() + System.lineSeparator()
            SaveTextUtils.setTextInStorage(directory,context,filename,folderName,text)
            context?.let { poemViewModel.getFavoritesPoemList(it) }

        } else {
            Toast.makeText(context, "external_storage_impossible_create_file", Toast.LENGTH_LONG).show()
        }
    }
}

