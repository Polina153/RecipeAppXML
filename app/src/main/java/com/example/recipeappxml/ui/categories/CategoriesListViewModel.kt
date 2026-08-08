package com.example.recipeappxml.ui.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.recipeappxml.data.RecipesRepositoryStub
import com.example.recipeappxml.model.Category

class CategoriesListViewModel: ViewModel() {

    // Backing property — приватное, изменяемое, внутреннее состояние
    private val _categoryList = MutableLiveData<CategoryListUiState>()

    // Публичное свойство — только для чтения, безопасно для UI
    val categoryList: LiveData<CategoryListUiState> get() = _categoryList

    init {
        loadList()
    }

    data class CategoryListUiState(
        val isLoading: Boolean = false,
        val categories: List<Category> = emptyList(),
        val error: Throwable? = null
    )

    fun loadList() {

        _categoryList.value = CategoryListUiState(isLoading = true)

        try {
            val categoryList = RecipesRepositoryStub.getCategories()

            _categoryList.value = CategoryListUiState(categories = categoryList, isLoading = false)
        } catch (e: Exception) {
            _categoryList.value = CategoryListUiState(error = e, isLoading = false)
        }
    }
}