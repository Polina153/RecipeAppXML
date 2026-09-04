package com.example.recipeappxml.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeappxml.data.RecipesRepository
import com.example.recipeappxml.model.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesListViewModel @Inject constructor(val repository: RecipesRepository) : ViewModel() {
    // --- НОВОЕ: Отдельный поток для событий ошибки ---
    private val _errorEvent = MutableSharedFlow<Throwable>()
    val errorEvent = _errorEvent.asSharedFlow()

    // Основной UI State строится ТОЛЬКО на базе + флаге загрузки
    private val _isRefreshing = MutableStateFlow(true)

    // Используем StateFlow вместо LiveData. Это "горячий" поток, хранящий последнее состояние.
    val categoryListUiState: StateFlow<CategoryListUiState> =
        combine(
            repository.getCategoriesFromCache(),
            _isRefreshing
        ) { cachedList, isLoading ->
            CategoryListUiState(
                isLoading = isLoading,
                categories = cachedList,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CategoryListUiState(isLoading = true)
        )

    data class CategoryListUiState(
        val isLoading: Boolean = false,
        val categories: List<Category> = emptyList(),
        // Поле error убрано отсюда, чтобы не мешать объединению потоков
    )

    fun triggerRefresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            val isDatabaseEmpty =
                repository.getCategoriesFromCache().first()
                    .isEmpty()
            val networkResult =
                repository.fetchCategoriesFromNetwork()
            if (networkResult != null) {
                repository.saveCategoriesToDb(networkResult)
            } else {
                if (isDatabaseEmpty) {
                    _errorEvent.emit(Throwable("Не удалось загрузить категории"))
                }
            }
            _isRefreshing.value = false
        }
    }
}
