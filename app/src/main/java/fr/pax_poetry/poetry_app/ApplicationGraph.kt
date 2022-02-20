package fr.pax_poetry.poetry_app

import dagger.Component
import fr.pb.roomandviewmodel.PoemViewModel
import fr.pb.roomandviewmodel.PoemViewModelFactory
import javax.inject.Singleton

@Singleton
@Component
interface ApplicationGraph {

    fun provideSaveTextUtils(): SaveTextUtils
    fun providePoemRepository(): PoemRepository
    fun providePoemViewModel(): PoemViewModelFactory
}