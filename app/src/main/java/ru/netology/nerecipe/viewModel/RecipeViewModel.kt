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

    val data = repository.getAll()
    private var recipeId by Delegates.notNull<Int>()
    private var newStepId = 0

    private var currentCard = RecipeCard(-1, "", "", "", false, ArrayList())

    val navigateToRecipeContentScreenEvent = SingleLiveEvent<RecipeCreateResult?>()
    val navigateToStepContentScreenEvent = SingleLiveEvent<StepCreateResult?>()
    val navigateToFavoriteFragment = SingleLiveEvent<Unit>()
    val navigateToRecipeFragment = SingleLiveEvent<Unit>()
    val navigateToFullRecipeFragment = SingleLiveEvent<Int>()
    val currentRecipe = MutableLiveData<RecipeCard?> (null)
    private val currentStep = MutableLiveData<StepsCard?> (null)
    private var stepsData: ArrayList<StepsCard> = ArrayList()

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
        if (recipeCreateResult.newStepsCard.isEmpty()) {
            Toast.makeText(getApplication(), "Рецепт не сохранён, добавьте в хотя бы один этап", Toast.LENGTH_LONG).show()
            return
        }
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
        Toast.makeText(getApplication(), "Рецепт создан/обновлён", Toast.LENGTH_SHORT).show()
        currentRecipe.value = null
    }

    fun onSaveStepButtonClicked(stepCreateResult: StepCreateResult) : ArrayList<StepsCard> {
        if (currentCard.id != -1) stepsData = currentCard.stepsCard
        val newStep = currentStep.value?.copy(
            content = stepCreateResult.newContent,
            picture = stepCreateResult.newPicture
        ) ?: StepsCard(
                id = newStepId,
                content = stepCreateResult.newContent,
                picture = stepCreateResult.newPicture
            )
        if (currentStep.value != null) {
            stepsData.forEachIndexed { index, stepsCard ->
                if (stepsCard.id == newStep.id) {
                    stepsData[index] = newStep
                }
            }
            currentCard.stepsCard = stepsData
            repository.save(currentCard)
        } else {
            stepsData.add(newStep)
            ++newStepId
        }
        return stepsData
    }

    override fun onFavoriteClicked(card: RecipeCard) {
        repository.addFavorite(card.id)
    }

    override fun onRemoveClicked(card: RecipeCard) {
        repository.remove(card.id)
        Toast.makeText(getApplication(), "Рецепт был удалён", Toast.LENGTH_SHORT).show()
    }

    override fun onEditClicked(card: RecipeCard) {
        navigateToRecipeContentScreenEvent.value =
            RecipeCreateResult(card.title, card.author, card.category, card.stepsCard)
        currentRecipe.value = card
    }

    override fun onRecipeClicked(card: RecipeCard) {
        navigateToFullRecipeFragment.value = card.id
        recipeId = card.id
        currentCard = card
    }

    override fun onStepRemoveClicked(stepsCard: StepsCard) {
        currentCard.stepsCard.remove(stepsCard)
        repository.save(currentCard)
    }

    override fun onStepEditClicked(stepsCard: StepsCard) {
        navigateToStepContentScreenEvent.value = StepCreateResult(stepsCard.content, stepsCard.picture)
        currentStep.value = stepsCard
    }

}