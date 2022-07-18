package ru.netology.nerecipe.repository

import androidx.lifecycle.LiveData
import ru.netology.nerecipe.data.RecipeCard

interface RecipeRepository {

    //val data: LiveData<List<RecipeCard>>

    fun getAll(): LiveData<List<RecipeCard>>
    fun addFavorite(recipeId: Int)
    fun remove(recipeId: Int)
    fun save(card: RecipeCard)

    companion object {
        const val NEW_RECIPE_ID = 0
    }
}