package com.example.dmfinal

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.dmfinal.util.DataPreprocessor
import com.google.android.material.button.MaterialButton
import com.google.android.material.appbar.MaterialToolbar

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        
        // Setup toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        
        val category = intent.getStringExtra("PREDICTION_CATEGORY") ?: "Unknown"
        
        val categoryTextView: TextView = findViewById(R.id.categoryTextView)
        val descriptionTextView: TextView = findViewById(R.id.descriptionTextView)
        val backButton: MaterialButton = findViewById(R.id.backButton)
        
        // Установка текста категории с форматированием
        val formattedCategory = category.replace("_", " ")
            .split(" ")
            .joinToString(" ") { word ->
                word.lowercase().replaceFirstChar { it.uppercase() }
            }
        categoryTextView.text = formattedCategory
        
        // Установка цвета в зависимости от категории
        val color = DataPreprocessor.getColorForCategory(category)
        categoryTextView.setTextColor(color)
        
        // Установка описания
        descriptionTextView.text = DataPreprocessor.getDescriptionForCategory(category)
        
        // Кнопка возврата
        backButton.setOnClickListener {
            finish()
        }
    }
}


