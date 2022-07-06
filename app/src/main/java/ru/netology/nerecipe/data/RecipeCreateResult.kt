package ru.netology.nerecipe.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class RecipeCreateResult (
    val newTitle: String,
    val newAuthor: String,
    val newCategory: String,
    val newStep1: String,
    val newStep2: String?
) : Parcelable