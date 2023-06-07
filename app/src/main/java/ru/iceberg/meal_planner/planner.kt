package ru.iceberg.meal_planner

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.*
import kotlinx.coroutines.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import com.google.gson.Gson


@Suppress("DEPRECATION")
class planner : AppCompatActivity() {

    private lateinit var caloriesTextView: TextView
    private var breakfastButtonText: String = ""
    private var dinnerButtonText: String = ""
    private var supperButtonText: String = ""
    private var snackButtonText: String = ""


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_planner)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.profile -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        caloriesTextView = findViewById(R.id.calories_textview)
        val weight = intent.getDoubleExtra("weight", 0.0)
        val height = intent.getDoubleExtra("height", 0.0)
        val age = intent.getIntExtra("age", 0)
        val gender = intent.getStringExtra("gender")
        val activityLevel = intent.getStringExtra("activityLevel")
        val breakfastButton = findViewById<Button>(R.id.breakfastButton)
        val dinnerButton = findViewById<Button>(R.id.dinnerButton)
        val supperButton = findViewById<Button>(R.id.supperButton)
        val snackButton = findViewById<Button>(R.id.snackButton)
        val refreshButton = findViewById<Button>(R.id.refreshButton)


        // Вычисляем количество калорий, необходимых пользователю
        val calories = calculateCalories(weight, height, gender, age, activityLevel)
        caloriesTextView.text = "Количество калорий: $calories"
        val databaseUrl = "https://meal-planner-8481d-default-rtdb.europe-west1.firebasedatabase.app"
        val databaseRef = FirebaseDatabase.getInstance(databaseUrl).reference
        val coroutineScope = CoroutineScope(Dispatchers.Default)
        refreshButton.setOnClickListener {
            coroutineScope.launch {
                val menu = generateMenu(databaseRef, calories)
                runOnUiThread {
                    breakfastButton.text = "${menu[0]["name"]} (${menu[0]["calories"]} ккал)"
                    dinnerButton.text = "${menu[1]["name"]} (${menu[1]["calories"]} ккал)"
                    supperButton.text = "${menu[2]["name"]} (${menu[2]["calories"]} ккал)"
                    snackButton.text = "${menu[3]["name"]} (${menu[3]["calories"]} ккал)"
                }
            }
        }
        coroutineScope.launch {
            val menu = generateMenu(databaseRef, calories)
            runOnUiThread {
                breakfastButton.text = "${menu[0]["name"]} (${menu[0]["calories"]} ккал)"
                dinnerButton.text = "${menu[1]["name"]} (${menu[1]["calories"]} ккал)"
                supperButton.text = "${menu[2]["name"]} (${menu[2]["calories"]} ккал)"
                snackButton.text = "${menu[3]["name"]} (${menu[3]["calories"]} ккал)"

                breakfastButtonText = breakfastButton.text.toString()
                dinnerButtonText = dinnerButton.text.toString()
                supperButtonText = supperButton.text.toString()
                snackButtonText = snackButton.text.toString()
            }
            breakfastButton.setOnClickListener {
                val breakfastRecipe = menu[0]
                val breakfastRecipeJson = Gson().toJson(breakfastRecipe)
                val intent = Intent(this@planner, show_recipe_details::class.java)
                intent.putExtra("breakfastJson", breakfastRecipeJson)
                startActivity(intent)
            }
            dinnerButton.setOnClickListener {
                val dinnerRecipe = menu[1]
                val dinnerRecipeJson = Gson().toJson(dinnerRecipe)
                val intent = Intent(this@planner, show_recipe_details::class.java)
                intent.putExtra("dinnerJson", dinnerRecipeJson)
                startActivity(intent)
            }
            supperButton.setOnClickListener {
                val supperRecipe = menu[2]
                val supperRecipeJson = Gson().toJson(supperRecipe)
                val intent = Intent(this@planner, show_recipe_details::class.java)
                intent.putExtra("supperJson", supperRecipeJson)
                startActivity(intent)
            }
            snackButton.setOnClickListener {
                val snackRecipe = menu[3]
                val snackRecipeJson = Gson().toJson(snackRecipe)
                val intent = Intent(this@planner, show_recipe_details::class.java)
                intent.putExtra("snackJson", snackRecipeJson)
                startActivity(intent)
            }

            refreshButton.setOnClickListener {
                coroutineScope.launch {
                    val menu = generateMenu(databaseRef, calories)
                    runOnUiThread {
                        breakfastButton.text = "${menu[0]["name"]} (${menu[0]["calories"]} ккал)"
                        dinnerButton.text = "${menu[1]["name"]} (${menu[1]["calories"]} ккал)"
                        supperButton.text = "${menu[2]["name"]} (${menu[2]["calories"]} ккал)"
                        snackButton.text = "${menu[3]["name"]} (${menu[3]["calories"]} ккал)"
                    }
                }
            }
        }

    }

    override fun onPause() {
        super.onPause()
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("calories", caloriesTextView.text.toString())
        editor.putString("breakfastButtonText", breakfastButtonText)
        editor.putString("dinnerButtonText", dinnerButtonText)
        editor.putString("supperButtonText", supperButtonText)
        editor.putString("snackButtonText", snackButtonText)
        editor.apply()
    }

    override fun onResume() {
        super.onResume()
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val calories = sharedPreferences.getString("calories", "")
        val breakfastButtonText = sharedPreferences.getString("breakfastButtonText", "")
        val dinnerButtonText = sharedPreferences.getString("dinnerButtonText", "")
        val supperButtonText = sharedPreferences.getString("supperButtonText", "")
        val snackButtonText = sharedPreferences.getString("snackButtonText", "")

        // Установка сохраненных значений на кнопки
        val breakfastButton = findViewById<Button>(R.id.breakfastButton)
        val dinnerButton = findViewById<Button>(R.id.dinnerButton)
        val supperButton = findViewById<Button>(R.id.supperButton)
        val snackButton = findViewById<Button>(R.id.snackButton)
        breakfastButton.text = breakfastButtonText
        dinnerButton.text = dinnerButtonText
        supperButton.text = supperButtonText
        snackButton.text = snackButtonText
        if (calories != null && calories.isNotEmpty()) {
            caloriesTextView.text = calories
        }

    }



    private fun calculateCalories(weight: Double, height: Double, gender: String?, age: Int, activityLevel: String?): Int {
        val coef: Double = if (activityLevel == "минимальный") 1.2
        else if (activityLevel == "низкий") 1.375
        else if (activityLevel == "умеренный") 1.55
        else if (activityLevel == "высокий") 1.7
        else 1.9
        val calories = if (gender == "Мужской") {
            66.5 + (13.75 * weight)+ (5.003 * height)- (6.775 * age) * coef
        } else {
            655.1 + (9.563 * weight ) + (1.85 * height )-  (4.676 * age ) * coef
        }
        return calories.toInt()
    }

    // Получение случайных значений для разных категорий
    suspend fun getRandomBreakfasts(databaseRef: DatabaseReference): Map<String, Any?>? {
        return suspendCoroutine { continuation ->
            val snacksRef = databaseRef.child("breakfasts")
            snacksRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val mealsCount = snapshot.childrenCount
                    if (mealsCount > 0) {
                        val randomIndex = (0 until mealsCount).random()
                        val randomSnack = snapshot.child(randomIndex.toString())

                        // Получение значений для случайной закуски
                        val name = randomSnack.child("name").value
                        val calories = randomSnack.child("calories").value
                        val carbohydrates = randomSnack.child("carbohydates").value
                        val fats = randomSnack.child("fats").value
                        val proteins = randomSnack.child("proteins").value
                        val ingredients = randomSnack.child("ingredients").value
                        val preparing_time = randomSnack.child("preparing_time").value
                        val recipe = randomSnack.child("recipe").value

                        val snackData = mapOf(
                            "name" to name,
                            "calories" to calories,
                            "carbohydrates" to carbohydrates,
                            "fats" to fats,
                            "proteins" to proteins,
                            "ingredients" to ingredients,
                            "preparing_time" to preparing_time,
                            "recipe" to recipe
                        )
                        continuation.resume(snackData)
                    } else {
                        continuation.resume(null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Обработка ошибки чтения из базы данных
                    println("Error reading snacks: ${error.message}")
                    continuation.resume(null)
                }
            })
        }
    }


    suspend fun getRandomDinner(databaseRef: DatabaseReference): Map<String, Any?>? {
        return suspendCoroutine { continuation ->
            val snacksRef = databaseRef.child("dinners")
            snacksRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val mealsCount = snapshot.childrenCount
                    if (mealsCount > 0) {
                        val randomIndex = (0 until mealsCount).random()
                        val randomSnack = snapshot.child(randomIndex.toString())

                        // Получение значений для случайной закуски
                        val name = randomSnack.child("name").value
                        val calories = randomSnack.child("calories").value
                        val carbohydrates = randomSnack.child("carbohydates").value
                        val fats = randomSnack.child("fats").value
                        val proteins = randomSnack.child("proteins").value
                        val ingredients = randomSnack.child("ingredients").value
                        val preparing_time = randomSnack.child("preparing_time").value
                        val recipe = randomSnack.child("recipe").value

                        val snackData = mapOf(
                            "name" to name,
                            "calories" to calories,
                            "carbohydrates" to carbohydrates,
                            "fats" to fats,
                            "proteins" to proteins,
                            "ingredients" to ingredients,
                            "preparing_time" to preparing_time,
                            "recipe" to recipe
                        )
                        continuation.resume(snackData)
                    } else {
                        continuation.resume(null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Обработка ошибки чтения из базы данных
                    println("Error reading snacks: ${error.message}")
                    continuation.resume(null)
                }
            })
        }
    }


    suspend fun getRandomSupper(databaseRef: DatabaseReference): Map<String, Any?>? {
        return suspendCoroutine { continuation ->
            val snacksRef = databaseRef.child("suppers")
            snacksRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val mealsCount = snapshot.childrenCount
                    if (mealsCount > 0) {
                        val randomIndex = (0 until mealsCount).random()
                        val randomSnack = snapshot.child(randomIndex.toString())

                        val name = randomSnack.child("name").value
                        val calories = randomSnack.child("calories").value
                        val carbohydrates = randomSnack.child("carbohydates").value
                        val fats = randomSnack.child("fats").value
                        val proteins = randomSnack.child("proteins").value
                        val ingredients = randomSnack.child("ingredients").value
                        val preparing_time = randomSnack.child("preparing_time").value
                        val recipe = randomSnack.child("recipe").value

                        val Data = mapOf(
                            "name" to name,
                            "calories" to calories,
                            "carbohydrates" to carbohydrates,
                            "fats" to fats,
                            "proteins" to proteins,
                            "ingredients" to ingredients,
                            "preparing_time" to preparing_time,
                            "recipe" to recipe
                        )
                        continuation.resume(Data)
                    } else {
                        continuation.resume(null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Обработка ошибки чтения из базы данных
                    println("Error reading snacks: ${error.message}")
                    continuation.resume(null)
                }
            })
        }
    }


    suspend fun getRandomSnacks(databaseRef: DatabaseReference): Map<String, Any?>? {
        return suspendCoroutine { continuation ->
            val snacksRef = databaseRef.child("snacks")
            snacksRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val mealsCount = snapshot.childrenCount
                    if (mealsCount > 0) {
                        val randomIndex = (0 until mealsCount).random()
                        val randomSnack = snapshot.child(randomIndex.toString())

                        // Получение значений для случайной закуски
                        val name = randomSnack.child("name").value
                        val calories = randomSnack.child("calories").value
                        val carbohydrates = randomSnack.child("carbohydates").value
                        val fats = randomSnack.child("fats").value
                        val proteins = randomSnack.child("proteins").value
                        val ingredients = randomSnack.child("ingredients").value
                        val preparing_time = randomSnack.child("preparing_time").value
                        val recipe = randomSnack.child("recipe").value

                        val Data = mapOf(
                            "name" to name,
                            "calories" to calories,
                            "carbohydrates" to carbohydrates,
                            "fats" to fats,
                            "proteins" to proteins,
                            "ingredients" to ingredients,
                            "preparing_time" to preparing_time,
                            "recipe" to recipe
                        )
                        continuation.resume(Data)
                    } else {
                        continuation.resume(null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Обработка ошибки чтения из базы данных
                    println("Error reading snacks: ${error.message}")
                    continuation.resume(null)
                }
            })
        }
    }


    suspend fun generateMenu(databaseRef: DatabaseReference, calories: Int): List<Map<String, Any>> {
        val menu = mutableListOf<Map<String, Any>>()

        // Генерация завтрака
        val breakfastData = getRandomBreakfasts(databaseRef)
        menu.add(breakfastData as Map<String, Any>)

        // Генерация обеда
        val dinnerData = getRandomDinner(databaseRef)
        menu.add(dinnerData as Map<String, Any>)

        // Генерация ужина
        val supperData = getRandomSupper(databaseRef)
        menu.add(supperData as Map<String, Any>)

        // Генерация закуски
        val snacksData = getRandomSnacks(databaseRef)
        menu.add(snacksData as Map<String, Any>)

        // Проверка общего количества калорий и коррекция, если необходимо
        var totalMenuCalories = menu.sumOf { (it["calories"] as Long).toInt()}
        println(totalMenuCalories)
        println(calories)
        var iterations = 0

        while (totalMenuCalories <= (calories - 100) || totalMenuCalories >= (calories + 100)) {
            // Генерация нового меню
            menu.clear()
            menu.add(getRandomBreakfasts(databaseRef) as Map<String, Any>)
            menu.add(getRandomDinner(databaseRef) as Map<String, Any>)
            menu.add(getRandomSupper(databaseRef) as Map<String, Any>)
            menu.add(getRandomSnacks(databaseRef) as Map<String, Any>)

            totalMenuCalories = menu.sumOf { (it["calories"] as Long).toInt()}
            iterations++
        }

        return menu
    }

}
