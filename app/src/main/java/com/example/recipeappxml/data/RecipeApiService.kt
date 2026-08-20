package com.example.recipeappxml.data

import com.example.recipeappxml.model.CategoryDto
import com.example.recipeappxml.model.RecipeDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RecipeApiService {
    @GET("api/category")
    fun getCategories(): Call<List<CategoryDto>>

    @GET("api/category/{id}/recipes")
    fun getRecipesByCategoryId(@Path("id") categoryId: Int): Call<List<RecipeDto>>

    @GET("api/recipe/{id}")
    fun getRecipeById(@Path("id") recipeId: Int): Call<RecipeDto>

    @GET("api/category/{id}")
    fun getCategoryById(@Path("id") categoryId: Int): Call<CategoryDto>

    @GET("api/recipes")
    fun getRecipesByIds(@Query("ids") ids: String): Call<List<RecipeDto>>
}