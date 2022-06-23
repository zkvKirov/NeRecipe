package ru.netology.nerecipe.repository

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nerecipe.data.RecipeCard
import kotlin.properties.Delegates

class FileRecipeRepositoryImpl(
    private val application: Application
) : RecipeRepository {

    private val gson = Gson()
    private val type = TypeToken.getParameterized(List::class.java, RecipeCard::class.java).type

    private val prefs = application.getSharedPreferences(
        "repository", Context.MODE_PRIVATE
    )

    private val data: MutableLiveData<List<RecipeCard>>

    init {
        val recipesFile = application.filesDir.resolve(FILE_NAME)
        val posts: List<RecipeCard> = if (recipesFile.exists()) {
            val inputStream = application.openFileInput(FILE_NAME)
            val reader = inputStream.bufferedReader()
            reader.use {gson.fromJson(it, type)}
        } else emptyList()
        data = MutableLiveData(posts)
    }
    private var nextId: Int by Delegates.observable(
        prefs.getInt(NEXT_ID_PREFS_KEY, 0)
    ) {_, _, newValue ->
        prefs.edit { putInt(NEXT_ID_PREFS_KEY, newValue) }
    }

    private var recipes
        get() = checkNotNull(data.value) {
            "Data value should not be null"
        }
        set(value) {
            application.openFileOutput(
                FILE_NAME, Context.MODE_PRIVATE
            ).bufferedWriter().use {
                it.write(gson.toJson(value))
            }
            data.value = value
        }

    override fun getAll(): LiveData<List<RecipeCard>> = data

    override fun addFavorite(recipeId: Int) {
        TODO("Not yet implemented")
    }

    override fun remove(recipeId: Long) {
        TODO("Not yet implemented")
    }

    override fun save(card: RecipeCard) {
        TODO("Not yet implemented")
    }

    companion object {
        const val NEXT_ID_PREFS_KEY = "nextId"
        const val FILE_NAME = "posts.json"
    }
}