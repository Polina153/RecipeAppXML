package com.example.recipeappxml.data

import com.example.recipeappxml.model.Category
import com.example.recipeappxml.model.Ingredient
import com.example.recipeappxml.model.Recipe

class RecipesRepositoryStub {
    companion object {
        fun getCategories(): List<Category> {
            return categories
        }

        fun getRecipesByCategoryId(categoryId: Int): List<Recipe> {
            return when (categoryId) {
                0 -> burgerRecipes  // Имитация GET /category/0/recipes
                else -> emptyList() // Остальные категории пока пустые
            }
        }

        fun getRecipeById(recipeId: Int): Recipe {
            return when (recipeId) {
                0 -> burgerRecipes[0]
                1 -> burgerRecipes[1]
                else -> burgerRecipes[0]
            }
        }

        fun getRecipesByIds(ids: Set<Int>): List<Recipe> {
            return burgerRecipes.filter { ids.contains(it.id) }
        }

        private val categories = listOf(
            Category(
                id = 0,
                title = "Бургеры",
                description = "Рецепты всех популярных видов бургеров",
                imageUrl = "burger.png"
            ),
            Category(
                id = 1,
                title = "Десерты",
                description = "Самые вкусные рецепты десертов специально для вас",
                imageUrl = "dessert.png"
            ),
            Category(
                id = 2,
                title = "Пицца",
                description = "Пицца на любой вкус и цвет. Лучшая подборка для тебя",
                imageUrl = "pizza.png"
            ),
            Category(
                id = 3,
                title = "Рыба",
                description = "Печеная, жареная, сушеная, любая рыба на твой вкус",
                imageUrl = "fish.png"
            ),
            Category(
                id = 4,
                title = "Супы",
                description = "От классики до экзотики: мир в одной тарелке",
                imageUrl = "soup.png"
            ),
            Category(
                id = 5,
                title = "Салаты",
                description = "Хрустящий калейдоскоп под соусом вдохновения",
                imageUrl = "salad.png"
            )
        )

        private val burgerRecipes = listOf(
            Recipe(
                id = 0,
                title = "Классический бургер с говядиной",
                ingredients = listOf(
                    Ingredient(
                        quantity = "0.5",
                        unitOfMeasure = "кг",
                        description = "говяжий фарш"
                    ),
                    Ingredient(
                        quantity = "1.0",
                        unitOfMeasure = "шт",
                        description = "луковица, мелко нарезанная"
                    ),
                    Ingredient(
                        quantity = "2.0",
                        unitOfMeasure = "зубч",
                        description = "чеснок, измельченный"
                    ),
                    Ingredient(
                        quantity = "4.0",
                        unitOfMeasure = "шт",
                        description = "булочки для бургера"
                    ),
                    Ingredient(
                        quantity = "4.0",
                        unitOfMeasure = "шт",
                        description = "листа салата"
                    ),
                    Ingredient(
                        quantity = "1.0",
                        unitOfMeasure = "шт",
                        description = "помидор, нарезанный кольцами"
                    ),
                    Ingredient(
                        quantity = "2.0",
                        unitOfMeasure = "ст. л.",
                        description = "горчица"
                    ),
                    Ingredient(
                        quantity = "2.0",
                        unitOfMeasure = "ст. л.",
                        description = "кетчуп"
                    ),
                    Ingredient(
                        quantity = "по вкусу",
                        unitOfMeasure = "",
                        description = "соль и черный перец"
                    )
                ),
                method = listOf(
                    "1. В глубокой миске смешайте говяжий фарш, лук, чеснок, соль и перец. Разделите фарш на 4 равные части и сформируйте котлеты.",
                    "2. Разогрейте сковороду на среднем огне. Обжаривайте котлеты с каждой стороны в течение 4-5 минут или до желаемой степени прожарки.",
                    "3. В то время как котлеты готовятся, подготовьте булочки. Разрежьте их пополам и обжарьте на сковороде до золотистой корочки.",
                    "4. Смазать нижние половинки булочек горчицей и кетчупом, затем положите лист салата, котлету, кольца помидора и закройте верхней половинкой булочки.",
                    "5. Подавайте бургеры горячими с картофельными чипсами или картофельным пюре."
                ),
                imageUrl = "burger-hamburger.jpg"
            ),
            Recipe(
                id = 1,
                title = "Чизбургер с беконом",
                ingredients = listOf(
                    Ingredient(
                        quantity = "0.4",
                        unitOfMeasure = "кг",
                        description = "говяжий фарш"
                    ),
                    Ingredient(
                        quantity = "4.0",
                        unitOfMeasure = "шт",
                        description = "ломтика бекона"
                    ),
                    Ingredient(
                        quantity = "4.0",
                        unitOfMeasure = "шт",
                        description = "ломтика сыра чеддер"
                    ),
                    Ingredient(
                        quantity = "4.0",
                        unitOfMeasure = "шт",
                        description = "булочки для бургера"
                    ),
                    Ingredient(
                        quantity = "1.0",
                        unitOfMeasure = "шт",
                        description = "помидор, нарезанный"
                    ),
                    Ingredient(
                        quantity = "по вкусу",
                        unitOfMeasure = "",
                        description = "майонез и кетчуп"
                    )
                ),
                method = listOf(
                    "1. Обжарьте бекон на сковороде до хрустящей корочки, отложите на бумажное полотенце.",
                    "2. Сформируйте из фарша 4 котлеты, обжарьте с каждой стороны по 4 минуты.",
                    "3. За минуту до готовности положите на каждую котлету по ломтику сыра, чтобы он расплавился.",
                    "4. Соберите бургер: булочка, майонез, котлета с сыром, бекон, помидор, кетчуп.",
                    "5. Подавайте горячими."
                ),
                imageUrl = "burger-cheeseburger.jpg"
            )
        )
    }
}