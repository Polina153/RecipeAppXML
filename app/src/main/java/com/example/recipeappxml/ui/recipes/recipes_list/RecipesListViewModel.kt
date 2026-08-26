package com.example.recipeappxml.ui.recipes.recipes_list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.recipeappxml.data.ApplicationClass
import com.example.recipeappxml.data.RecipesRepository
import com.example.recipeappxml.model.Recipe
import com.example.recipeappxml.model.toRecipe
import kotlinx.coroutines.launch

class RecipesListViewModel(private val categoryId: Int, application: Application): AndroidViewModel(application) {

    // Backing property — приватное, изменяемое, внутреннее состояние
    private val _recipeList = MutableLiveData<RecipeListUiState>()

    // Публичное свойство — только для чтения, безопасно для UI
    val recipeList: LiveData<RecipeListUiState> get() = _recipeList

    private val repository: RecipesRepository = (application as ApplicationClass).repository

    init {
        loadList()
    }

    data class RecipeListUiState(
        val isLoading: Boolean = false,
        val recipes: List<Recipe> = emptyList(),
        val error: Throwable? = null
    )

    fun loadList() {

        _recipeList.value = RecipeListUiState(isLoading = true)

        viewModelScope.launch {
            val recipeList = repository.getRecipesByCategoryId(categoryId)

            val state = if (recipeList == null) {
                RecipeListUiState(
                    error = IllegalStateException("Не удалось загрузить рецепты"),
                    isLoading = false
                )
            } else {
                RecipeListUiState(
                    recipes = recipeList.map { it.toRecipe() },
                    isLoading = false
                )
            }
            _recipeList.value = state
        }
    }
}