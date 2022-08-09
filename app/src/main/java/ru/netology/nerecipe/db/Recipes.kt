package ru.netology.nerecipe.db

import ru.netology.nerecipe.data.RecipeCard

internal fun RecipeEntity.toModel() = RecipeCard(
    id = id,
    title = title,
    author = author,
    category = category,
    isFavorite = isFavorite,
    stepsCard = stepsCard
    )

internal fun RecipeCard.toEntity() = RecipeEntity(
    id = id,
    title = title,
    author = author,
    category = category,
    isFavorite = isFavorite,
    stepsCard = stepsCard
)