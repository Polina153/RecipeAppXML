package com.example.recipeappxml.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.recipeappxml.data.RecipesRepository
import com.example.recipeappxml.ui.categories.CategoriesListViewModel

class CategoriesListViewModelFactory(private val recipesRepository: RecipesRepository) :
    ViewModelProvider.Factory {

    override fun <CategoriesListViewModel : ViewModel> create(
        modelClass: Class<CategoriesListViewModel>,
        extras: CreationExtras
    ): CategoriesListViewModel { // Проверяем тип, чтобы избежать ClassCastException
        return when {
            modelClass.isAssignableFrom(CategoriesListViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST") CategoriesListViewModel(recipesRepository) as CategoriesListViewModel
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

}