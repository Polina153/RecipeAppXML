package com.example.recipeappxml.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.recipeappxml.data.RecipesRepository
import com.example.recipeappxml.ui.categories.CategoriesListViewModel

class CategoriesListViewModelFactory(private val recipesRepository: RecipesRepository) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return if (modelClass.isAssignableFrom(CategoriesListViewModel::class.java)) {
            CategoriesListViewModel(recipesRepository) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

}
