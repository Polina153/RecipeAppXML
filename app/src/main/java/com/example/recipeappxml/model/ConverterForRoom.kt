package com.example.recipeappxml.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ConverterForRoom {
    private val gson = Gson()

    // Вспомогательный inline-метод.
    // Ключевое слово 'reified' позволяет узнать реальный тип T внутри функции.
    private inline fun <reified T> genericHelper(json: String): T {
        return gson.fromJson(json, object : TypeToken<T>() {}.type)
    }

    @TypeConverter
    fun fromIngredients(json: String): List<Ingredient> = genericHelper<List<Ingredient>>(json)

    @TypeConverter
    fun toIngredients(ingredients: List<Ingredient>): String = gson.toJson(ingredients)

    @TypeConverter
    fun fromMethod(json: String): List<String> = genericHelper<List<String>>(json)

    @TypeConverter
    fun toMethod(method: List<String>): String = gson.toJson(method)

}