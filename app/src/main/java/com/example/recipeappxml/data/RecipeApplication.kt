package com.example.recipeappxml.data

import android.app.Application
import com.example.recipeappxml.di.AppContainer


class RecipeApplication : Application() {

    val appContainer: AppContainer by lazy{
        AppContainer(this)
    }
}