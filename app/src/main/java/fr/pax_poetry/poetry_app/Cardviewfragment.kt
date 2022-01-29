package fr.pax_poetry.poetry_app

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import org.jetbrains.annotations.NotNull
import java.lang.Exception


class Cardviewfragment(val text:String): Fragment(){

    private lateinit var poemText:String
    private lateinit var poemTextView: TextView
    private lateinit var readerFragment: ReaderFragment
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.poem_cardview,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        poemTextView = view!!.findViewById(R.id.poemtext)
        poemText = text
        poemTextView.text = poemText
        readerFragment = parentFragment as ReaderFragment

        view?.setOnClickListener {
            readerFragment.onCLickCardView()
        }

        view?.setOnLongClickListener {
            Toast.makeText(context, "LONG CLICK READER", Toast.LENGTH_LONG).show()
            readerFragment.onCLickCardView()
            true
        }
    }
}