package ru.netology.nerecipe.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
class RecipeCreateResult (
    val newTitle: String,
    val newAuthor: String,
    val newCategory: String,
//    @RawValue
//    val newStep: StepsCard
    val newStep: String
) : Parcelable