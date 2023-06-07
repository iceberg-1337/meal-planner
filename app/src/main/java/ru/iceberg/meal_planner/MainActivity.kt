package ru.iceberg.meal_planner

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var editTextHeight: EditText
    private lateinit var editTextWeight: EditText
    private lateinit var editTextAge: EditText
    private lateinit var radioGroupGender: RadioGroup
    private lateinit var radioButtonMale: RadioButton
    private lateinit var radioButtonFemale: RadioButton
    private lateinit var spinnerActivityLevel: Spinner
    private lateinit var buttonSave: Button

    private val plannerResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val calories = data?.getIntExtra("calories", 0)
            Toast.makeText(this, "Количество калорий: $calories", Toast.LENGTH_LONG).show()
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.generator -> {
                    val intent = Intent(this, planner::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        // Находим все элементы пользовательского интерфейса по их ID
        editTextHeight = findViewById(R.id.editTextHeight)
        editTextWeight = findViewById(R.id.editTextWeight)
        editTextAge = findViewById(R.id.editTextAge)
        radioGroupGender = findViewById(R.id.radioGroupGender)
        radioButtonMale = findViewById(R.id.radioButtonMale)
        radioButtonFemale = findViewById(R.id.radioButtonFemale)
        spinnerActivityLevel = findViewById(R.id.spinnerActivityLevel)
        buttonSave = findViewById(R.id.buttonSave)

        // Устанавливаем слушатель нажатия на кнопку "Сохранить"
        buttonSave.setOnClickListener {
            // Получаем значения из всех элементов пользовательского интерфейса
            val height = editTextHeight.text.toString().toDoubleOrNull()
            val weight = editTextWeight.text.toString().toDoubleOrNull()
            val age = editTextAge.text.toString().toIntOrNull()
            val gender = if (radioButtonMale.isChecked) "Мужской" else "Женский"
            val activityLevel = spinnerActivityLevel.selectedItem.toString()

            // Проверяем, что все значения были введены корректно
            if (height == null || weight == null || age == null) {
                Toast.makeText(this, "Пожалуйста, введите корректные значения", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val calories = calculateCalories(weight, height, gender, age, activityLevel)
            val calories_txt = "Количество калорий: $calories"
            val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("calories", calories_txt.toString())
            editor.apply()

            // Создаем Intent для перехода на новую страницу
            val intent = Intent(this, planner::class.java)
            intent.putExtra("weight", weight)
            intent.putExtra("height", height)
            intent.putExtra("age", age)
            intent.putExtra("gender", gender)
            intent.putExtra("activityLevel", activityLevel)

            // Запускаем экран Planner с помощью resultLauncher
            startActivity(intent)
        }
    }

    override fun onPause() {
        super.onPause()

        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("height", editTextHeight.text.toString())
        editor.putString("weight", editTextWeight.text.toString())
        editor.putString("age", editTextAge.text.toString())
        editor.putInt("gender", radioGroupGender.checkedRadioButtonId)
        editor.putInt("activityLevel", spinnerActivityLevel.selectedItemPosition)
        editor.apply()
    }


    override fun onResume() {
        super.onResume()

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        editTextHeight.setText(sharedPreferences.getString("height", ""))
        editTextWeight.setText(sharedPreferences.getString("weight", ""))
        editTextAge.setText(sharedPreferences.getString("age", ""))
        radioGroupGender.check(sharedPreferences.getInt("gender", R.id.radioButtonMale))
        spinnerActivityLevel.setSelection(sharedPreferences.getInt("activityLevel", 0))
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

}
