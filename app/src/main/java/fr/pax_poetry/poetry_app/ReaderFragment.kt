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


class ReaderFragment() : Fragment() {

    private lateinit var mPager: ViewPager2
    private var poemList = mutableListOf<PoemItem>()
    private var savedPosition:Int = -1
    private var actualPosition: Int = 0
    lateinit var positionPasser: OnPositionPass
    lateinit var state:Parcelable
    private lateinit var pagerAdapter:ScreenSlidePagerAdapter

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

        // The pager adapter, which provides the pages to the view pager widget.
        pagerAdapter = ScreenSlidePagerAdapter(childFragmentManager)
        state = pagerAdapter.saveState()
        mPager!!.adapter = pagerAdapter
        //mPager.offsetLeftAndRight(3)
        mPager.setSaveEnabled(false)
        mPager.setPageTransformer(MarginPageTransformer(80))
        mPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                passData(position)
                super.onPageSelected(position)
            }
        })

        if(savedPosition>=0)
            mPager.setCurrentItem(savedPosition, false)

    }

    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) : FragmentStateAdapter(fm,this.lifecycle) {

        override fun getItemCount(): Int = poemList.size

        override fun createFragment(position: Int):  Cardviewfragment
        {
            return Cardviewfragment(poemList[position].poem)
        }
    }

    fun InitializePoemList(poemListInput : List<PoemItem>){
        poemList.addAll(poemListInput)
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
}

