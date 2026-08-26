package com.example.recipeappxml.model

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Category::class], version = 1, exportSchema = false)
abstract class CategoriesDatabase : RoomDatabase() {
    abstract fun categoriesDao(): CategoriesDao
}