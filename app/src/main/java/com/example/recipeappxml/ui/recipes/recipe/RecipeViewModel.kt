package com.example.recipeappxml.ui.recipes.recipe


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.recipeappxml.model.Ingredient


class RecipeViewModel : ViewModel() {

    // Backing property — приватное, изменяемое, внутреннее состояние
    private val mutableSelectedRecipe = MutableLiveData<RecipeState>()
    // Публичное свойство — только для чтения, безопасно для UI
    val selectedRecipe: LiveData<RecipeState> get() = mutableSelectedRecipe

    init {
        Log.i("!!!", "ViewModel инициализирована")
        selectRecipe(RecipeState())
    }

    fun selectRecipe(recipe: RecipeState) {
        mutableSelectedRecipe.value = recipe
    }


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