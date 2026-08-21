package com.example.recipeappxml.ui.recipes.favorites


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.recipeappxml.data.FavoritePrefsManager
import com.example.recipeappxml.data.RecipesRepository
import com.example.recipeappxml.model.Recipe
import com.example.recipeappxml.model.toRecipe
import kotlinx.coroutines.launch


class FavoritesViewModel(val prefsManager: FavoritePrefsManager) : ViewModel(),
    ViewModelProvider.Factory {

    private val _favoriteRecipes = MutableLiveData<FavoritesUiState>()
    val favoriteRecipes: LiveData<FavoritesUiState> get() = _favoriteRecipes
    private val repository: RecipesRepository = RecipesRepository()

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

        viewModelScope.launch {
            val favoriteIds = prefsManager.getAllFavorites().mapNotNull {
                it.toIntOrNull()
            }.toSet()

            val recipes = repository.getRecipesByIds(favoriteIds)

            val state = if (recipes == null) {
                FavoritesUiState(
                    error = IllegalStateException("Не удалось загрузить избранное"),
                    isLoading = false
                )
            } else {
                FavoritesUiState(
                    recipes = recipes.map { it.toRecipe() },
                    isLoading = false
                )
            }
            _favoriteRecipes.value = state
        }
    }
}