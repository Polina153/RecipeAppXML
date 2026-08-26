package com.example.recipeappxml.data

import android.app.Application
import androidx.room.Room
import com.example.recipeappxml.model.CategoriesDao
import com.example.recipeappxml.model.CategoriesDatabase

class ApplicationClass : Application() {

    val database: CategoriesDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            CategoriesDatabase::class.java,
            "categories.db"
        ).build()
    }

    val categoriesDao: CategoriesDao by lazy { database.categoriesDao() }
    val repository: RecipesRepository by lazy { RecipesRepository(categoriesDao) }
}