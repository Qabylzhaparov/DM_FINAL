package com.example.dmfinal.model

/**
 * Модель данных пользователя для предсказания уровня ожирения
 */
data class UserData(
    val gender: String, // "Male" or "Female"
    val age: Int,
    val height: Double, // Рост в метрах
    val weight: Double, // Вес в килограммах
    val familyHistoryWithOverweight: Boolean, // yes/no
    val favc: Boolean, // Frequently consume high caloric food
    val fcvc: Double, // количество раз в день потребления овощей (1-3)
    val ncp: Double, // количество основных приемов пищи (1-4)
    val caec: String, // "no", "Sometimes", "Frequently", "Always"
    val smoke: Boolean,
    val ch2o: Double, // потребление воды (1-3)
    val scc: Boolean, // Calories consumption monitoring
    val faf: Double, // физическая активность (0-3)
    val tue: Double, // время использования технологий (0-2)
    val calc: String, // "no", "Sometimes", "Frequently", "Always"
    val mtrans: String // "Bike", "Motorbike", "Public_Transportation", "Walking", "Automobile"
)



