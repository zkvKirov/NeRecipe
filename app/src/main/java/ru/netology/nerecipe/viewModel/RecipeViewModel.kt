package ru.netology.nerecipe.viewModel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.netology.nerecipe.adapter.RecipeInteractionListener
import ru.netology.nerecipe.data.RecipeCard
import ru.netology.nerecipe.data.RecipeCreateResult
import ru.netology.nerecipe.repository.FileRecipeRepositoryImpl
import ru.netology.nerecipe.repository.RecipeRepository
import ru.netology.nmedia.util.SingleLiveEvent
import kotlin.properties.Delegates

class RecipeViewModel(
    application: Application
) : AndroidViewModel(application), RecipeInteractionListener {

    private val repository: RecipeRepository = FileRecipeRepositoryImpl(application)

    val data = repository.getAll()
    var recipeId by Delegates.notNull<Int>()

    val navigateToRecipeContentScreenEvent = SingleLiveEvent<RecipeCreateResult?>()
    val navigateToFavoriteFragment = SingleLiveEvent<Unit>()
    val navigateToRecipeFragment = SingleLiveEvent<Unit>()
    //val navigateToCheckboxFragment = SingleLiveEvent<Unit>()
    val navigateToFullRecipeFragment = SingleLiveEvent<Int>()
    private val currentRecipe = MutableLiveData<RecipeCard?> (null)

    fun onAddButtonClicked(draft: RecipeCreateResult?) {
        navigateToRecipeContentScreenEvent.value = draft
    }

    fun onFavoriteButtonClicked() {
        navigateToFavoriteFragment.call()
    }

    fun onRecipeButtonClicked() {
        navigateToRecipeFragment.call()
    }

//    fun onFilterButtonClicked() {
//        navigateToCheckboxFragment.call()
//    }

    fun onSaveButtonClicked(recipeCreateResult: RecipeCreateResult) {
        if (recipeCreateResult.equals(null)) return
        val newRecipe = currentRecipe.value?.copy(
            title = recipeCreateResult.newTitle,
            author = recipeCreateResult.newAuthor,
            category = recipeCreateResult.newCategory,
            step1 = recipeCreateResult.newStep1,
            step2 = recipeCreateResult.newStep2
        ) ?: RecipeCard(
            id =RecipeRepository.NEW_RECIPE_ID,
            title = recipeCreateResult.newTitle,
            author = recipeCreateResult.newAuthor,
            category = recipeCreateResult.newCategory,
            step1 = recipeCreateResult.newStep1,
            step2 = recipeCreateResult.newStep2
        )
        repository.save(newRecipe)
        Toast.makeText(getApplication(), "Успех", Toast.LENGTH_SHORT).show()
        currentRecipe.value = null
    }

    override fun onFavoriteClicked(card: RecipeCard) {
        repository.addFavorite(card.id)
    }

    override fun onRemoveClicked(card: RecipeCard) {
        repository.remove(card.id)
        Toast.makeText(getApplication(), "Recipe was deleted", Toast.LENGTH_SHORT).show()
    }

    override fun onEditClicked(card: RecipeCard) {
        navigateToRecipeContentScreenEvent.value = RecipeCreateResult(card.title, card.author, card.category, card.step1, card.step2)
        currentRecipe.value = card
    }

    override fun onRecipeClicked(card: RecipeCard) {
        navigateToFullRecipeFragment.value = card.id
        recipeId = card.id
    }
}