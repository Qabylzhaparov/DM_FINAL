package com.example.dmfinal

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.dmfinal.model.UserData
import com.example.dmfinal.service.PredictionResult
import com.example.dmfinal.service.PredictionService
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import android.widget.AutoCompleteTextView

class MainActivity : AppCompatActivity() {
    
    private lateinit var predictionService: PredictionService
    
    // UI Elements - Quick Section
    private lateinit var heightInput: TextInputEditText
    private lateinit var weightInput: TextInputEditText
    private lateinit var quickPredictButton: MaterialButton
    private lateinit var quickProgressBar: View
    private lateinit var quickResultCard: View
    private lateinit var quickResultText: android.widget.TextView
    
    // UI Elements - Detailed Section
    private lateinit var detailedSection: View
    private lateinit var detailedSectionToggle: View
    private lateinit var expandIcon: android.widget.TextView
    private lateinit var genderSpinner: AutoCompleteTextView
    private lateinit var ageInput: TextInputEditText
    private lateinit var familyHistorySwitch: SwitchMaterial
    private lateinit var favcSwitch: SwitchMaterial
    private lateinit var fcvcInput: TextInputEditText
    private lateinit var ncpInput: TextInputEditText
    private lateinit var caecSpinner: AutoCompleteTextView
    private lateinit var smokeSwitch: SwitchMaterial
    private lateinit var ch2oInput: TextInputEditText
    private lateinit var sccSwitch: SwitchMaterial
    private lateinit var fafInput: TextInputEditText
    private lateinit var tueInput: TextInputEditText
    private lateinit var calcSpinner: AutoCompleteTextView
    private lateinit var mtransSpinner: AutoCompleteTextView
    private lateinit var detailedPredictButton: MaterialButton
    private lateinit var detailedProgressBar: View
    private lateinit var detailedResultCard: View
    private lateinit var detailedResultText: android.widget.TextView
    
    private var isDetailedSectionExpanded = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Setup toolbar
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        
        predictionService = PredictionService(this)
        initializeViews()
        setupSpinners()
        setupToggleSection()
        setupQuickPredictButton()
        setupDetailedPredictButton()
    }
    
    private fun initializeViews() {
        // Quick section
        heightInput = findViewById(R.id.heightInput)
        weightInput = findViewById(R.id.weightInput)
        quickPredictButton = findViewById(R.id.quickPredictButton)
        quickProgressBar = findViewById(R.id.quickProgressBar)
        quickResultCard = findViewById(R.id.quickResultCard)
        quickResultText = findViewById(R.id.quickResultText)
        
        // Detailed section toggle
        detailedSection = findViewById(R.id.detailedSection)
        detailedSectionToggle = findViewById(R.id.detailedSectionToggle)
        expandIcon = findViewById(R.id.expandIcon)
        
        // Detailed section fields
        genderSpinner = findViewById(R.id.genderSpinner)
        ageInput = findViewById(R.id.ageInput)
        familyHistorySwitch = findViewById(R.id.familyHistorySwitch)
        favcSwitch = findViewById(R.id.favcSwitch)
        fcvcInput = findViewById(R.id.fcvcInput)
        ncpInput = findViewById(R.id.ncpInput)
        caecSpinner = findViewById(R.id.caecSpinner)
        smokeSwitch = findViewById(R.id.smokeSwitch)
        ch2oInput = findViewById(R.id.ch2oInput)
        sccSwitch = findViewById(R.id.sccSwitch)
        fafInput = findViewById(R.id.fafInput)
        tueInput = findViewById(R.id.tueInput)
        calcSpinner = findViewById(R.id.calcSpinner)
        mtransSpinner = findViewById(R.id.mtransSpinner)
        detailedPredictButton = findViewById(R.id.detailedPredictButton)
        detailedProgressBar = findViewById(R.id.detailedProgressBar)
        detailedResultCard = findViewById(R.id.detailedResultCard)
        detailedResultText = findViewById(R.id.detailedResultText)
    }
    
    private fun setupSpinners() {
        // Gender
        val genderAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, listOf("Male", "Female"))
        genderSpinner.setAdapter(genderAdapter)
        genderSpinner.setOnItemClickListener { _, _, position, _ ->
            genderSpinner.setText(genderAdapter.getItem(position), false)
        }
        
        // CAEC
        val caecAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, 
            listOf("no", "Sometimes", "Frequently", "Always"))
        caecSpinner.setAdapter(caecAdapter)
        caecSpinner.setOnItemClickListener { _, _, position, _ ->
            caecSpinner.setText(caecAdapter.getItem(position), false)
        }
        
        // CALC
        val calcAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line,
            listOf("no", "Sometimes", "Frequently", "Always"))
        calcSpinner.setAdapter(calcAdapter)
        calcSpinner.setOnItemClickListener { _, _, position, _ ->
            calcSpinner.setText(calcAdapter.getItem(position), false)
        }
        
        // MTRANS
        val mtransAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line,
            listOf("Public_Transportation", "Automobile", "Walking", "Motorbike", "Bike"))
        mtransSpinner.setAdapter(mtransAdapter)
        mtransSpinner.setOnItemClickListener { _, _, position, _ ->
            mtransSpinner.setText(mtransAdapter.getItem(position), false)
        }
    }
    
    private fun setupToggleSection() {
        detailedSectionToggle.setOnClickListener {
            isDetailedSectionExpanded = !isDetailedSectionExpanded
            detailedSection.visibility = if (isDetailedSectionExpanded) View.VISIBLE else View.GONE
            expandIcon.text = if (isDetailedSectionExpanded) "▲" else "▼"
        }
    }
    
    private fun setupQuickPredictButton() {
        quickPredictButton.setOnClickListener {
            if (validateQuickInput()) {
                makeQuickPrediction()
            }
        }
    }
    
    private fun setupDetailedPredictButton() {
        detailedPredictButton.setOnClickListener {
            if (validateQuickInput() && validateDetailedInput()) {
                val userData = collectUserData()
                makeDetailedPrediction(userData)
            }
        }
    }
    
    private fun validateQuickInput(): Boolean {
        // Проверка Height
        val heightText = heightInput.text.toString()
        if (heightText.isEmpty()) {
            showError("Please enter your height")
            return false
        }
        val height = heightText.toDoubleOrNull()
        if (height == null || height <= 0.5 || height > 3.0) {
            showError("Please enter a valid height (0.5-3.0 meters)")
            return false
        }
        
        // Проверка Weight
        val weightText = weightInput.text.toString()
        if (weightText.isEmpty()) {
            showError("Please enter your weight")
            return false
        }
        val weight = weightText.toDoubleOrNull()
        if (weight == null || weight <= 20 || weight > 300) {
            showError("Please enter a valid weight (20-300 kg)")
            return false
        }
        
        return true
    }
    
    private fun validateDetailedInput(): Boolean {
        // Проверка Gender
        if (genderSpinner.text.toString().isEmpty()) {
            showError("Please select gender")
            return false
        }
        
        // Проверка Age
        val ageText = ageInput.text.toString()
        if (ageText.isEmpty()) {
            showError("Please enter age")
            return false
        }
        val age = ageText.toIntOrNull()
        if (age == null || age < 0 || age > 150) {
            showError("Please enter a valid age (0-150)")
            return false
        }
        
        // Проверка FCVC
        val fcvcText = fcvcInput.text.toString()
        if (fcvcText.isEmpty()) {
            showError("Please enter vegetable consumption frequency")
            return false
        }
        val fcvc = fcvcText.toDoubleOrNull()
        if (fcvc == null || fcvc < 0) {
            showError("Please enter a valid vegetable consumption value")
            return false
        }
        
        // Проверка NCP
        val ncpText = ncpInput.text.toString()
        if (ncpText.isEmpty()) {
            showError("Please enter number of main meals")
            return false
        }
        val ncp = ncpText.toDoubleOrNull()
        if (ncp == null || ncp < 0) {
            showError("Please enter a valid number of meals")
            return false
        }
        
        // Проверка CAEC
        if (caecSpinner.text.toString().isEmpty()) {
            showError("Please select consumption of food between meals")
            return false
        }
        
        // Проверка CH2O
        val ch2oText = ch2oInput.text.toString()
        if (ch2oText.isEmpty()) {
            showError("Please enter water consumption")
            return false
        }
        val ch2o = ch2oText.toDoubleOrNull()
        if (ch2o == null || ch2o < 0) {
            showError("Please enter a valid water consumption value")
            return false
        }
        
        // Проверка FAF
        val fafText = fafInput.text.toString()
        if (fafText.isEmpty()) {
            showError("Please enter physical activity minutes")
            return false
        }
        val faf = fafText.toDoubleOrNull()
        if (faf == null || faf < 0) {
            showError("Please enter a valid physical activity value")
            return false
        }
        
        // Проверка TUE
        val tueText = tueInput.text.toString()
        if (tueText.isEmpty()) {
            showError("Please enter screen time")
            return false
        }
        val tue = tueText.toDoubleOrNull()
        if (tue == null || tue < 0) {
            showError("Please enter a valid screen time value")
            return false
        }
        
        // Проверка CALC
        if (calcSpinner.text.toString().isEmpty()) {
            showError("Please select alcohol consumption")
            return false
        }
        
        // Проверка MTRANS
        if (mtransSpinner.text.toString().isEmpty()) {
            showError("Please select transportation method")
            return false
        }
        
        return true
    }
    
    private fun collectUserData(): UserData {
        return UserData(
            gender = genderSpinner.text.toString(),
            age = ageInput.text.toString().toInt(),
            height = heightInput.text.toString().toDouble(),
            weight = weightInput.text.toString().toDouble(),
            familyHistoryWithOverweight = familyHistorySwitch.isChecked,
            favc = favcSwitch.isChecked,
            fcvc = fcvcInput.text.toString().toDouble(),
            ncp = ncpInput.text.toString().toDouble(),
            caec = caecSpinner.text.toString(),
            smoke = smokeSwitch.isChecked,
            ch2o = ch2oInput.text.toString().toDouble(),
            scc = sccSwitch.isChecked,
            faf = fafInput.text.toString().toDouble(),
            tue = tueInput.text.toString().toDouble(),
            calc = calcSpinner.text.toString(),
            mtrans = mtransSpinner.text.toString()
        )
    }
    
    private fun makeQuickPrediction() {
        quickProgressBar.visibility = View.VISIBLE
        quickPredictButton.isEnabled = false
        quickPredictButton.text = "Calculating..."
        
        // Локальный расчёт BMI (без API)
        val height = heightInput.text.toString().toDouble()
        val weight = weightInput.text.toString().toDouble()
        val bmi = weight / (height * height)
        
        // Классификация по стандартным диапазонам BMI
        val prediction = when {
            bmi < 18.5 -> "Insufficient_Weight"
            bmi < 25.0 -> "Normal_Weight"
            bmi < 27.0 -> "Overweight_Level_I"
            bmi < 30.0 -> "Overweight_Level_II"
            bmi < 35.0 -> "Obesity_Type_I"
            bmi < 40.0 -> "Obesity_Type_II"
            else -> "Obesity_Type_III"
        }
        
        // Показываем результат с небольшой задержкой для UX
        lifecycleScope.launch {
            kotlinx.coroutines.delay(500) // Имитация обработки
            quickProgressBar.visibility = View.GONE
            quickPredictButton.isEnabled = true
            quickPredictButton.text = "⚡ Quick Check"
            showQuickResult(prediction, bmi)
        }
    }
    
    private fun makeDetailedPrediction(userData: UserData) {
        detailedProgressBar.visibility = View.VISIBLE
        detailedPredictButton.isEnabled = false
        detailedPredictButton.text = "Analyzing..."
        
        lifecycleScope.launch {
            try {
                val result = predictionService.predict(userData)
                
                result.onSuccess { predictionResult ->
                    detailedProgressBar.visibility = View.GONE
                    detailedPredictButton.isEnabled = true
                    detailedPredictButton.text = "Get Detailed Prediction"
                    showDetailedResult(predictionResult)
                }.onFailure { exception ->
                    detailedProgressBar.visibility = View.GONE
                    detailedPredictButton.isEnabled = true
                    detailedPredictButton.text = "Get Detailed Prediction"
                    
                    // Используем мок для демонстрации
                    val mockPredictionResult = predictionService.predictMock(userData)
                    Toast.makeText(
                        this@MainActivity,
                        "API unavailable. Using mock prediction.",
                        Toast.LENGTH_SHORT
                    ).show()
                    showDetailedResult(mockPredictionResult)
                }
            } catch (e: Exception) {
                detailedProgressBar.visibility = View.GONE
                detailedPredictButton.isEnabled = true
                detailedPredictButton.text = "Get Detailed Prediction"
                showError("Error: ${e.message}")
            }
        }
    }
    
    private fun showQuickResult(prediction: String, bmi: Double? = null) {
        quickResultCard.visibility = View.VISIBLE
        if (bmi != null) {
            quickResultText.text = "$prediction\nBMI: ${String.format("%.1f", bmi)}"
        } else {
            quickResultText.text = prediction
        }
    }
    
    private fun showDetailedResult(predictionResult: PredictionResult) {
        detailedResultCard.visibility = View.VISIBLE
        val confidencePercent = String.format("%.1f%%", predictionResult.confidence * 100)
        detailedResultText.text = "${predictionResult.prediction}\nConfidence: $confidencePercent"
    }
    
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

