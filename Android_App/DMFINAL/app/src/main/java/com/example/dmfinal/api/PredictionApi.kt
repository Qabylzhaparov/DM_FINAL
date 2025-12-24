package com.example.dmfinal.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * API интерфейс для предсказания уровня ожирения
 * Соответствует структуре FastAPI бэкенда
 */
interface PredictionApi {
    @POST("predict")
    suspend fun predict(@Body request: ObesityInputRequest): Response<ObesityPredictionResponse>
}

/**
 * Запрос в формате, ожидаемом FastAPI бэкендом
 */
data class ObesityInputRequest(
    val Gender: String, // "Male" or "Female"
    val Age: Double,
    val Height: Double, // в метрах
    val Weight: Double, // в килограммах
    val family_history_with_overweight: String, // "yes" or "no"
    val FAVC: String, // "yes" or "no"
    val FCVC: Double, // 1-3
    val NCP: Double, // 1-4
    val CAEC: String, // "no", "Sometimes", "Frequently", "Always"
    val SMOKE: String, // "yes" or "no"
    val CH2O: Double, // 1-3
    val SCC: String, // "yes" or "no"
    val FAF: Double, // 0-3
    val TUE: Double, // 0-2
    val CALC: String, // "no", "Sometimes", "Frequently", "Always"
    val MTRANS: String // "Public_Transportation", "Automobile", "Walking", "Motorbike", "Bike"
)

/**
 * Ответ от FastAPI бэкенда
 */
data class ObesityPredictionResponse(
    val predicted_class: String,
    val confidence: Double,
    val all_probabilities: Map<String, Double>
)



