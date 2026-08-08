package com.example.recipeappxml.ui.recipes.recipes_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.recipeappxml.data.RecipesRepositoryStub
import com.example.recipeappxml.model.Recipe

class RecipesListViewModel(private val categoryId: Int) : ViewModel() {

    // Backing property — приватное, изменяемое, внутреннее состояние
    private val _recipeList = MutableLiveData<RecipeListUiState>()

    // Публичное свойство — только для чтения, безопасно для UI
    val recipeList: LiveData<RecipeListUiState> get() = _recipeList

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

        try {
            val recipeList = RecipesRepositoryStub.getRecipesByCategoryId(categoryId)

            _recipeList.value = RecipeListUiState(recipes = recipeList, isLoading = false)
        } catch (e: Exception) {
            _recipeList.value = RecipeListUiState(error = e, isLoading = false)
        }
    }
}