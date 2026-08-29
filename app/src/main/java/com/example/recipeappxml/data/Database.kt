package com.example.recipeappxml.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.recipeappxml.model.CategoriesDao
import com.example.recipeappxml.model.Category
import com.example.recipeappxml.model.ConverterForRoom
import com.example.recipeappxml.model.Recipe
import com.example.recipeappxml.model.RecipesDao

@Database(entities = [Category::class, Recipe::class], version = 2, exportSchema = false)
@TypeConverters(ConverterForRoom::class)
abstract class Database : RoomDatabase() {
    abstract fun categoriesDao(): CategoriesDao
    abstract fun recipesDao(): RecipesDao
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `recipes` (
                `id` INTEGER NOT NULL,
                `categoryId` INTEGER,
                `title` TEXT NOT NULL,
                `ingredients` TEXT NOT NULL,
                `method` TEXT NOT NULL,
                `imageUrl` TEXT NOT NULL,
                PRIMARY KEY(`id`)
            )
            """.trimIndent()
        )
    }
}