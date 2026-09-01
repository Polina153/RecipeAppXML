package com.example.recipeappxml.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.recipeappxml.data.RecipesRepository
import com.example.recipeappxml.ui.recipes.recipe.RecipeViewModel

class RecipeViewModelFactory(private val recipesRepository: RecipesRepository) :
    ViewModelProvider.Factory {


    override fun <RecipeViewModel : ViewModel> create(
        modelClass: Class<RecipeViewModel>,
        extras: CreationExtras
    ): RecipeViewModel { // Проверяем тип, чтобы избежать ClassCastException
        return when {
            modelClass.isAssignableFrom(RecipeViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST") RecipeViewModel(
                    recipesRepository
                ) as RecipeViewModel
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}