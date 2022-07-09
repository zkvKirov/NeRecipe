package ru.netology.nerecipe.data

import kotlinx.serialization.Serializable

@Serializable
class StepsCard(
    val id: Int = 0,
    val picture: String? = null,
    val content: String
)