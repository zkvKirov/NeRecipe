package ru.netology.nerecipe.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


class RecipeCreateResult (
    val newTitle: String,
    val newAuthor: String,
    val newCategory: String,
    val newStepsCard: ArrayList<StepsCard>?
)