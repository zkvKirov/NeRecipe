package ru.netology.nerecipe.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
class RecipeCreateResult (
    @SerializedName("newTitle")
    val newTitle: String,
    @SerializedName("newAuthor")
    val newAuthor: String,
    @SerializedName("newCategory")
    val newCategory: String,
    @SerializedName("SerializedName")
    val newStepsCard: ArrayList<StepsCard>
)