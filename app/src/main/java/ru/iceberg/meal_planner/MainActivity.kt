package ru.iceberg.meal_planner

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private lateinit var editTextHeight: EditText
    private lateinit var editTextWeight: EditText
    private lateinit var editTextAge: EditText
    private lateinit var radioGroupGender: RadioGroup
    private lateinit var radioButtonMale: RadioButton
    private lateinit var radioButtonFemale: RadioButton
    private lateinit var radioGroupFatPercentage: RadioGroup
    private lateinit var radioButtonLowFat: RadioButton
    private lateinit var radioButtonNormalFat: RadioButton
    private lateinit var radioButtonHighFat: RadioButton
    private lateinit var spinnerActivityLevel: Spinner
    private lateinit var spinnerTarget: Spinner
    private lateinit var buttonSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Находим все элементы пользовательского интерфейса по их ID
        editTextHeight = findViewById(R.id.editTextHeight)
        editTextWeight = findViewById(R.id.editTextWeight)
        editTextAge = findViewById(R.id.editTextAge)
        radioGroupGender = findViewById(R.id.radioGroupGender)
        radioButtonMale = findViewById(R.id.radioButtonMale)
        radioButtonFemale = findViewById(R.id.radioButtonFemale)
        radioGroupFatPercentage = findViewById(R.id.radioGroupFatPercentage)
        radioButtonLowFat = findViewById(R.id.radioButtonLowFat)
        radioButtonNormalFat = findViewById(R.id.radioButtonNormalFat)
        radioButtonHighFat = findViewById(R.id.radioButtonHighFat)
        spinnerActivityLevel = findViewById(R.id.spinnerActivityLevel)
        spinnerTarget = findViewById(R.id.spinnerTarget)
        buttonSave = findViewById(R.id.buttonSave)

        // Устанавливаем слушатель нажатия на кнопку "Сохранить"
        buttonSave.setOnClickListener {
            // Получаем значения из всех элементов пользовательского интерфейса
            val height = editTextHeight.text.toString().toDoubleOrNull()
            val weight = editTextWeight.text.toString().toDoubleOrNull()
            val age = editTextAge.text.toString().toIntOrNull()
            val gender = if (radioButtonMale.isChecked) "Мужской" else "Женский"
            val fatPercentage = when {
                radioButtonLowFat.isChecked -> "Низкий"
                radioButtonNormalFat.isChecked -> "Нормальный"
                else -> "Высокий"
            }
            val activityLevel = spinnerActivityLevel.selectedItem.toString()
            val target = spinnerTarget.selectedItem.toString()


            // Проверяем, что все значения были введены корректно
            if (height == null || weight == null || age == null) {
                Toast.makeText(this, "Пожалуйста, введите корректные значения", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            buttonSave.setOnClickListener {
                // Создаем Intent для перехода на новую страницу
                val intent = Intent(this, planner::class.java)
                intent.putExtra("weight", weight)
                intent.putExtra("height", height)
                intent.putExtra("age", age)
                intent.putExtra("gender", gender)
                startActivity(intent)
            }
        }
    }


}

