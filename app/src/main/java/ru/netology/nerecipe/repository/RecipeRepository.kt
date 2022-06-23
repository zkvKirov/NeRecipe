package ru.netology.nerecipe.repository

import androidx.lifecycle.LiveData
import ru.netology.nerecipe.data.RecipeCard

interface RecipeRepository {

    fun getAll(): LiveData<List<RecipeCard>>
    fun addFavorite(recipeId: Int)
    fun remove(recipeId: Long)
    fun save(card: RecipeCard)

    companion object {
        const val NEW_RECIPE_ID = 0
    }
}