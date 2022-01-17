package fr.pax_poetry.poetry_app.metier

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PoemItem(val id:Int, val poem:String): Parcelable{

}

@Parcelize
data class PoemItemDto(val poem:String): Parcelable{

}
