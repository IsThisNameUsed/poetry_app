package fr.pax_poetry.poetry_app

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import fr.pax_poetry.poetry_app.api.ClientPoetryAPI
import fr.pax_poetry.poetry_app.metier.PoemItemDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WriterFragment : Fragment() {

    private lateinit var send_button: Button
    private lateinit var text_editor: EditText
    private lateinit var clientPoetryAPI : ClientPoetryAPI

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.writing_fragment_layout,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (getArguments() != null) {
            clientPoetryAPI = getArguments()!!.getParcelable("ClientPoetryAPI")!!
        }

        send_button = view!!.findViewById(R.id.send_button)
        text_editor = view!!.findViewById(R.id.text_editor)
        send_button.setOnClickListener { sendTextToServer() }

        Log.d("D","STARTED")
    }

    private fun sendTextToServer(){
        Log.d("D","sendTextToServer")
        val poemItem = PoemItemDto(text_editor.text.toString())
        if(clientPoetryAPI==null || poemItem.poem=="")
            return

        clientPoetryAPI?.service?. sendItem(poemItem)?.enqueue(object : Callback<PoemItemDto> {
            override fun onResponse(call: Call<PoemItemDto>, response: Response<PoemItemDto>) {

                val poemResponse = response.body()
                poemResponse?.let {
                }
            }
            override fun onFailure(call: Call<PoemItemDto>, t: Throwable) {
                Log.e("REG", "Error : $t")
            }
        })
    }
}