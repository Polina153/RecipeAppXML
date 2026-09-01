package com.example.recipeappxml.di

import com.example.recipeappxml.data.RecipesRepository
import com.example.recipeappxml.ui.recipes.recipe.RecipeViewModel

class RecipeViewModelFactory(private val recipesRepository: RecipesRepository) :
    Factory<RecipeViewModel> {

    override fun create(): RecipeViewModel =
        RecipeViewModel(recipesRepository)

}