package com.example.recipeappxml.ui.recipes.recipes_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeappxml.data.RecipesRepository
import com.example.recipeappxml.model.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class RecipesListViewModel(private val categoryId: Int, val repository: RecipesRepository) :
    ViewModel() {

    private val _uiState = MutableStateFlow(RecipeListUiState(isLoading = true))
    val recipeList: StateFlow<RecipeListUiState> = _uiState.asStateFlow()

    init {
        loadList()
    }

    data class RecipeListUiState(
        val isLoading: Boolean = false,
        val recipes: List<Recipe> = emptyList(),
        val error: Throwable? = null
    )

    fun loadList() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val cached = repository.getRecipesByCategoryFromCache(categoryId).first()
            _uiState.value = _uiState.value.copy(recipes = cached, isLoading = false, error = null)
            val networkResult = repository.getRecipesByCategoryIdFromNetwork(categoryId)
            if (networkResult != null) {
                repository.saveRecipesToDb(networkResult, categoryId)
                _uiState.value = _uiState.value.copy(
                    recipes = repository.getRecipesByCategoryFromCache(categoryId).first(),
                    isLoading = false,
                    error = null
                )
            } else {
                if (cached.isEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        error = IllegalStateException("Не удалось загрузить рецепты"),
                        isLoading = false,
                    )
                }
            }
        }
    }
}