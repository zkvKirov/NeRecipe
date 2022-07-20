package ru.netology.nerecipe.data

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class RecipeCard(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("author")
    val author: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("isFavorite")
    val isFavorite: Boolean = false,
//    @SerializedName("stepsCard")
//    val stepsCard: ArrayList<StepsCard>
    )