package com.example.recipeappxml.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.recipeappxml.data.RecipesRepository
import com.example.recipeappxml.ui.recipes.recipes_list.RecipesListViewModel

class RecipesListViewModelFactory(
    private val categoryId: Int,
    private val recipesRepository: RecipesRepository
) :
    ViewModelProvider.Factory {

    override fun <RecipesListViewModel : ViewModel> create(
        modelClass: Class<RecipesListViewModel>,
        extras: CreationExtras
    ): RecipesListViewModel { // Проверяем тип, чтобы избежать ClassCastException
        return when {
            modelClass.isAssignableFrom(RecipesListViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST") RecipesListViewModel(
                    categoryId,
                    recipesRepository
                ) as RecipesListViewModel
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

}