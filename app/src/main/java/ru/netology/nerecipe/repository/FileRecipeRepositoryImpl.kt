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
        val recipes: List<RecipeCard> = if (recipesFile.exists()) {
            val inputStream = application.openFileInput(FILE_NAME)
            val reader = inputStream.bufferedReader()
            reader.use {gson.fromJson(it, type)}
        } else emptyList()
        data = MutableLiveData(recipes)
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
        recipes = recipes.map {
            if (it.id != recipeId) it
            else it.copy(
                isFavorite = !it.isFavorite
            )
        }
    }

    override fun remove(recipeId: Int) {
        recipes = recipes.filter {
            it.id != recipeId
        }
    }

    override fun save(card: RecipeCard) {
        if (card.id == RecipeRepository.NEW_RECIPE_ID) insert(card) else update(card)
    }

    private fun insert(card: RecipeCard) {
        recipes = listOf(
            card.copy(id = ++nextId)
        ) + recipes
    }

    private fun update(card: RecipeCard) {
        recipes = recipes.map {
            if (it.id != card.id) it else it.copy(
                title = card.title,
                author = card.author,
                category = card.category
            )
        }
    }

    companion object {
        const val NEXT_ID_PREFS_KEY = "nextId"
        const val FILE_NAME = "posts.json"
    }
}