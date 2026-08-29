package com.example.recipeappxml.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey val id: Int,
    val categoryId: Int?,
    val title: String,
    val ingredients: List<Ingredient>,
    val method: List<String>,
    val imageUrl: String,
    var isFavorite: Boolean
) : Parcelable