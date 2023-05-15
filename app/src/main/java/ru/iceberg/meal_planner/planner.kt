package ru.iceberg.meal_planner

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener



data class Meal(
    val category: String = "",
    val calories: Int = 0,
    val carbohydrates: Int = 0,
    val fats: Int = 0,
    val ingredients: Map<String, String> = emptyMap(),
    val name: String = "",
    val preparingTime: String = "",
    val proteins: Int = 0,
    val recipe: List<String> = emptyList()
)

class planner : AppCompatActivity() {

    private lateinit var caloriesTextView: TextView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_planner)

        caloriesTextView = findViewById(R.id.calories_textview)
        val weight = intent.getDoubleExtra("weight", 0.0)
        val height = intent.getDoubleExtra("height", 0.0)
        val age = intent.getIntExtra("age", 0)
        val gender = intent.getStringExtra("gender")

        // Вычисляем количество калорий, необходимых пользователю
        val calories = calculateCalories(weight, height, gender, age)
        caloriesTextView.text = "Количество калорий: $calories"
        val breakfastsRef = FirebaseDatabase.getInstance().getReference("breakfasts")
        getRandomMeals { meals ->
            if (meals.isNotEmpty()) {
                // Выводим полученные случайные значения
                for (meal in meals) {
                    println("Name: ${meal.name}")
                    println("Category: ${meal.category}")
                    println("Calories: ${meal.calories}")
                    println("Ingredients: ${meal.ingredients}")
                    println("Preparing Time: ${meal.preparingTime}")
                    println("Recipe: ${meal.recipe}")
                    println()
                }
            } else {
                // Нет достаточного количества значений в базе данных
                println("Not enough meals available")
                println(meals)
            }
        }
    }

    private fun calculateCalories(weight: Double, height: Double, gender: String?, age: Int): Int {
        val calories = if (gender == "Мужской") {
            (10 * weight) + (6.25 * height) - (5 * age) + 5
        } else {
            (10 * weight) + (6.25 * height) - (5 * age) - 161
        }
        return calories.toInt()
    }

    fun getRandomMeals(callback: (List<Meal>) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val mealsRef = database.getReference("meals")

        mealsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val meals = mutableListOf<Meal>()

                val breakfastsSnapshot = snapshot.child("breakfasts")
                val dinnersSnapshot = snapshot.child("dinners")
                val suppersSnapshot = snapshot.child("suppers")
                val snacksSnapshot = snapshot.child("snacks")

                // Получаем случайное значение из каждой категории
                val breakfast = getRandomMealFromSnapshot(breakfastsSnapshot)
                val dinner = getRandomMealFromSnapshot(dinnersSnapshot)
                val supper = getRandomMealFromSnapshot(suppersSnapshot)
                val snack = getRandomMealFromSnapshot(snacksSnapshot)

                if (breakfast != null && dinner != null && supper != null && snack != null) {
                    meals.add(breakfast)
                    meals.add(dinner)
                    meals.add(supper)
                    meals.add(snack)
                }

                callback(meals)
            }

            override fun onCancelled(error: DatabaseError) {
                // Обработка ошибки
                callback(emptyList())
            }
        })
    }

    fun getRandomMealFromSnapshot(snapshot: DataSnapshot): Meal? {
        val meals = mutableListOf<Meal>()

        for (mealSnapshot in snapshot.children) {
            val meal = mealSnapshot.getValue(Meal::class.java)
            meal?.let {
                meals.add(it)
            }
        }

        if (meals.isNotEmpty()) {
            return meals.random()
        }

        return null
    }


}


