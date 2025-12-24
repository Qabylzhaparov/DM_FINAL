package com.example.dmfinal.service

import android.content.Context
import android.util.Log
import com.example.dmfinal.api.ObesityInputRequest
import com.example.dmfinal.api.PredictionApi
import com.example.dmfinal.model.UserData
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Результат предсказания с уверенностью модели
 */
data class PredictionResult(
    val prediction: String,
    val confidence: Double // от 0.0 до 1.0
)

/**
 * Сервис для работы с ML моделью через FastAPI
 * 
 * Для использования необходимо запустить Python FastAPI сервер
 * Пример: python api.py (запускается на http://0.0.0.0:8000)
 * 
 * Для эмулятора Android Studio используйте: http://10.0.2.2:8000
 * Для реального устройства используйте IP адрес вашего компьютера в локальной сети
 */
class PredictionService(private val context: Context) {
    
    companion object {
        private const val TAG = "PredictionService"
        // Базовый URL для API (измените на адрес вашего сервера)
        // Для эмулятора Android Studio используйте http://10.0.2.2:8000
        // Для реального устройства используйте IP адрес вашего компьютера в локальной сети
        // Узнать IP можно запустив: python get_ip.py (в папке с api.py)
        private const val BASE_URL = "http://192.168.1.14:8000/"
    }
    
    private val api: PredictionApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PredictionApi::class.java)
    }
    
    /**
     * Предсказывает уровень ожирения на основе данных пользователя
     */
    suspend fun predict(userData: UserData): Result<PredictionResult> {
        return try {
            Log.d(TAG, "Отправка запроса на: $BASE_URL")
            Log.d(TAG, "Данные пользователя: gender=${userData.gender}, age=${userData.age}, height=${userData.height}, weight=${userData.weight}")
            
            // Преобразуем UserData в формат, ожидаемый FastAPI
            val request = convertToApiRequest(userData)
            
            // Отправка запроса к API
            val response = api.predict(request)
            
            if (response.isSuccessful && response.body() != null) {
                val predictionResponse = response.body()!!
                val predictedClass = predictionResponse.predicted_class
                val confidence = predictionResponse.confidence
                
                Log.d(TAG, "Предсказание успешно: $predictedClass (уверенность: ${String.format("%.2f%%", confidence * 100)})")
                Result.success(PredictionResult(predictedClass, confidence))
            } else {
                val errorBody = response.errorBody()?.string() ?: "No error body"
                val errorMsg = "API error: ${response.code()} - ${response.message()}\n$errorBody"
                Log.e(TAG, errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при предсказании", e)
            Result.failure(e)
        }
    }
    
    /**
     * Преобразует UserData в формат ObesityInputRequest для API
     */
    private fun convertToApiRequest(userData: UserData): ObesityInputRequest {
        return ObesityInputRequest(
            Gender = userData.gender,
            Age = userData.age.toDouble(),
            Height = userData.height,
            Weight = userData.weight,
            family_history_with_overweight = if (userData.familyHistoryWithOverweight) "yes" else "no",
            FAVC = if (userData.favc) "yes" else "no",
            FCVC = userData.fcvc,
            NCP = userData.ncp,
            CAEC = userData.caec,
            SMOKE = if (userData.smoke) "yes" else "no",
            CH2O = userData.ch2o,
            SCC = if (userData.scc) "yes" else "no",
            FAF = userData.faf,
            TUE = userData.tue,
            CALC = userData.calc,
            MTRANS = userData.mtrans
        )
    }
    
    /**
     * Мок-метод для тестирования без API
     * Возвращает случайную категорию для демонстрации
     */
    fun predictMock(userData: UserData): PredictionResult {
        // Это временный метод для тестирования UI без реального API
        // В реальном приложении этот метод должен быть удален
        val categories = listOf(
            "Insufficient_Weight",
            "Normal_Weight",
            "Overweight_Level_I",
            "Overweight_Level_II",
            "Obesity_Type_I",
            "Obesity_Type_II",
            "Obesity_Type_III"
        )
        // Простая эвристика на основе возраста и активности
        val prediction = when {
            userData.age < 25 && userData.faf > 60 -> "Normal_Weight"
            userData.age > 50 && userData.faf < 30 -> "Obesity_Type_II"
            userData.faf < 20 -> "Overweight_Level_II"
            else -> categories.random()
        }
        
        // Генерируем случайную уверенность от 0.65 до 0.95
        val confidence = 0.65 + (Math.random() * 0.3)
        
        return PredictionResult(prediction, confidence)
    }
}



