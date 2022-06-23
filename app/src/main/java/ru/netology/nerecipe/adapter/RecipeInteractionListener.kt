package ru.netology.nerecipe.adapter

import ru.netology.nerecipe.data.RecipeCard

interface RecipeInteractionListener {
    fun onFavoriteClicked(card: RecipeCard)
    fun onRemoveClicked(card: RecipeCard)
    fun onEditClicked(card: RecipeCard)
}