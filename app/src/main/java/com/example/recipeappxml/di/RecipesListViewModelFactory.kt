package com.example.recipeappxml.di

import com.example.recipeappxml.data.RecipesRepository
import com.example.recipeappxml.ui.recipes.recipes_list.RecipesListViewModel

class RecipesListViewModelFactory(
    //private val categoryId: Int,
    private val recipesRepository: RecipesRepository
) :
    Factory<RecipesListViewModel> {

    override fun create(): RecipesListViewModel =
        RecipesListViewModel(repository = recipesRepository)

}