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

@Database(entities = [Category::class, Recipe::class], version = 3, exportSchema = false)
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

// Новая миграция 2 -> 3
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Добавляем булево поле. INTEGER + NOT NULL + DEFAULT 0 гарантирует,
        // что у старых записей будет false (0), а новые записи обязаны иметь значение.
        db.execSQL("ALTER TABLE recipes ADD COLUMN isFavorite INTEGER NOT NULL DEFAULT 0;")
    }
}