package ru.netology.nerecipe.data

import kotlinx.serialization.Serializable

@Serializable
data class RecipeCard(
    val id: Int,
    val title: String,
    val author: String,
    val category: String,
    val isFavorite: Boolean = false,
    val step1: String,
    val step2: String?,
//    val step3: String? = null,
//    val step4: String? = null,
//    val step5: String? = null,
//    val step6: String? = null
    )