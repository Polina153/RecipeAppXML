package com.example.recipeappxml.data

import com.example.recipeappxml.model.CategoryDto
import com.example.recipeappxml.model.RecipeDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RecipeApiService {
    @GET("api/category")
    suspend fun getCategories(): List<CategoryDto>

    @GET("api/category/{id}/recipes")
    suspend fun getRecipesByCategoryId(@Path("id") categoryId: Int): List<RecipeDto>

    @GET("api/recipe/{id}")
    suspend fun getRecipeById(@Path("id") recipeId: Int): RecipeDto

    @GET("api/category/{id}")
    suspend fun getCategoryById(@Path("id") categoryId: Int): CategoryDto

    @GET("api/recipes")
    suspend fun getRecipesByIds(@Query("ids") ids: String): List<RecipeDto>
}