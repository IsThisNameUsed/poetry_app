package fr.pax_poetry.poetry_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.pax_poetry.poetry_app.metier.PoemItem
import java.util.ArrayList

class CardViewAdapter(var list:ArrayList<PoemItem>): RecyclerView.Adapter<CardViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.poem_cardview, parent, false))

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        (holder as ViewHolder).bind(list.get(position));
    }

    override fun getItemCount(): Int {
        return list.size
    }
    fun setOnItemClickListener(aClickListener: ClickListener) {
        mClickListener = aClickListener
    }



    lateinit var mClickListener: ClickListener
    interface ClickListener {
        fun onClick(pos: Int, aView: View)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{

        init {
            itemView.setOnClickListener(this)
        }

        val st_name=itemView.findViewById<TextView>(R.id.poemtext)

        fun bind(model: PoemItem): Unit {
            st_name.text = model.poem
        }

        override fun onClick(v: View?) {
            mClickListener.onClick(adapterPosition, itemView)
        }
    }
}

