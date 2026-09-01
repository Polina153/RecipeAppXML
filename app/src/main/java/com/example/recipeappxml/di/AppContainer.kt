package com.example.recipeappxml.di

import android.content.Context
import androidx.room.Room
import com.example.recipeappxml.data.Database
import com.example.recipeappxml.data.MIGRATION_1_2
import com.example.recipeappxml.data.MIGRATION_2_3
import com.example.recipeappxml.data.RecipeApiService
import com.example.recipeappxml.data.RecipesRepository
import com.example.recipeappxml.model.CategoriesDao
import com.example.recipeappxml.model.RecipesDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer(context: Context) {

    val database: Database by lazy {
        Room.databaseBuilder(
            context,
            Database::class.java,
            "categories.db"
        ).addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .build()
    }

    private val categoriesDao: CategoriesDao by lazy { database.categoriesDao() }
    private val recipesDao: RecipesDao by lazy { database.recipesDao() }
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://recipes.androidsprint.ru/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service: RecipeApiService =
        retrofit.create(RecipeApiService::class.java)

    val repository: RecipesRepository by lazy {
        RecipesRepository(
            recipesDao = recipesDao,
            categoriesDao = categoriesDao,
            service  = service,
            iODispatcher = ioDispatcher,
        )
    }
}