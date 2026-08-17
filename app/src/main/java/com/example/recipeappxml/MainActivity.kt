package com.example.recipeappxml

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import com.example.recipeappxml.databinding.ActivityMainBinding
import com.example.recipeappxml.model.CategoryDto
import com.example.recipeappxml.model.RecipeDto
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import java.util.concurrent.Executors.newFixedThreadPool

private const val TAG = "NetworkLesson"

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = requireNotNull(_binding)

    // URL вашего тестового API категорий
    private val apiUrl = "https://recipes.androidsprint.ru/api/category"
    private val apiRecipeId = "https://recipes.androidsprint.ru/api/category"
    private val threadPool = newFixedThreadPool(10)
    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor { Log.d(TAG, "$it") }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }).build()
        //FIXME Смотрите, что произойдёт при HTTP-ошибке категорий. Сервер ответил 500, сработала ветка в fetchCategories:
        //
        //showError("Сервер временно недоступен, попробуйте позже") // тост №1 throw IOException(...) // летит вверх
        //Исключение улетает в catch в onCreate, а там:
        //
        //showError("Проверьте интернет-соединение") // тост №2
        //Итог: пользователь увидит два тоста подряд — «Сервер временно недоступен» и «Проверьте интернет-соединение». Причём второй противоречит первому. Вместо ясности — каша.
        //
        //Корень проблемы: вы ловите все исключения одним catch и не различаете «сети нет» и «сервер ответил ошибкой». А это разные сценарии с разными текстами.
        //
        //Как развести, минимально и честно:
        //
        //1. Создайте маленький тип для HTTP-ошибки, чтобы отличать её от сетевой:
        //
        //class HttpException(val code: Int, message: String) : Exception(message)
        //2. В HTTP-ветках кидайте его (и не показывайте тост внутри fetchCategories):
        //
        //if (!response.isSuccessful) { throw HttpException(response.code, "HTTP Error: ${response.code}") }
        //3. В onCreate разведите catch:
        //
        //} catch (e: HttpException) { Log.e(TAG, "HTTP-ошибка", e) showError("Сервер временно недоступен, попробуйте позже") } catch (e: Exception) { Log.e(TAG, "Ошибка при выполнении запроса", e) showError("Проверьте интернет-соединение") }
        //Теперь при 500 — один тост «Сервер недоступен», при отсутствии сети — один тост «Проверьте интернет». Никаких противоречий.

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
                if (categories.isEmpty()) {
                    showError("Данные не найдены")
                    return@execute
                }
                getRecipes(categories)

            } catch (e: Exception) {
                Log.e(TAG, "Ошибка при выполнении запроса", e)
                showError("Проверьте интернет-соединение")
            }
        }
    }

    private fun getRecipes(categories: List<CategoryDto>) {
        val idList = categories.map { it.id }
        idList.forEach { threadPool.execute { fetchRecipe(it) } }
    }

    private fun fetchRecipe(categoryId: Int) {
        try {
            val request = Request.Builder()
                .url("${apiRecipeId}/$categoryId/recipes")
                .get()
                .build()

            client.newCall(request).execute().use { response ->

                if (!response.isSuccessful) {
                    //showError("Проверьте интернет-соединение")
                    Log.e(TAG, "HTTP Error for Category $categoryId: ${response.code}")
                    throw IOException("HTTP Error for Category $categoryId: ${response.code}")
                }

                val jsonString = response.body?.string()
                    ?: throw IOException("Body is null for recipe $categoryId")

                Log.d(TAG, "Тело responseText Recipes ($categoryId): $jsonString.")

                val recipes =
                    json.decodeFromString<List<RecipeDto>>(jsonString)

                Log.d(TAG, "Категория $categoryId: получено ${recipes.size} рецептов")
                if (recipes.isEmpty()) {
                    Log.w(TAG, "Категория $categoryId вернулась пустой")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка для категории $categoryId", e)
        }
    }

    private fun fetchCategories(urlString: String): List<CategoryDto> {
        val request = Request.Builder()
            .url(urlString)
            .get()
            .build()

        client.newCall(request).execute().use { response ->

            if (!response.isSuccessful) {
                showError("Сервер временно недоступен, попробуйте позже")
                throw IOException("HTTP Error: ${response.code}. Message: ${response.message}")
            }

            // Безопасное чтение тела. use{} закроет поток автоматически.
            val jsonString =
                response.body?.string() ?: throw IOException("Response body is null")

            Log.d(TAG, "Тело responseText Categories: $jsonString.")

            // Десериализация JSON -> Kotlin Objects
            return json.decodeFromString<List<CategoryDto>>(jsonString)
        }
    }

    private fun showError(message: String) {
        runOnUiThread { Toast.makeText(this, message, Toast.LENGTH_LONG).show() }
    }

    override fun onDestroy() {
        super.onDestroy()
        threadPool.shutdown()
    }

    companion object {
        private val json = Json { ignoreUnknownKeys = true }
    }
}