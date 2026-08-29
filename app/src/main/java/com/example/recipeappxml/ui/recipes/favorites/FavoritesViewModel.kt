package com.example.recipeappxml.ui.recipes.favorites


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.recipeappxml.data.ApplicationClass
import com.example.recipeappxml.data.RecipesRepository
import com.example.recipeappxml.model.Recipe
import kotlinx.coroutines.launch


class FavoritesViewModel(application: Application) :
    AndroidViewModel(application) {

    private val _favoriteRecipes = MutableLiveData<FavoritesUiState>()
    val favoriteRecipes: LiveData<FavoritesUiState> get() = _favoriteRecipes
    private val repository: RecipesRepository = (application as ApplicationClass).repository

    init {
        _favoriteRecipes.value = FavoritesUiState(isLoading = true)

        viewModelScope.launch {
            repository.getFavoriteRecipes().collect { recipes ->
                _favoriteRecipes.value = FavoritesUiState(
                    recipes = recipes,
                    isLoading = false
                )
            }
        }
    }

    data class FavoritesUiState(
        val isLoading: Boolean = false,
        val recipes: List<Recipe> = emptyList(),
        val error: Throwable? = null
    )
}