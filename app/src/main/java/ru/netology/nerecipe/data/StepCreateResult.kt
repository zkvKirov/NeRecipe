package ru.netology.nerecipe.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class StepCreateResult(
    val newContent: String,
    val newPicture: String?
)  : Parcelable