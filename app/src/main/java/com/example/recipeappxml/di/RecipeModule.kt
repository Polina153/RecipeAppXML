package com.example.recipeappxml.di

import android.content.Context
import androidx.room.Room
import com.example.recipeappxml.data.Database
import com.example.recipeappxml.data.MIGRATION_1_2
import com.example.recipeappxml.data.MIGRATION_2_3
import com.example.recipeappxml.data.RecipeApiService
import com.example.recipeappxml.model.CategoriesDao
import com.example.recipeappxml.model.RecipesDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
class RecipeModule {

    @Provides
    fun provideDatabase(@ApplicationContext context: Context): Database =
        Room.databaseBuilder(
            context,
            Database::class.java,
            "categories.db"
        ).addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .build()

    @Provides
    fun provideCategoriesDao(database: Database): CategoriesDao = database.categoriesDao()

    @Provides
    fun provideRecipesDao(database: Database): RecipesDao = database.recipesDao()

    @Provides
    //@Singleton
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl("https://recipes.androidsprint.ru/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    fun provideService(retrofit: Retrofit): RecipeApiService =
        retrofit.create(RecipeApiService::class.java)
}