package ru.iceberg.meal_planner

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.gson.Gson

data class Meal(
    val category: String = "",
    val calories: Int = 0,
    val carbohydrates: Int = 0,
    val fats: Int = 0,
    val ingredients: Map<String, String> = emptyMap(),
    val name: String = "",
    val preparing_time: String = "",
    val proteins: Int = 0,
    val recipe: List<String> = emptyList()
)

class show_recipe_details : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_recipe_details)

        val breakfastJson = intent.getStringExtra("breakfastJson")

        val dinnerJson = intent.getStringExtra("dinnerJson")

        val supperJson = intent.getStringExtra("supperJson")

        val snackJson = intent.getStringExtra("snackJson")

        val mealJson = breakfastJson ?: dinnerJson ?: supperJson ?: snackJson
        val meal = Gson().fromJson(mealJson, Meal::class.java)

        findViewById<TextView>(R.id.recipeNameTextView).text = meal.name
        findViewById<TextView>(R.id.caloriesTextView).text = "Калории: ${meal.calories}"
        findViewById<TextView>(R.id.carbohydratesTextView).text = "Углеводы: ${meal.carbohydrates}"
        findViewById<TextView>(R.id.fatsTextView).text = "Жиры: ${meal.fats}"
        findViewById<TextView>(R.id.proteinsTextView).text = "Белки: ${meal.proteins}"
        findViewById<TextView>(R.id.ingredientsTextView).text = "Ингредиенты:\n${meal.ingredients}"
        findViewById<TextView>(R.id.preparingTimeTextView).text = "Время приготовления: ${meal.preparing_time}"
        findViewById<TextView>(R.id.recipeTextView).text = "Рецепт:\n${meal.recipe}"
        }

}