package com.example.recipeappxml.data

import android.content.Context
import androidx.room.Room
import com.example.recipeappxml.model.CategoriesDao
import com.example.recipeappxml.model.Category
import com.example.recipeappxml.model.CategoryDto
import com.example.recipeappxml.model.RecipeDto
import com.example.recipeappxml.model.toCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RecipesRepository(applicationContext: Context) {

    val database: Database by lazy {
        Room.databaseBuilder(
            applicationContext,
            Database::class.java,
            "categories.db"
        ).build()
    }

    val categoriesDao: CategoriesDao by lazy { database.categoriesDao() }

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://recipes.androidsprint.ru/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service: RecipeApiService =
        retrofit.create(RecipeApiService::class.java)

    fun getCategoriesFromCache(): Flow<List<Category>> = categoriesDao.getAllCategories()

    // СЕТЕВОЙ ЗАПРОС: Только сеть. Возвращаем DTO или null.
    suspend fun fetchCategoriesFromNetwork(): List<CategoryDto>? {
        return safeCall { service.getCategories() }
    }

    // СОХРАНЕНИЕ В БАЗУ: Room сам выполнит insert в IO-потоке благодаря suspend-функции DAO.
    suspend fun saveCategoriesToDb(categories: List<CategoryDto>) {
        withContext(Dispatchers.IO) {
            categoriesDao.clearCategories()
            categoriesDao.insertCategories(categories.map { it.toCategory() })
        }
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