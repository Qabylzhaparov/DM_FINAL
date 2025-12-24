# Obesity Level Prediction Android App

Android приложение для предсказания уровня ожирения на основе lifestyle-данных пользователя.

## Описание

Приложение собирает данные пользователя (без веса и роста) и использует обученную модель машинного обучения (Random Forest) для предсказания категории ожирения (NObeyesdad).

## Возможные категории ожирения

- Insufficient_Weight
- Normal_Weight
- Overweight_Level_I
- Overweight_Level_II
- Obesity_Type_I
- Obesity_Type_II
- Obesity_Type_III

## Структура проекта

```
app/src/main/java/com/example/dmfinal/
├── MainActivity.kt          # Главный экран с формой ввода
├── ResultActivity.kt        # Экран результатов предсказания
├── model/
│   └── UserData.kt          # Модель данных пользователя
├── api/
│   └── PredictionApi.kt     # Интерфейс API для предсказания
├── service/
│   └── PredictionService.kt # Сервис для работы с ML моделью
└── util/
    └── DataPreprocessor.kt  # Предобработка данных перед подачей в модель
```

## Настройка

### 1. Android приложение

Приложение готово к использованию. По умолчанию оно настроено на подключение к API по адресу `http://10.0.2.2:5000` (для эмулятора Android).

Для реального устройства измените `BASE_URL` в `PredictionService.kt` на IP адрес вашего компьютера в локальной сети.

### 2. Python Backend

Для работы с реальной моделью необходимо запустить Python backend сервер:

1. Установите зависимости:
```bash
pip install flask flask-cors scikit-learn pandas numpy
```

2. Поместите ваш файл модели `model.pkl` в директорию с `backend_example.py`

3. Запустите сервер:
```bash
python backend_example.py
```

4. Сервер будет доступен на `http://localhost:5000` (для эмулятора) или `http://<ваш-ip>:5000` (для реального устройства)

### 3. Настройка API endpoint

Откройте `app/src/main/java/com/example/dmfinal/service/PredictionService.kt` и измените `BASE_URL`:

- Для эмулятора: `http://10.0.2.2:5000`
- Для реального устройства: `http://<IP-вашего-компьютера>:5000`

## Использование

1. Запустите приложение
2. Заполните все поля формы:
   - Gender (Пол)
   - Age (Возраст)
   - Family history with overweight (Семейная история ожирения)
   - Frequently consume high caloric food (Частое потребление высококалорийной пищи)
   - Vegetable consumption (Потребление овощей, раз в день)
   - Number of main meals (Количество основных приемов пищи в неделю)
   - Consumption of food between meals (Потребление пищи между приемами)
   - Smoke (Курение)
   - Water consumption (Потребление воды, литров в день)
   - Calories consumption monitoring (Мониторинг потребления калорий)
   - Physical activity (Физическая активность, минут в день)
   - Screen time (Время перед экраном, часов в день)
   - Alcohol consumption (Потребление алкоголя)
   - Transportation method (Способ передвижения)

3. Нажмите "Predict Obesity Level"
4. Просмотрите результат на экране результатов

## Предобработка данных

Приложение автоматически преобразует введенные данные в формат, требуемый моделью:

- Бинарные признаки → 0/1
- Порядковые признаки (CAEC, CALC) → 0.0/0.33/0.67/1.0
- Категориальный признак (MTRANS) → One-Hot Encoding
- Числовые признаки → стандартизируются через StandardScaler

## Мок-режим

Если API недоступен, приложение использует мок-метод для демонстрации функциональности. Для реальных предсказаний необходимо настроить Python backend.

## Зависимости

- Retrofit 2.9.0 - для HTTP запросов
- Gson 2.10.1 - для JSON сериализации
- Kotlin Coroutines - для асинхронных операций
- Material Design Components - для UI

## Будущие улучшения

- Добавление Height и Weight для расчета BMI
- Сохранение истории предсказаний
- Рекомендации по диете и активности
- Конвертация модели в ONNX/TensorFlow Lite для прямого использования в приложении

## Примечания

- Все поля формы обязательны для заполнения
- Приложение валидирует ввод перед отправкой
- Приложение показывает прогресс загрузки во время предсказания



