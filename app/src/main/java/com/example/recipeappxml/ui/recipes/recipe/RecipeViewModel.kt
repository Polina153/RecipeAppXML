package com.example.recipeappxml.ui.recipes.recipe


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeappxml.data.RecipesRepository
import com.example.recipeappxml.model.Ingredient
import com.example.recipeappxml.model.toRecipe
import kotlinx.coroutines.launch


class RecipeViewModel(val repository: RecipesRepository) : ViewModel() {

    // Backing property — приватное, изменяемое, внутреннее состояние
    private val mutableSelectedRecipe = MutableLiveData<RecipeState>()

    // Публичное свойство — только для чтения, безопасно для UI
    val selectedRecipe: LiveData<RecipeState> get() = mutableSelectedRecipe

    init {
        selectRecipe(RecipeState())
    }

    fun selectRecipe(recipe: RecipeState) {
        mutableSelectedRecipe.value = recipe
    }

    fun onPortionsCountChanged(count: Int) {
        val current = mutableSelectedRecipe.value ?: return
        mutableSelectedRecipe.value = current.copy(portionsCount = count)
    }

    /*
        sealed interface RecipeScreenState {
            object Loading : RecipeScreenState // Показываем спиннер
            data class Success(val data: RecipeState) : RecipeScreenState // Показываем контент
            data class Error(val message: String) : RecipeScreenState // Показываем сообщение об ошибке
        }*/

    data class RecipeState(
        val title: String = "",
        val isFavorite: Boolean = false,
        val portionsCount: Int = 1,
        val ingredients: List<Ingredient> = emptyList(),
        val method: List<String> = emptyList(),
        val imageUrl: String? = null,
        val error: String? = null,
    )

    /**
     * Загружает рецепт по ID из репозитория и формирует полный стейт экрана.
     * Вызывается один раз при создании фрагмента.
     */
    fun loadRecipe(recipeId: Int) {
        viewModelScope.launch {
            val recipeDto = repository.getRecipeById(recipeId)
            if (recipeDto == null) {
                mutableSelectedRecipe.value = RecipeState(error = "Не удалось загрузить рецепт")
                return@launch
            }
            val recipeFromRepo = recipeDto.toRecipe()
            val recipeFromDb = repository.getRecipeFromDbById(recipeId)

            val finalState = RecipeState(
                title = recipeFromRepo.title,
                isFavorite = recipeFromDb?.isFavorite ?: false,
                portionsCount = mutableSelectedRecipe.value?.portionsCount ?: 1,
                ingredients = recipeFromRepo.ingredients,
                method = recipeFromRepo.method,
                imageUrl = recipeFromRepo.imageUrl,
            )
            mutableSelectedRecipe.value = finalState
        }
    }

    fun onFavoritesClicked(recipeId: Int) {
        val currentState = mutableSelectedRecipe.value ?: return
        mutableSelectedRecipe.value = currentState.copy(isFavorite = !currentState.isFavorite)
        viewModelScope.launch { repository.changeFavorite(recipeId) }
    }
}