package com.example.recipeappxml.ui.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.recipeappxml.data.RecipesRepository
import com.example.recipeappxml.model.Category
import com.example.recipeappxml.model.toCategory
import java.util.concurrent.Executors

class CategoriesListViewModel : ViewModel(), ViewModelProvider.Factory {

    // Backing property — приватное, изменяемое, внутреннее состояние
    private val _categoryList = MutableLiveData<CategoryListUiState>()

    // Публичное свойство — только для чтения, безопасно для UI
    val categoryList: LiveData<CategoryListUiState> get() = _categoryList

    private val repository: RecipesRepository = RecipesRepository()
    private val threadPool = Executors.newFixedThreadPool(4)

    init {
        loadList()
    }

    data class CategoryListUiState(
        val isLoading: Boolean = false,
        val categories: List<Category> = emptyList(),
        val error: Throwable? = null
    )

    //error = IllegalStateException("Ошибка загрузки") — технически работает, но класть Throwable в UiState ради строки — тяжеловато.
    // Проще было бы error: String? и передавать текст.
    // Но это уже вкус архитектуры, не ошибка — оставляйте как есть, если не хотите менять.
    fun loadList() {
        _categoryList.value = CategoryListUiState(isLoading = true)

        threadPool.execute {
            val result = repository.getCategories()

            if (result == null) {
                _categoryList.postValue(
                    CategoryListUiState(
                        error = IllegalStateException("Ошибка загрузки"),
                        isLoading = false
                    )
                )
            } else {
                _categoryList.postValue(
                    CategoryListUiState(
                        categories = result.map { it.toCategory() },
                        isLoading = false
                    )
                )
            }
        }
    }

    override fun onCleared() {
        threadPool.shutdown()
        super.onCleared()
    }
}