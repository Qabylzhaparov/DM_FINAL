package com.example.dmfinal.util

import com.example.dmfinal.model.UserData

/**
 * Класс для предобработки данных перед подачей в модель
 */
object DataPreprocessor {
    
    /**
     * Преобразует UserData в массив признаков для модели
     * Порядок признаков должен соответствовать порядку признаков при обучении модели
     */
    fun preprocessData(userData: UserData): List<Double> {
        val features = mutableListOf<Double>()
        
        // 1. Gender: Male = 1, Female = 0
        features.add(if (userData.gender == "Male") 1.0 else 0.0)
        
        // 2. Age (числовой, будет стандартизирован)
        features.add(userData.age.toDouble())
        
        // 3. family_history_with_overweight: yes = 1, no = 0
        features.add(if (userData.familyHistoryWithOverweight) 1.0 else 0.0)
        
        // 4. FAVC: yes = 1, no = 0
        features.add(if (userData.favc) 1.0 else 0.0)
        
        // 5. FCVC (числовой)
        features.add(userData.fcvc)
        
        // 6. NCP (числовой)
        features.add(userData.ncp)
        
        // 7. CAEC: no = 0.0, Sometimes = 0.33, Frequently = 0.67, Always = 1.0
        features.add(when (userData.caec) {
            "no" -> 0.0
            "Sometimes" -> 0.33
            "Frequently" -> 0.67
            "Always" -> 1.0
            else -> 0.0
        })
        
        // 8. SMOKE: yes = 1, no = 0
        features.add(if (userData.smoke) 1.0 else 0.0)
        
        // 9. CH2O (числовой)
        features.add(userData.ch2o)
        
        // 10. SCC: yes = 1, no = 0
        features.add(if (userData.scc) 1.0 else 0.0)
        
        // 11. FAF (числовой)
        features.add(userData.faf)
        
        // 12. TUE (числовой)
        features.add(userData.tue)
        
        // 13. CALC: no = 0.0, Sometimes = 0.33, Frequently = 0.67, Always = 1.0
        features.add(when (userData.calc) {
            "no" -> 0.0
            "Sometimes" -> 0.33
            "Frequently" -> 0.67
            "Always" -> 1.0
            else -> 0.0
        })
        
        // 14-17. MTRANS: One-Hot Encoding
        // Порядок: Bike, Motorbike, Public_Transportation, Walking
        features.add(if (userData.mtrans == "Bike") 1.0 else 0.0)
        features.add(if (userData.mtrans == "Motorbike") 1.0 else 0.0)
        features.add(if (userData.mtrans == "Public_Transportation") 1.0 else 0.0)
        features.add(if (userData.mtrans == "Walking") 1.0 else 0.0)
        
        return features
    }
    
    /**
     * Получает цвет для категории ожирения
     */
    fun getColorForCategory(category: String): Int {
        return when {
            category.contains("Insufficient", ignoreCase = true) -> android.graphics.Color.parseColor("#FFA500") // Orange
            category.contains("Normal", ignoreCase = true) -> android.graphics.Color.parseColor("#4CAF50") // Green
            category.contains("Overweight_Level_I", ignoreCase = true) -> android.graphics.Color.parseColor("#FFC107") // Amber
            category.contains("Overweight_Level_II", ignoreCase = true) -> android.graphics.Color.parseColor("#FF9800") // Orange
            category.contains("Obesity_Type_I", ignoreCase = true) -> android.graphics.Color.parseColor("#F44336") // Red
            category.contains("Obesity_Type_II", ignoreCase = true) -> android.graphics.Color.parseColor("#D32F2F") // Dark Red
            category.contains("Obesity_Type_III", ignoreCase = true) -> android.graphics.Color.parseColor("#B71C1C") // Very Dark Red
            else -> android.graphics.Color.parseColor("#757575") // Gray
        }
    }
    
    /**
     * Получает описание категории ожирения
     */
    fun getDescriptionForCategory(category: String): String {
        return when {
            category.contains("Insufficient", ignoreCase = true) -> "Insufficient Weight - Below normal weight range"
            category.contains("Normal", ignoreCase = true) -> "Normal Weight - Healthy weight range"
            category.contains("Overweight_Level_I", ignoreCase = true) -> "Overweight Level I - Slightly above normal"
            category.contains("Overweight_Level_II", ignoreCase = true) -> "Overweight Level II - Moderately above normal"
            category.contains("Obesity_Type_I", ignoreCase = true) -> "Obesity Type I - Mild obesity"
            category.contains("Obesity_Type_II", ignoreCase = true) -> "Obesity Type II - Moderate obesity"
            category.contains("Obesity_Type_III", ignoreCase = true) -> "Obesity Type III - Severe obesity"
            else -> "Unknown category"
        }
    }
}



