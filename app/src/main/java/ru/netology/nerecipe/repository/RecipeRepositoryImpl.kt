package ru.netology.nerecipe.repository

import androidx.lifecycle.Transformations
import ru.netology.nerecipe.data.RecipeCard
import ru.netology.nerecipe.db.RecipeDao
import ru.netology.nerecipe.db.toEntity
import ru.netology.nerecipe.db.toModel

class RecipeRepositoryImpl(
    private val dao: RecipeDao
) : RecipeRepository {

    override fun getAll() = Transformations.map(dao.getAll()) { entities ->
        entities.map {
            it.toModel()
        }
    }

    override fun save(card: RecipeCard) {
        if (card.id == RecipeRepository.NEW_RECIPE_ID) dao.insertRecipeCard(card.toEntity())
        else dao.updateRecipeCard(card.id, card.title, card.author, card.category)
    }

    override fun remove(recipeId: Int) = dao.removeRecipeCard(recipeId)

    override fun addFavorite(recipeId: Int) {
        dao.addRecipeToFavorite(recipeId)
        dao.updateRecipeFavorite(recipeId)
    }



}