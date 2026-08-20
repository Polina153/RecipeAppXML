package com.example.recipeappxml.model

import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    val id: Int,
    val title: String,
    val description: String,
    val imageUrl: String
) {
}

fun CategoryDto.toCategory() =
    Category(
        id = id,
        title = title,
        description = description,
        imageUrl = imageUrl.let { "https://recipes.androidsprint.ru/api/images/$it" })