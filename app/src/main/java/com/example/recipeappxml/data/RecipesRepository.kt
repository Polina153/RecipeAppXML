package com.example.recipeappxml.data

import com.example.recipeappxml.model.CategoryDto
import com.example.recipeappxml.model.RecipeDto
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RecipesRepository {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://recipes.androidsprint.ru/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service: RecipeApiService =
        retrofit.create(RecipeApiService::class.java)

    fun getCategories(): List<CategoryDto>? {
        return safeCall { service.getCategories().execute().body() }
    }

    fun getRecipesByCategoryId(categoryId: Int): List<RecipeDto>? {
        return safeCall { service.getRecipesByCategoryId(categoryId).execute().body() }
    }

    fun getRecipeById(recipeId: Int): RecipeDto? {
        return safeCall { service.getRecipeById(recipeId).execute().body() }
    }

/*    fun getCategoryById(categoryId: Int): CategoryDto? {
        return safeCall { service.getCategoryById(categoryId).execute().body() }
    }*/

    fun getRecipesByIds(ids: Set<Int>): List<RecipeDto>? {
        return safeCall { service.getRecipesByIds(ids.joinToString(",")).execute().body() }
    }

    private fun <T> safeCall(block: () -> T?): T? = try {
        block()
    } catch (e: Exception) {
        null
    }
}