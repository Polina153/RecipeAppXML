package com.example.recipeappxml

import kotlinx.parcelize.Parcelize

@Parcelize
data class Ingredient(
    val quantity: String,
    val unitOfMeasure: String,
    val description: String
)