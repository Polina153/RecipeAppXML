package com.example.recipeappxml.ui.recipes.recipe


import android.app.Application
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.recipeappxml.data.Constants
import com.example.recipeappxml.data.RecipesRepositoryStub
import com.example.recipeappxml.model.Ingredient


class RecipeViewModel(application: Application) : AndroidViewModel(application) {

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
        var isFavorite: Boolean = false,
        var portionsCount: Int = 1,
        val ingredients: List<Ingredient> = emptyList(),
        val method: List<String> = emptyList(),
        val recipeImage: Drawable? = null,
        val imageUrl: String? = null
    )

    /**
     * Загружает рецепт по ID из репозитория и формирует полный стейт экрана.
     * Вызывается один раз при создании фрагмента.
     */
    fun loadRecipe(recipeId: Int) {
        // TODO: Заменить заглушку на реальный вызов API/БД через корутины
        // Для примера используем синхронный вызов stub-репозитория
        try {
            val recipeFromRepo = RecipesRepositoryStub.getRecipeById(recipeId)
            if (recipeFromRepo != null) {
                // Получаем актуальный статус избранного
                val favoritesSet = getFavorites()

                // Создаем итоговый объект состояния, проинициализировав нужные поля
                val finalState = RecipeState(
                    title = recipeFromRepo.title,
                    isFavorite = favoritesSet.contains(recipeId.toString()), // 1. Проверка избранного
                    portionsCount = mutableSelectedRecipe.value?.portionsCount ?: 1,
                    ingredients = recipeFromRepo.ingredients,
                    method = recipeFromRepo.method,
                    recipeImage = loadRecipeImage(recipeFromRepo.imageUrl),
                    imageUrl = recipeFromRepo.imageUrl
                )
                mutableSelectedRecipe.value = finalState
            }
        } catch (e: Exception) {
            Log.e("!!!", "Ошибка загрузки рецепта", e)
        }
    }

    private fun loadRecipeImage(imageUrl: String?): Drawable? {
        if (imageUrl == null) return null
        return try {
            val inputStream = getApplication<Application>().assets.open(imageUrl)
            Drawable.createFromStream(inputStream, null)
        } catch (e: Exception) {
            Log.e("!!!", "Изображение не загрузилось: $e")
            null
        }
    }

    fun getFavorites(): MutableSet<String> {
        val sharedPref =
            getApplication<Application>().applicationContext.getSharedPreferences(
                Constants.FAVORITES_PREFS_NAME,
                Context.MODE_PRIVATE
            )
        val storedSet = sharedPref.getStringSet(Constants.FAVORITES_KEY, emptySet()) ?: emptySet()
        return HashSet(storedSet)
    }

    fun onFavoritesClicked(recipeId: Int) {
        val currentState = mutableSelectedRecipe.value ?: return
        val newIsFavorite = !currentState.isFavorite

        val newState = currentState.copy(isFavorite = newIsFavorite)
        mutableSelectedRecipe.value = newState

        saveFavorites(recipeId.toString(), newIsFavorite)
    }

    private fun saveFavorites(recipeId: String, isFavorite: Boolean) {
        val prefs = getApplication<Application>().getSharedPreferences(
            Constants.FAVORITES_PREFS_NAME,
            Context.MODE_PRIVATE
        )
        val favorites = getFavorites().toMutableSet()
        if (isFavorite) favorites.add(recipeId) else favorites.remove(recipeId)
        prefs.edit { putStringSet(Constants.FAVORITES_KEY, favorites) }
    }

}