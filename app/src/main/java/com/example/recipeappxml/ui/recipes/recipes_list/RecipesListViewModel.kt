package com.example.recipeappxml.ui.recipes.recipes_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.recipeappxml.data.RecipesRepository
import com.example.recipeappxml.model.Recipe
import com.example.recipeappxml.model.toRecipe
import java.util.concurrent.Executors

class RecipesListViewModel(private val categoryId: Int) : ViewModel(), ViewModelProvider.Factory {

    // Backing property — приватное, изменяемое, внутреннее состояние
    private val _recipeList = MutableLiveData<RecipeListUiState>()

    // Публичное свойство — только для чтения, безопасно для UI
    val recipeList: LiveData<RecipeListUiState> get() = _recipeList

    private val repository: RecipesRepository = RecipesRepository()
    private val threadPool = Executors.newFixedThreadPool(4)

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

        threadPool.execute {
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
            _recipeList.postValue(state)
        }
    }

    override fun onCleared() {
        threadPool.shutdown()
        super.onCleared()
    }
}