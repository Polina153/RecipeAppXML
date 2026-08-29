package com.example.recipeappxml.model


import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String,
    val imageUrl: String
) : Parcelable