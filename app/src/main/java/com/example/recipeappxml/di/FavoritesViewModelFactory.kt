package com.example.recipeappxml.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.recipeappxml.data.RecipesRepository
import com.example.recipeappxml.ui.recipes.favorites.FavoritesViewModel

class FavoritesViewModelFactory (private val recipesRepository: RecipesRepository) :
    ViewModelProvider.Factory {

    override fun <FavoritesViewModel : ViewModel> create(
        modelClass: Class<FavoritesViewModel>,
        extras: CreationExtras
    ): FavoritesViewModel { // Проверяем тип, чтобы избежать ClassCastException
        return when {
            modelClass.isAssignableFrom(FavoritesViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST") FavoritesViewModel(recipesRepository) as FavoritesViewModel
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

}