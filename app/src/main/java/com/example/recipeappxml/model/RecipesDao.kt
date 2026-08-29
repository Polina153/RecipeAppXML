package com.example.recipeappxml.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipesDao {
    @Query("SELECT * FROM recipes WHERE categoryId = :categoryId")
    fun getRecipesByCategory(categoryId: Int): Flow<List<Recipe>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipes: List<Recipe>)

    @Query("DELETE FROM recipes WHERE categoryId = :categoryId")
    suspend fun deleteRecipesByCategory(categoryId: Int)

    @Query("SELECT * FROM recipes WHERE isFavorite = 1")
    fun getFavoriteRecipes(): Flow <List<Recipe>>

    @Query("UPDATE recipes SET isFavorite = NOT isFavorite WHERE id = :recipeId")
    //UPDATE recipes SET isFavorite = 1 - isFavorite WHERE id = :recipeId  или
    suspend fun changeFavoriteRecipe(recipeId: Int)

    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    suspend fun getRecipeById(recipeId: Int): Recipe?

    @Query("SELECT id FROM recipes WHERE isFavorite = 1")
    suspend fun getFavoriteRecipeIds(): List<Int>
}