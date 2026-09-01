package com.example.recipeappxml.data

import android.app.Application
import com.example.recipeappxml.di.AppContainer


class RecipeApplication : Application() {

    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this)
    }
}