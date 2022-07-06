package ru.netology.nerecipe.viewModel

import android.app.Application
import org.junit.Test
import org.junit.Assert.*
import org.junit.Ignore
import ru.netology.nerecipe.data.RecipeCard
import ru.netology.nerecipe.data.RecipeCreateResult

internal class RecipeViewModelTest {

    @Ignore
    @Test
    fun onSaveButtonClicked_newRecipe() {
        //arrange
        val viewModel = RecipeViewModel(application = Application())
        val expectedRecipeCard = RecipeCard(id = 0, title = "салат", author = "Я", category = "Азия", isFavorite = false, step1 = "1 этап", step2 = null)
        //act
        val actualRecipeCard = viewModel.onSaveButtonClicked(RecipeCreateResult(newTitle = "салат", newAuthor = "Я", newCategory = "Азия", newStep1 = "1 этап", newStep2 = null))
        //assert
        assertEquals(expectedRecipeCard, actualRecipeCard)
    }
}

