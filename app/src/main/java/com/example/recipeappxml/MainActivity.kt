package com.example.recipeappxml

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import com.example.recipeappxml.databinding.ActivityMainBinding
import com.example.recipeappxml.model.CategoryDto
import com.example.recipeappxml.model.RecipeDto
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors.newFixedThreadPool

private const val TAG = "NetworkLesson"

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = requireNotNull(_binding)

    // URL вашего тестового API категорий
    private val apiUrl = "https://recipes.androidsprint.ru/api/category"
    private val apiRecipeId = "https://recipes.androidsprint.ru/api/category"
    private val threadPool = newFixedThreadPool(10)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Log.d(TAG, "Метод onCreate() выполняется на потоке: ${Thread.currentThread().name}")
        binding.categoriesButton.setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(R.id.categoriesListFragment)
        }

        binding.favoritesButton.setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(R.id.favoritesFragment)
        }

        threadPool.execute {
            try {
                Log.d(TAG, "Выполняю запрос на потоке: ${Thread.currentThread().name}")

                // Вызываем функцию запроса
                val categories = fetchCategories(apiUrl)

                // Здесь можно обновить UI через runOnUiThread или вернуться в Coroutine Dispatcher.Main
                Log.d(TAG, "Успешно получено данных: ${categories.size} шт.")
                getRecipes(categories)

            } catch (e: Exception) {
                Log.e(TAG, "Ошибка при выполнении запроса", e)
            }
        }
    }

    private fun getRecipes(categories: List<CategoryDto>) {
        val idList = categories.map { it.id }
        idList.forEach { threadPool.execute { fetchRecipe(it) } }
    }

    private fun fetchRecipe(categoryId: Int) {
        var urlConnection: HttpURLConnection? = null
        try {
            val url = URL("${apiRecipeId}/$categoryId/recipes")
            val connection = url.openConnection()
            urlConnection = requireNotNull(connection as? HttpURLConnection) {
                "Unexpected URLConnection type for $url: ${connection::class.java}"
            }

            urlConnection.requestMethod = "GET"
            urlConnection.connectTimeout = 10000
            urlConnection.readTimeout = 15000

            if (urlConnection.responseCode == HttpURLConnection.HTTP_OK) {
                // inputStream.bufferedReader().use { ... } гарантирует закрытие ридера В ЛЮБОМ СЛУЧАЕ
                // даже если внутри блока произойдет исключение (OutOfMemoryError, SerializationException и т.д.)
                val responseText = urlConnection.inputStream.bufferedReader().use { reader ->
                    reader.readText()
                }
                Log.d(TAG, "Тело responseText: $responseText.")
                // Десериализация JSON -> Kotlin Objects
                val recipes = json.decodeFromString<List<RecipeDto>>(responseText)
                Log.d(TAG, "Категория $categoryId: получено ${recipes.size} рецептов")

            } else {
                throw Exception("HTTP Error: ${urlConnection.responseCode}. Message: ${urlConnection.responseMessage}")
            }

        } catch (e: Exception) {
            Log.e(TAG, "Ошибка для категории $categoryId", e)
        } finally {
            // Соединение нужно закрывать всегда, независимо от успеха или ошибки выше
            urlConnection?.disconnect()
        }
    }

    /**
     * Функция выполнения HTTP-запроса.
     * Возвращает список объектов DTO.
     */
    private fun fetchCategories(urlString: String): List<CategoryDto> {
        var urlConnection: HttpURLConnection? = null

        return try {
            val url = URL(urlString)
            val connection = url.openConnection()
            urlConnection = requireNotNull(connection as? HttpURLConnection) {
                "Unexpected URLConnection type for $url: ${connection::class.java}"
            }

            urlConnection.requestMethod = "GET"
            urlConnection.connectTimeout = 10000
            urlConnection.readTimeout = 15000

            if (urlConnection.responseCode == HttpURLConnection.HTTP_OK) {
                // inputStream.bufferedReader().use { ... } гарантирует закрытие ридера В ЛЮБОМ СЛУЧАЕ
                // даже если внутри блока произойдет исключение (OutOfMemoryError, SerializationException и т.д.)
                val responseText = urlConnection.inputStream.bufferedReader().use { reader ->
                    reader.readText()
                }
                Log.d(TAG, "Тело responseText: $responseText.")
                // Десериализация JSON -> Kotlin Objects
                json.decodeFromString<List<CategoryDto>>(responseText)

            } else {
                throw Exception("HTTP Error: ${urlConnection.responseCode}. Message: ${urlConnection.responseMessage}")
            }

        } finally {
            // Соединение нужно закрывать всегда, независимо от успеха или ошибки выше
            urlConnection?.disconnect()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        threadPool.shutdown()
    }

    companion object {
        private val json = Json { ignoreUnknownKeys = true }
    }
}