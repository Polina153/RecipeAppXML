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
    val imageUrl: String
) : Parcelable {
    override fun describeContents(): Int = 0

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        categoryId?.let { parcel.writeInt(it) }
        parcel.writeString(title)
        // Пишем ингредиенты: превращаем List в JSON-строку
        val ingredientsJson = Gson().toJson(ingredients)
        parcel.writeString(ingredientsJson)
        // Пишем метод: стандартный метод для списка строк
        parcel.writeStringList(method)
        parcel.writeString(imageUrl)
    }
}