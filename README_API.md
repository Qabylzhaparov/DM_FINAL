# 🚀 Obesity Prediction API

REST API для предсказания уровня ожирения на основе физических и поведенческих данных. Создано для Android приложения.

## 📋 Требования

- Python 3.8+
- Файл модели `obesity_model.pkl` (создается в ноутбуке)

## 🔧 Установка

1. Установите зависимости:
```bash
pip install -r requirements.txt
```

2. Убедитесь, что файл `obesity_model.pkl` находится в той же директории, что и `api.py`

## 🚀 Запуск

### Локальный запуск:
```bash
python api.py
```

Или с помощью uvicorn:
```bash
uvicorn api:app --host 0.0.0.0 --port 8000 --reload
```

### Запуск на сервере:
```bash
uvicorn api:app --host 0.0.0.0 --port 8000 --workers 4
```

API будет доступен по адресу: `http://localhost:8000`

## 📚 Документация API

После запуска сервера документация доступна по адресам:
- Swagger UI: `http://localhost:8000/docs`
- ReDoc: `http://localhost:8000/redoc`

## 🔌 Endpoints

### 1. Корневой endpoint
```
GET /
```
Возвращает информацию об API

### 2. Проверка здоровья
```
GET /health
```
Проверяет, загружена ли модель

### 3. Предсказание уровня ожирения
```
POST /predict
```

**Тело запроса (JSON):**
```json
{
  "Gender": "Male",
  "Age": 25.0,
  "Height": 1.75,
  "Weight": 75.0,
  "family_history_with_overweight": "no",
  "FAVC": "no",
  "FCVC": 2.0,
  "NCP": 3.0,
  "CAEC": "Sometimes",
  "SMOKE": "no",
  "CH2O": 2.0,
  "SCC": "no",
  "FAF": 1.0,
  "TUE": 1.0,
  "CALC": "Sometimes",
  "MTRANS": "Public_Transportation"
}
```

**Ответ:**
```json
{
  "predicted_class": "Normal_Weight",
  "confidence": 0.95,
  "all_probabilities": {
    "Insufficient_Weight": 0.01,
    "Normal_Weight": 0.95,
    "Obesity_Type_I": 0.02,
    "Obesity_Type_II": 0.01,
    "Obesity_Type_III": 0.0,
    "Overweight_Level_I": 0.01,
    "Overweight_Level_II": 0.0
  }
}
```

## 📱 Использование из Android приложения

### Пример запроса (Kotlin/Java):

```kotlin
// Retrofit Interface
interface ObesityApi {
    @POST("/predict")
    suspend fun predictObesity(@Body input: ObesityInput): ObesityPrediction
}

// Data Classes
data class ObesityInput(
    val Gender: String,
    val Age: Double,
    val Height: Double,
    val Weight: Double,
    val family_history_with_overweight: String,
    val FAVC: String,
    val FCVC: Double,
    val NCP: Double,
    val CAEC: String,
    val SMOKE: String,
    val CH2O: Double,
    val SCC: String,
    val FAF: Double,
    val TUE: Double,
    val CALC: String,
    val MTRANS: String
)

data class ObesityPrediction(
    val predicted_class: String,
    val confidence: Double,
    val all_probabilities: Map<String, Double>
)

// Использование
val retrofit = Retrofit.Builder()
    .baseUrl("http://YOUR_SERVER_IP:8000")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val api = retrofit.create(ObesityApi::class.java)

val input = ObesityInput(
    Gender = "Male",
    Age = 25.0,
    Height = 1.75,
    Weight = 75.0,
    family_history_with_overweight = "no",
    FAVC = "no",
    FCVC = 2.0,
    NCP = 3.0,
    CAEC = "Sometimes",
    SMOKE = "no",
    CH2O = 2.0,
    SCC = "no",
    FAF = 1.0,
    TUE = 1.0,
    CALC = "Sometimes",
    MTRANS = "Public_Transportation"
)

val prediction = api.predictObesity(input)
```

### Пример запроса (HTTP):

```bash
curl -X POST "http://localhost:8000/predict" \
  -H "Content-Type: application/json" \
  -d '{
    "Gender": "Male",
    "Age": 25.0,
    "Height": 1.75,
    "Weight": 75.0,
    "family_history_with_overweight": "no",
    "FAVC": "no",
    "FCVC": 2.0,
    "NCP": 3.0,
    "CAEC": "Sometimes",
    "SMOKE": "no",
    "CH2O": 2.0,
    "SCC": "no",
    "FAF": 1.0,
    "TUE": 1.0,
    "CALC": "Sometimes",
    "MTRANS": "Public_Transportation"
  }'
```

## 🔒 Безопасность

⚠️ **Важно для продакшена:**
1. Измените `allow_origins=["*"]` на конкретные домены в `api.py`
2. Добавьте аутентификацию (API ключи, JWT токены)
3. Используйте HTTPS
4. Добавьте rate limiting
5. Валидируйте и санитизируйте входные данные

## 🐳 Docker (опционально)

Создайте `Dockerfile`:
```dockerfile
FROM python:3.11-slim

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY api.py .
COPY obesity_model.pkl .

EXPOSE 8000

CMD ["uvicorn", "api:app", "--host", "0.0.0.0", "--port", "8000"]
```

Сборка и запуск:
```bash
docker build -t obesity-api .
docker run -p 8000:8000 obesity-api
```

## 📝 Примечания

- Убедитесь, что модель `obesity_model.pkl` создана в ноутбуке перед запуском API
- Все входные данные валидируются через Pydantic модели
- API автоматически применяет ту же предобработку, что и в ноутбуке
- CORS настроен для работы с Android приложениями

## 🐛 Отладка

Если возникают проблемы:
1. Проверьте, что модель загружена: `GET /health`
2. Проверьте логи сервера
3. Убедитесь, что все зависимости установлены
4. Проверьте формат входных данных

