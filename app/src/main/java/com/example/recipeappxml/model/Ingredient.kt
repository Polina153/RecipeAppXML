package com.example.recipeappxml.model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Ingredient(
    val quantity: String,
    val unitOfMeasure: String,
    val description: String
) : Parcelable {

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(quantity)
        dest.writeString(unitOfMeasure)
        dest.writeString(description)
    }
}