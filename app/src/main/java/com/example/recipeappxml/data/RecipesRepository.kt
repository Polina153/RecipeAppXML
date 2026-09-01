package com.example.recipeappxml.data

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

class RecipesRepository(
    val recipesDao: RecipesDao,
    val categoriesDao: CategoriesDao,
    val service: RecipeApiService,
    val iODispatcher: CoroutineDispatcher
) {

    fun getCategoriesFromCache(): Flow<List<Category>> = categoriesDao.getAllCategories()

    fun getRecipesByCategoryFromCache(categoryId: Int): Flow<List<Recipe>> =
        recipesDao.getRecipesByCategory(categoryId)

    fun getFavoriteRecipes(): Flow<List<Recipe>> = recipesDao.getFavoriteRecipes()

    suspend fun changeFavorite(recipeId: Int) = recipesDao.changeFavoriteRecipe(recipeId)

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

    // СОХРАНЕНИЕ В БАЗУ: Room сам выполнит insert в IO-потоке благодаря suspend-функции DAO.
    suspend fun saveRecipesToDb(recipes: List<RecipeDto>, categoryId: Int) {
        withContext(Dispatchers.IO) {
            val favoriteIdsFromCache = recipesDao.getFavoriteRecipeIds().toSet()
            recipesDao.deleteRecipesByCategory(categoryId)
            recipesDao.insertRecipes(recipes.map { it.toRecipe(categoryId, favoriteIdsFromCache) })
        }
    }

    suspend fun getRecipesByCategoryIdFromNetwork(categoryId: Int): List<RecipeDto>? {
        return safeCall { service.getRecipesByCategoryId(categoryId) }
    }

    suspend fun getRecipeById(recipeId: Int): RecipeDto? {
        return safeCall { service.getRecipeById(recipeId) }
    }

    suspend fun getRecipeFromDbById(recipeId: Int): Recipe? {
        return recipesDao.getRecipeById(recipeId)
    }

    suspend fun getRecipesByIds(ids: Set<Int>): List<RecipeDto>? {
        return safeCall { service.getRecipesByIds(ids.joinToString(",")) }
    }
//safeCall глотает все Exception и возвращает null. Работает, но теряется диагностика:
// вы не узнаете, упал ли сервер, отвалилась ли сеть или пришёл 500.
// Для этой задачи допустимо, но имейте в виду: в боевом проекте такую обёртку обычно сужают или
// пробрасывают ошибку наверх.

    private suspend fun <T> safeCall(block: suspend () -> T?): T? = try {
        withContext(iODispatcher) {
            block()
        }
    } catch (e: Exception) {
        null
    }
}