package com.example.recipeappxml.ui.recipes.recipe

import androidx.lifecycle.ViewModel
import com.example.recipeappxml.model.Ingredient

class RecipeViewModel : ViewModel() {

    sealed interface RecipeScreenState {
        object Loading : RecipeScreenState // Показываем спиннер
        data class Success(val data: RecipeState) : RecipeScreenState // Показываем контент
        data class Error(val message: String) : RecipeScreenState // Показываем сообщение об ошибке
    }

    data class RecipeState(
        val title: String = "",
        var isFavorite: Boolean = false,
        val ingredients: List<Ingredient> = emptyList(),
        val method: List<String> = emptyList(),
        val imageUrl: String? = null
    )


}