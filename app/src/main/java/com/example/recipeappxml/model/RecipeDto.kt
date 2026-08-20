package com.example.recipeappxml.model

import kotlinx.serialization.Serializable

@Serializable
data class RecipeDto(
    val id: Int,
    val title: String,
    val ingredients: List<IngredientDto>,
    val method: List<String>,
    val imageUrl: String
)

fun RecipeDto.toRecipe() =
    Recipe(
        id = id,
        title = title,
        ingredients = ingredients.map { it.toIngredient() },
        method = method,
        imageUrl = imageUrl.let { "https://recipes.androidsprint.ru/api/images/$it" }
    )