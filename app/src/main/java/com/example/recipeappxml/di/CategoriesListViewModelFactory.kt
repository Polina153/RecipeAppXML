package com.example.recipeappxml.di

import com.example.recipeappxml.data.RecipesRepository
import com.example.recipeappxml.ui.categories.CategoriesListViewModel

class CategoriesListViewModelFactory(
    private val recipesRepository: RecipesRepository
) : Factory<CategoriesListViewModel> {

    override fun create(): CategoriesListViewModel =
        CategoriesListViewModel(recipesRepository)
}