package com.example.recipeappxml.data

import android.util.Log
import com.example.recipeappxml.model.CategoriesDao
import com.example.recipeappxml.model.Category
import com.example.recipeappxml.model.CategoryDto
import com.example.recipeappxml.model.Recipe
import com.example.recipeappxml.model.RecipeDto
import com.example.recipeappxml.model.RecipesDao
import com.example.recipeappxml.model.toCategory
import com.example.recipeappxml.model.toRecipe
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RecipesRepository @Inject constructor(
    val recipesDao: RecipesDao,
    val categoriesDao: CategoriesDao,
    val service: RecipeApiService,
    //val iODispatcher: CoroutineDispatcher
) {
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    fun getCategoriesFromCache(): Flow<List<Category>> = categoriesDao.getAllCategories()

    fun getRecipesByCategoryFromCache(categoryId: Int): Flow<List<Recipe>> =
        recipesDao.getRecipesByCategory(categoryId)

    fun getFavoriteRecipes(): Flow<List<Recipe>> = recipesDao.getFavoriteRecipes()

    suspend fun changeFavorite(recipeId: Int) = recipesDao.changeFavoriteRecipe(recipeId)

    // СЕТЕВОЙ ЗАПРОС: Только сеть. Возвращаем DTO или null.
    suspend fun fetchCategoriesFromNetwork(): List<CategoryDto>? {
        return safeNetworkCall { service.getCategories() }
    }

    // СОХРАНЕНИЕ В БАЗУ: Room сам выполнит insert в IO-потоке благодаря suspend-функции DAO.
    suspend fun saveCategoriesToDb(categories: List<CategoryDto>) {
        withContext(Dispatchers.IO) {
            categoriesDao.clearCategories()
            categoriesDao.insertCategories(categories.map { it.toCategory() })
        }
    }

    // СОХРАНЕНИЕ В БАЗУ: Room сам выполнит insert в IO-потоке благодаря suspend-функции DAO.
    suspend fun saveRecipesToDb(recipes: List<RecipeDto>, categoryId: Int) {
        withContext(Dispatchers.IO) {
            val favoriteIdsFromCache = recipesDao.getFavoriteRecipeIds().toSet()
            recipesDao.deleteRecipesByCategory(categoryId)
            recipesDao.insertRecipes(recipes.map { it.toRecipe(categoryId, favoriteIdsFromCache) })
        }
    }

    suspend fun getRecipesByCategoryIdFromNetwork(categoryId: Int): List<RecipeDto>? {
        return safeNetworkCall { service.getRecipesByCategoryId(categoryId) }
    }

    suspend fun getRecipeById(recipeId: Int): RecipeDto? {
        return safeNetworkCall { service.getRecipeById(recipeId) }
    }

    suspend fun getRecipeFromDbById(recipeId: Int): Recipe? {
        return recipesDao.getRecipeById(recipeId)
    }

    suspend fun getRecipesByIds(ids: Set<Int>): List<RecipeDto>? {
        return safeNetworkCall { service.getRecipesByIds(ids.joinToString(",")) }
    }

    private suspend fun <T> safeNetworkCall(block: suspend () -> T?): T? {
        return try {
            withContext(ioDispatcher) { block() }
        } catch (e: Exception) {
            // 1. Если корутина была отменена пользователем (ушел со экрана),
            // мы НЕ должны ловить эту ошибку, иначе отмена сломается.
            if (e is java.util.concurrent.CancellationException) {
                throw e
            }
            // 2. Для всех остальных ошибок (No Internet, Timeout, HTTP 500) пишем лог.
            Log.e("!!!", "Ошибка при выполнении сетевого запроса", e)
            // 3. И возвращаем null, чтобы ViewModel мог показать UI состояния ошибки.
            null
        }
    }
}