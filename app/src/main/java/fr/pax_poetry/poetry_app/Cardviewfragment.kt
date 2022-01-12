package fr.pax_poetry.poetry_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class Cardviewfragment(val text:String): Fragment() {

    private lateinit var poemText:String
    private lateinit var poemTextView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.poem_cardview,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        poemTextView = view!!.findViewById(R.id.poemtext)
        poemText = text
        poemTextView.text = poemText
    }

}