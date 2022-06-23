package ru.netology.nerecipe.data

data class Recipe(
    val step1: String,
    val step2: String? = null,
    val step3: String? = null,
    val step4: String? = null,
    val step5: String? = null,
    val step6: String? = null
)