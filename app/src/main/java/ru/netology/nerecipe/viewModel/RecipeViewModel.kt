package ru.netology.nerecipe.viewModel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.netology.nerecipe.adapter.RecipeInteractionListener
import ru.netology.nerecipe.adapter.StepInteractionListener
import ru.netology.nerecipe.data.RecipeCard
import ru.netology.nerecipe.data.RecipeCreateResult
import ru.netology.nerecipe.data.StepCreateResult
import ru.netology.nerecipe.data.StepsCard
import ru.netology.nerecipe.repository.FileRecipeRepositoryImpl
import ru.netology.nerecipe.repository.RecipeRepository
import ru.netology.nmedia.util.SingleLiveEvent
import kotlin.properties.Delegates

class RecipeViewModel(
    application: Application
) : AndroidViewModel(application), RecipeInteractionListener, StepInteractionListener {

    private val repository: RecipeRepository = FileRecipeRepositoryImpl(application)

    //val data by repository::data
    val data = repository.getAll()
    private var recipeId by Delegates.notNull<Int>()
    private var newStepId = 0

    private var stepsData: ArrayList<StepsCard> = ArrayList()
    //private var stepCreateResult: StepCreateResult = StepCreateResult("", null)

    val navigateToRecipeContentScreenEvent = SingleLiveEvent<RecipeCreateResult?>()
    val navigateToStepContentScreenEvent = SingleLiveEvent<StepCreateResult?>()
    val navigateToFavoriteFragment = SingleLiveEvent<Unit>()
    val navigateToRecipeFragment = SingleLiveEvent<Unit>()
    val navigateToFullRecipeFragment = SingleLiveEvent<Int>()
    private val currentRecipe = MutableLiveData<RecipeCard?> (null)
    //private val stepsData = MutableLiveData<StepsCard?> (null)

    fun onAddButtonClicked(draft: RecipeCreateResult?) {
        navigateToRecipeContentScreenEvent.value = draft
    }

    fun onAddStepClicked(draftStep: StepCreateResult?) {
        navigateToStepContentScreenEvent.value = draftStep
    }

    fun onFavoriteButtonClicked() {
        navigateToFavoriteFragment.call()
    }

    fun onRecipeButtonClicked() {
        navigateToRecipeFragment.call()
    }

    fun onSaveButtonClicked(recipeCreateResult: RecipeCreateResult) {
        if (
            recipeCreateResult.newTitle.isBlank() &&
            recipeCreateResult.newAuthor.isBlank() &&
            recipeCreateResult.newCategory.isBlank() &&
            recipeCreateResult.newStepsCard.isEmpty()
                ) return
        val newRecipe = currentRecipe.value?.copy(
            title = recipeCreateResult.newTitle,
            author = recipeCreateResult.newAuthor,
            category = recipeCreateResult.newCategory,
            stepsCard = stepsData
        ) ?: RecipeCard(
            id = RecipeRepository.NEW_RECIPE_ID,
            title = recipeCreateResult.newTitle,
            author = recipeCreateResult.newAuthor,
            category = recipeCreateResult.newCategory,
            stepsCard = stepsData
        )
        repository.save(newRecipe)
        Toast.makeText(getApplication(), "Успех", Toast.LENGTH_SHORT).show()
        currentRecipe.value = null
    }

    fun onSaveStepButtonClicked(stepCreateResult: StepCreateResult) : ArrayList<StepsCard> {
        //if (stepCreateResult.newContent.isBlank()) return
        val newStep = StepsCard(
            id = newStepId,
            content = stepCreateResult.newContent,
            picture = stepCreateResult.newPicture
        )
        ++newStepId
        stepsData.add(newStep)
        return stepsData
    }

    override fun onFavoriteClicked(card: RecipeCard) {
        repository.addFavorite(card.id)
    }

    override fun onRemoveClicked(card: RecipeCard) {
        repository.remove(card.id)
        Toast.makeText(getApplication(), "Recipe was deleted", Toast.LENGTH_SHORT).show()
    }

    override fun onEditClicked(card: RecipeCard) {
        navigateToRecipeContentScreenEvent.value =
            card.stepsCard?.let { RecipeCreateResult(card.title, card.author, card.category, it) }
        currentRecipe.value = card
    }

    override fun onRecipeClicked(card: RecipeCard) {
        navigateToFullRecipeFragment.value = card.id
        recipeId = card.id
    }

    override fun onStepRemoveClicked(stepsCard: StepsCard) {
        TODO("Not yet implemented")
    }

    override fun onStepEditClicked(stepsCard: StepsCard) {
        TODO("Not yet implemented")
    }


}