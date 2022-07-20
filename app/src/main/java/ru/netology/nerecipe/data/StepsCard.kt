package ru.netology.nerecipe.data

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
class StepsCard(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("picture")
    val picture: String? = null,
    @SerializedName("content")
    val content: String
)