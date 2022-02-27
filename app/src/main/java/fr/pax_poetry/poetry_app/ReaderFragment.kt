package fr.pax_poetry.poetry_app

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
import kotlinx.coroutines.*
import java.io.IOException

class ReaderFragment : Fragment() {

    private val filename = "favori.txt"
    private val folderName = "favoris"
    private val loadingNewPageMaxPosition = 8
    private val loadingNewPageMinPosition = 1
    private lateinit var mPager: ViewPager2
    private var poemListToDisplay = mutableListOf<String>()
    private var favoritesPoemList = mutableListOf<String>()
    private var pageViewerPoemList = mutableListOf<String>()
    private var displayingFavorites = false
    private var displayingOfflineList = false
    private var navigateBack = false
    private var backFromFavorites = false
    private var pageNumber = 1
    private var savedPosition:Int = -1
    //private lateinit var positionPasser: OnPositionPass
    private lateinit var state:Parcelable
    private lateinit var pagerAdapter:ScreenSlidePagerAdapter
    private lateinit var switchButton: Button
    private lateinit var saveButton: Button
    val applicationGraph: ApplicationGraph = DaggerApplicationGraph.create()
    val saveTextUtils: SaveTextUtils = applicationGraph.provideSaveTextUtils()
    val poemViewModel: PoemViewModel by viewModels {
        applicationGraph.providePoemViewModel()
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

        // ----- Instantiate a ViewPager and a PagerAdapter.
        mPager = view!!.findViewById(R.id.pager)
        mPager.setOnClickListener {
            Toast.makeText(context, "CLICK PAGER", Toast.LENGTH_LONG).show()
        }

        pagerAdapter = ScreenSlidePagerAdapter(childFragmentManager)

        state = pagerAdapter.saveState()
        mPager.offsetLeftAndRight(1)
        mPager.isSaveEnabled = false
        mPager.setPageTransformer(MarginPageTransformer(80))
        mPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setPageViewerSavedPosition(position)
                if(!MainActivity.isConnected())
                {
                    return
                }
                else if(displayingFavorites)
                    return
                majPageViewerData(position)
                //passData(position)
            }
        })
        mPager.adapter = pagerAdapter

        // ----- Set Buttons
        saveButton = view!!.findViewById(R.id.save_button)
        saveButton.setOnClickListener {writeOnExternalStorage()}

        switchButton = view!!.findViewById(R.id.favoris_button)
        switchButton.setOnClickListener {
           if(!displayingFavorites) {
               displayingFavorites = true
               showFavoritesPoemList()
               switchButton.text = resources.getString(R.string.read_button)
               saveButton.visibility = View.INVISIBLE
               }
           else{
               backFromFavorites = true
               displayingFavorites = false
               getPoemItemsData(pageNumber)
               switchButton.text = resources.getString(R.string.favoris_button)
               saveButton.visibility = View.VISIBLE
           }
        }

        getPoemItemsData(pageNumber)
    }

    fun majPageViewerData(position: Int = savedPosition){
        if(position >= loadingNewPageMaxPosition){
            pageNumber = pageNumber + 1
            navigateBack = false
            getPoemItemsData(pageNumber)
        }
        else if(position <= loadingNewPageMinPosition && pageNumber>1)
        {
            pageNumber = pageNumber - 1
            navigateBack = true
            getPoemItemsData(pageNumber)
        }
    }
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if(!isHidden)
        {
            //getPoemItemsData(pageNumber)
        }
    }

    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) : FragmentStateAdapter(fm,this.lifecycle) {

        lateinit var currentItem: Cardviewfragment
        override fun getItemCount(): Int = pageViewerPoemList.size

        override fun createFragment(position: Int): Cardviewfragment {
            currentItem = Cardviewfragment(pageViewerPoemList[position])
            return currentItem
        }
    }

    private fun setDataObservers(){

        val apiListObserver = Observer<MutableList<String>> { newList ->
            //displayingOfflineList = !MainActivity.serverStatusManager.getServerStatus()
            poemListToDisplay = newList
            showApiPoemList()
        }
        poemViewModel.remotePoemList.observe(this, apiListObserver)

        val favoritesListObserver = Observer<MutableList<String>> { newList ->
            favoritesPoemList = newList
        }
        poemViewModel.favoritesPoemList.observe(this, favoritesListObserver)

    }

    private fun getPoemItemsData(newPageNumber: Int)
    {
        if(!MainActivity.isConnected())
        {
            //Toast.makeText(context, "no available connection, load saved data", Toast.LENGTH_LONG).show()
            poemViewModel.displayOfflinePoemList()
            return
        }
        else{
            CoroutineScope(Dispatchers.IO).async{getPoemItemsRemoteData(newPageNumber)}
        }
    }

    private suspend fun getPoemItemsRemoteData(newPageNumber: Int){

        val request=CoroutineScope(Dispatchers.IO).async {
            try {
                poemViewModel.getRemotePoemList(newPageNumber)
                Log.d("COR","ReaderFragment cor START" + kotlin.coroutines.coroutineContext.toString())
            } catch (e: IOException) {
               throw e
            }
        }
        request.await()
    }

    private fun showApiPoemList() {
        if(pageViewerPoemList != poemListToDisplay)
        {
            pageViewerPoemList = poemListToDisplay.toMutableList()
            majPageViewerDataDisplayed()
        }
    }

    private fun showFavoritesPoemList() {
        pageViewerPoemList = favoritesPoemList.toMutableList()
        majPageViewerDataDisplayed()
    }

    private fun initializePoemListFromFavoris(favorisString : String){
        poemListToDisplay.clear()
        val sb = StringBuilder()
        for(item in favorisString)
        {
            if(saveTextUtils.separator.compareTo(item) != 0)
                sb.append(item)
            else {
                val text = saveTextUtils.cleanStringWhithPattern(sb.toString(),"\n*")
                poemListToDisplay.add(text)
                sb.clear()
            }
        }
    }

    private fun majPageViewerDataDisplayed() {
        if(backFromFavorites) {
            backFromFavorites = false
            mPager.adapter = pagerAdapter
            mPager.setCurrentItem(savedPosition, false)
        }
        else if(displayingFavorites)
            mPager.adapter = pagerAdapter
        else if(displayingOfflineList)
            mPager.adapter = pagerAdapter
        else if(!navigateBack)
            pagerAdapter.notifyItemRangeRemoved (0, 5)
        else if(navigateBack){
            pagerAdapter.notifyItemRangeRemoved (5, 5)
            pagerAdapter.notifyItemRangeInserted (0,5)
        }
        else mPager.adapter = pagerAdapter
    }

    //save the position for return from favorites and writer fragment
    fun setPageViewerSavedPosition(savedPosition: Int){
        if(!displayingFavorites)
            this.savedPosition = savedPosition
    }

    /*fun passData(position: Int){
        positionPasser.onPositionPass(position)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        positionPasser = context as OnPositionPass
    }

    interface OnPositionPass {
        fun onPositionPass(position: Int)
    }*/

    fun onCLickCardView(){
        if(displayingFavorites)
        {
            val text = poemListToDisplay[mPager.currentItem]
            Toast.makeText(context, "DELETE: $text", Toast.LENGTH_LONG).show()
            deleteFavoriteOnExternalStorage(text)
        }
    }


    private fun deleteFavoriteOnExternalStorage(text: String){
        val directory: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        saveTextUtils.deleteTextFromStorage(directory,context,filename,folderName, text)
        showFavoritesPoemList()
    }

    private fun writeOnExternalStorage() {
        if (saveTextUtils.isExternalStorageWritable()) {

            val directory: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            //directory = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            val text = pageViewerPoemList[savedPosition] + saveTextUtils.separator.toString() + System.lineSeparator()
            saveTextUtils.setTextInStorage(directory,context,filename,folderName,text)
            context?.let { poemViewModel.getFavoritesPoemList(it) }

        } else {
            Toast.makeText(context, "external_storage_impossible_create_file", Toast.LENGTH_LONG).show()
        }
    }
}

