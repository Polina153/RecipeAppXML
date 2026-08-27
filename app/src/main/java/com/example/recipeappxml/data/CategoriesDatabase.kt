package com.example.recipeappxml.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.recipeappxml.model.CategoriesDao
import com.example.recipeappxml.model.Category

@Database(entities = [Category::class], version = 1, exportSchema = false)
abstract class CategoriesDatabase : RoomDatabase() {
    abstract fun categoriesDao(): CategoriesDao
}