package com.example.recipeappxml.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class GenericViewModelFactory<T : ViewModel>(
    private val creator: () -> T
) : ViewModelProvider.Factory {

    // Здесь нет @Suppress("UNCHECKED_CAST"), потому что мы гарантируем создание нужного типа
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return creator() as T
    }
}