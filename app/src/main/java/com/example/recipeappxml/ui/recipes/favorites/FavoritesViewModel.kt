package com.example.recipeappxml.ui.recipes.favorites


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.recipeappxml.data.FavoritePrefsManager
import com.example.recipeappxml.data.RecipesRepositoryStub
import com.example.recipeappxml.model.Recipe


class FavoritesViewModel(val prefsManager: FavoritePrefsManager) : ViewModel(), ViewModelProvider.Factory {

    private val _favoriteRecipes = MutableLiveData<FavoritesUiState>()
    val favoriteRecipes: LiveData<FavoritesUiState> get() = _favoriteRecipes

    init {
        loadFavorites()
    }

    data class FavoritesUiState(
        val isLoading: Boolean = false,
        val recipes: List<Recipe> = emptyList(),
        val error: Throwable? = null
    )

    fun loadFavorites() {

        _favoriteRecipes.value = FavoritesUiState(isLoading = true)

        try {
            val favoriteIds = prefsManager.getAllFavorites()

            val recipes = favoriteIds.mapNotNull { idString ->
                idString.toIntOrNull()?.let { id ->
                    RecipesRepositoryStub.getRecipeById(id)
                }
            }

            _favoriteRecipes.value = FavoritesUiState(recipes = recipes, isLoading = false)
        } catch (e: Exception) {
            _favoriteRecipes.value = FavoritesUiState(error = e, isLoading = false)
        }
    }
}