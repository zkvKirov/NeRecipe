package ru.netology.nerecipe.data

import kotlinx.serialization.Serializable

@Serializable
data class RecipeCard(
    val id: Int,
    val title: String,
    val author: String,
    val category: String,
    val isFavorite: Boolean = false,
    //val steps: StepsCard
    val steps: String
    )