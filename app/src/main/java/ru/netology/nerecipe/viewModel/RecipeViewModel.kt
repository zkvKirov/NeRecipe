package ru.netology.nerecipe.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import ru.netology.nerecipe.adapter.RecipeInteractionListener
import ru.netology.nerecipe.data.RecipeCard
import ru.netology.nerecipe.repository.FileRecipeRepositoryImpl
import ru.netology.nerecipe.repository.RecipeRepository
import ru.netology.nmedia.util.SingleLiveEvent

class RecipeViewModel(
    application: Application
) : AndroidViewModel(application), RecipeInteractionListener {

    private val repository: RecipeRepository = FileRecipeRepositoryImpl(application)

    val data = repository.getAll()

    val navigateToPostContentScreenEvent = SingleLiveEvent<String>()

    fun onAddButtonClicked() {
        navigateToPostContentScreenEvent.call()
    }


    override fun onFavoriteClicked(card: RecipeCard) {
        TODO("Not yet implemented")
    }

    override fun onRemoveClicked(card: RecipeCard) {
        TODO("Not yet implemented")
    }

    override fun onEditClicked(card: RecipeCard) {
        TODO("Not yet implemented")
    }
}