package com.example.recipeappxml.ui.recipes.favorites


import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.recipeappxml.data.Constants
import com.example.recipeappxml.data.RecipesRepositoryStub
import com.example.recipeappxml.model.Recipe


class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val _favoriteRecipes = MutableLiveData<List<Recipe>>()
    val favoriteRecipes: LiveData<List<Recipe>> get() = _favoriteRecipes

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        val prefs = getApplication<Application>().getSharedPreferences(
            Constants.FAVORITES_PREFS_NAME,
            Context.MODE_PRIVATE
        )
        val favoriteIds = prefs.getStringSet(Constants.FAVORITES_KEY, emptySet()) ?: emptySet()

        val recipes = favoriteIds.mapNotNull { idString ->
            idString.toIntOrNull()?.let { id ->
                RecipesRepositoryStub.getRecipeById(id)
            }
        }
        _favoriteRecipes.value = recipes
    }
}