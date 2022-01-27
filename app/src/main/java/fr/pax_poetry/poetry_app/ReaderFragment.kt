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
import fr.pax_poetry.poetry_app.metier.PoemItem
import android.os.Environment
import android.widget.Button
import java.io.File
import android.widget.Toast
import androidx.core.view.get
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction


class ReaderFragment() : Fragment() {

    private val FILENAME = "favori.txt"
    private val FOLDERNAME = "favoris"

    private var readFromStorage = false
    private lateinit var mPager: ViewPager2
    private var itemList = mutableListOf<PoemItem>()
    private var poemList = mutableListOf<String>()
    private var savedPosition:Int = -1
    lateinit var positionPasser: OnPositionPass
    lateinit var state:Parcelable
    private lateinit var pagerAdapter:ScreenSlidePagerAdapter
    private lateinit var switchButton: Button
    private lateinit var saveButton: Button

   fun SetPageViewerPosition(savedPosition: Int){
        this.savedPosition = savedPosition
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.reading_fragment_layout,container,false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = view!!.findViewById(R.id.pager)

        // The pager adapter provides the pages to the view pager.
        pagerAdapter = ScreenSlidePagerAdapter(childFragmentManager)

        state = pagerAdapter.saveState()

        //mPager.offsetLeftAndRight(3)
        mPager.setSaveEnabled(false)
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

        mPager!!.adapter = pagerAdapter

        saveButton = view!!.findViewById(R.id.save_button)
        saveButton.setOnClickListener {writeOnExternalStorage()}

        switchButton = view!!.findViewById(R.id.favoris_button)

        switchButton.setOnClickListener {
           if(!readFromStorage) {
               readFromStorage()
               switchButton.text = getResources().getString(R.string.read_button)
               saveButton.visibility = View.INVISIBLE
               }
           else{
               readFromApi()
               switchButton.text = getResources().getString(R.string.favoris_button)
               saveButton.visibility = View.VISIBLE
           }
        }
    }

    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) : FragmentStateAdapter(fm,this.lifecycle) {

        override fun getItemCount(): Int = poemList.size

        override fun createFragment(position: Int):  Cardviewfragment
        {
            return Cardviewfragment(poemList[position])
        }
    }

    fun passData(position: Int){
        positionPasser.onPositionPass(position)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        positionPasser = context as OnPositionPass
    }

    interface OnPositionPass {
        fun onPositionPass(data: Int)
    }


    private fun readFromStorage() {

        if (SaveTextUtils.isExternalStorageReadable()) {
            var directory: File

            //External public
            directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)

            // External - Private
            //directory = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)

            var result = SaveTextUtils.getTextFromStorage(directory, context,FILENAME,FOLDERNAME)
            if (result != null) {
                InitializePoemListFromFavoris(result)
                mPager!!.adapter = pagerAdapter
                readFromStorage = true;
            }
        }
    }

    private fun readFromApi() {
        InitializePoemListFromItemList()
        mPager!!.adapter = pagerAdapter
        readFromStorage = false;
    }

    fun InitializePoemListFromFavoris(favorisString : String){
        poemList.clear()
        val sb = StringBuilder()
        for(item in favorisString)
        {
            if(SaveTextUtils.separator.compareTo(item) != 0)
                sb.append(item)
            else {
                poemList.add(sb.toString())
                sb.clear()
            }
        }
    }

    fun InitializeItemListFromApi(poemListInput : List<PoemItem>){
        itemList.clear()
        itemList.addAll(poemListInput)
        InitializePoemListFromItemList()
    }

    private fun InitializePoemListFromItemList(){
        poemList.clear()
        for(item in itemList)
        {
            poemList.add(item.poem)
        }
    }
    private fun writeOnExternalStorage() {
        if (SaveTextUtils.isExternalStorageWritable()) {
            val directory: File

            directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            //directory = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)

            SaveTextUtils.setTextInStorage(directory,context,FILENAME,FOLDERNAME,poemList[savedPosition])

        } else {
            Toast.makeText(context, "external_storage_impossible_create_file", Toast.LENGTH_LONG).show()
        }
    }
}

