package com.example.recipeappxml.di

import com.example.recipeappxml.data.RecipesRepository
import com.example.recipeappxml.ui.recipes.favorites.FavoritesViewModel

class FavoritesViewModelFactory (private val recipesRepository: RecipesRepository) :
    Factory<FavoritesViewModel> {
    override fun create(): FavoritesViewModel {
        return FavoritesViewModel(recipesRepository)
    }

}