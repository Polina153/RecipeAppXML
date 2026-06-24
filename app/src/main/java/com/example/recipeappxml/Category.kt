package com.example.recipeappxml

import kotlinx.parcelize.Parcelize

@Parcelize
data class Category(
    val id: Int,
    val title: String,
    val description: String,
    val imageUrl: String
)