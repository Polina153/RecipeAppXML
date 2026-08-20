package com.example.recipeappxml.ui.recipes.recipe


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.recipeappxml.data.FavoritePrefsManager
import com.example.recipeappxml.data.RecipesRepository
import com.example.recipeappxml.model.Ingredient
import com.example.recipeappxml.model.toRecipe
import java.util.concurrent.Executors


class RecipeViewModel(application: Application) : AndroidViewModel(application) {

    // Backing property — приватное, изменяемое, внутреннее состояние
    private val mutableSelectedRecipe = MutableLiveData<RecipeState>()
    // Публичное свойство — только для чтения, безопасно для UI
    val selectedRecipe: LiveData<RecipeState> get() = mutableSelectedRecipe

    private val prefsManager = FavoritePrefsManager(getApplication())

    private val repository: RecipesRepository = RecipesRepository()
    private val threadPool = Executors.newFixedThreadPool(4)

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
        val error: String? = null
    )

    /**
     * Загружает рецепт по ID из репозитория и формирует полный стейт экрана.
     * Вызывается один раз при создании фрагмента.
     */
    fun loadRecipe(recipeId: Int) {
        threadPool.execute {
            val recipeDto = repository.getRecipeById(recipeId)
            if (recipeDto == null) {
                mutableSelectedRecipe.postValue(
                    RecipeState(error = "Не удалось загрузить рецепт")
                )
                return@execute
            }
            val recipeFromRepo = recipeDto.toRecipe()

            val finalState = RecipeState(
                title = recipeFromRepo.title,
                isFavorite = prefsManager.isFavorite(recipeId),
                portionsCount = mutableSelectedRecipe.value?.portionsCount ?: 1,
                ingredients = recipeFromRepo.ingredients,
                method = recipeFromRepo.method,
                imageUrl = recipeFromRepo.imageUrl,
            )
            mutableSelectedRecipe.postValue(finalState)
        }
    }

    fun onFavoritesClicked(recipeId: Int) {
        val currentState = mutableSelectedRecipe.value ?: return
        val newIsFavorite = !currentState.isFavorite

        val newState = currentState.copy(isFavorite = newIsFavorite)
        mutableSelectedRecipe.value = newState

        saveFavorites(recipeId, newIsFavorite)
    }

    private fun saveFavorites(recipeId: Int, isFavorite: Boolean) {
        if (isFavorite) {
            prefsManager.addToFavorites(recipeId)
        } else {
            prefsManager.removeFromFavorites(recipeId)
        }
    }

    override fun onCleared() {
        threadPool.shutdown()
        super.onCleared()
    }
}