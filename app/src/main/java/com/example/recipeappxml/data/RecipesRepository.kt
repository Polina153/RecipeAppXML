package com.example.recipeappxml.data

import com.example.recipeappxml.model.CategoryDto
import com.example.recipeappxml.model.RecipeDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RecipesRepository {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://recipes.androidsprint.ru/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service: RecipeApiService =
        retrofit.create(RecipeApiService::class.java)

    suspend fun getCategories(): List<CategoryDto>? {
        return safeCall { service.getCategories() }
    }

    suspend fun getRecipesByCategoryId(categoryId: Int): List<RecipeDto>? {
        return safeCall { service.getRecipesByCategoryId(categoryId) }
    }

    suspend fun getRecipeById(recipeId: Int): RecipeDto? {
        return safeCall { service.getRecipeById(recipeId) }
    }

    suspend fun getRecipesByIds(ids: Set<Int>): List<RecipeDto>? {
        return safeCall { service.getRecipesByIds(ids.joinToString(",")) }
    }
//safeCall глотает все Exception и возвращает null. Работает, но теряется диагностика:
// вы не узнаете, упал ли сервер, отвалилась ли сеть или пришёл 500.
// Для этой задачи допустимо, но имейте в виду: в боевом проекте такую обёртку обычно сужают или
// пробрасывают ошибку наверх.

    private suspend fun <T> safeCall(block: suspend () -> T?): T? = try {
        withContext(Dispatchers.IO) {
            block()
        }
    } catch (e: Exception) {
        null
    }
}