package ru.netology.nerecipe.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RecipeDao {

    @Query("SELECT * FROM recipes ORDER BY id DESC")
    fun getAll(): LiveData<List<RecipeEntity>>

    @Insert
    fun insertRecipeCard(recipe: RecipeEntity)

    @Query("UPDATE recipes SET title = :title, author = :author, category = :category WHERE id = :id")
    fun updateRecipeCard(id: Int, title: String?, author: String?, category: String?)

    @Query("DELETE FROM recipes WHERE id = :id")
    fun removeRecipeCard(id: Int)

    @Query("SELECT * FROM recipes WHERE id = :id")
    fun addRecipeToFavorite(id: Int)

    @Query("UPDATE recipes SET isFavorite = CASE WHEN isFavorite THEN 0 ELSE 1 END WHERE id = :id")
    fun updateRecipeFavorite(id: Int)

}