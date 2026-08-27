package com.example.recipeappxml.data

import android.app.Application


class ApplicationClass : Application() {
    val repository: RecipesRepository by lazy {
        RecipesRepository(applicationContext)
    }
}