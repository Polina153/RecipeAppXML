package com.example.recipeappxml

import android.os.Parcel
import android.os.Parcelable


data class Ingredient(
    val quantity: String,
    val unitOfMeasure: String,
    val description: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(quantity)
        parcel.writeString(unitOfMeasure)
        parcel.writeString(description)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Ingredient> {
        override fun createFromParcel(parcel: Parcel): Ingredient = Ingredient(parcel)
        override fun newArray(size: Int): Array<Ingredient?> = arrayOfNulls(size)
    }
}