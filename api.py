"""
FastAPI Backend for Obesity Level Prediction
API для Android приложения для предсказания уровня ожирения
"""

from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field
from typing import Literal
import pickle
import pandas as pd
import numpy as np
import os

# =====================================================
# FastAPI App Initialization
# =====================================================

app = FastAPI(
    title="Obesity Prediction API",
    description="API для предсказания уровня ожирения на основе физических и поведенческих данных",
    version="1.0.0"
)

# CORS middleware для Android приложения
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # В продакшене укажите конкретные домены
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# =====================================================
# Pydantic Models для валидации входных данных
# =====================================================

class ObesityInput(BaseModel):
    """Модель входных данных для предсказания"""
    Gender: Literal["Male", "Female"] = Field(..., description="Пол: Male или Female")
    Age: float = Field(..., ge=0, le=120, description="Возраст (лет)")
    Height: float = Field(..., gt=0, le=3, description="Рост (метры)")
    Weight: float = Field(..., gt=0, le=300, description="Вес (килограммы)")
    family_history_with_overweight: Literal["yes", "no"] = Field(..., description="Семейная история ожирения")
    FAVC: Literal["yes", "no"] = Field(..., description="Частое потребление высококалорийной пищи")
    FCVC: float = Field(..., ge=1, le=3, description="Частота потребления овощей (1-3)")
    NCP: float = Field(..., ge=1, le=4, description="Количество основных приемов пищи (1-4)")
    CAEC: Literal["no", "Sometimes", "Frequently", "Always"] = Field(..., description="Потребление пищи между приемами")
    SMOKE: Literal["yes", "no"] = Field(..., description="Курение")
    CH2O: float = Field(..., ge=1, le=3, description="Потребление воды (1-3)")
    SCC: Literal["yes", "no"] = Field(..., description="Мониторинг калорий")
    FAF: float = Field(..., ge=0, le=3, description="Физическая активность (0-3)")
    TUE: float = Field(..., ge=0, le=2, description="Время использования технологий (0-2)")
    CALC: Literal["no", "Sometimes", "Frequently", "Always"] = Field(..., description="Потребление алкоголя")
    MTRANS: Literal["Public_Transportation", "Automobile", "Walking", "Motorbike", "Bike"] = Field(
        ..., description="Транспорт"
    )

    class Config:
        json_schema_extra = {
            "example": {
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
        }


class ObesityPrediction(BaseModel):
    """Модель ответа с предсказанием"""
    predicted_class: str = Field(..., description="Предсказанный класс ожирения")
    confidence: float = Field(..., ge=0, le=1, description="Уверенность модели (вероятность)")
    all_probabilities: dict = Field(..., description="Вероятности для всех классов")


# =====================================================
# Глобальные переменные для модели
# =====================================================

MODEL_PATH = "obesity_model.pkl"
model_package = None
feature_names = None


# =====================================================
# Функции предобработки данных
# =====================================================

def preprocess_data(input_data: dict) -> pd.DataFrame:
    """
    Предобработка входных данных (такая же как в ноутбуке)
    
    Args:
        input_data: словарь с входными данными
        
    Returns:
        DataFrame с предобработанными признаками
    """
    # Создаем DataFrame из входных данных
    df = pd.DataFrame([input_data])
    
    # =====================================================
    # 1. BINARY ENCODING
    # =====================================================
    binary_maps = {
        "Gender": {"Male": 1, "Female": 0},
        "family_history_with_overweight": {"yes": 1, "no": 0},
        "FAVC": {"yes": 1, "no": 0},
        "SMOKE": {"yes": 1, "no": 0},
        "SCC": {"yes": 1, "no": 0}
    }
    
    for col, mapping in binary_maps.items():
        if col in df.columns:
            df[col] = df[col].map(mapping)
    
    # =====================================================
    # 2. FUZZY / ORDINAL ENCODING
    # =====================================================
    fuzzy_maps = {
        "CAEC": {"no": 0.0, "Sometimes": 0.33, "Frequently": 0.67, "Always": 1.0},
        "CALC": {"no": 0.0, "Sometimes": 0.33, "Frequently": 0.67, "Always": 1.0}
    }
    
    for col, mapping in fuzzy_maps.items():
        if col in df.columns:
            df[col] = df[col].map(mapping)
    
    # =====================================================
    # 3. DISCRETIZATION (Age, Height, Weight with step of 5)
    # =====================================================
    # Height is in meters → convert to centimeters first
    if "Height" in df.columns:
        df["Height_cm"] = df["Height"] * 100
    
    def discretize_step_5(series):
        return (series // 5) * 5
    
    for col in ["Age", "Height_cm", "Weight"]:
        if col in df.columns:
            df[f"{col}_cat"] = discretize_step_5(df[col])
    
    # =====================================================
    # 4. NOMINAL ENCODING: MTRANS (One-Hot Encoding)
    # =====================================================
    if "MTRANS" in df.columns:
        # Все возможные значения MTRANS
        mtrans_values = ["Public_Transportation", "Automobile", "Walking", "Motorbike", "Bike"]
        
        # Создаем one-hot encoding вручную (drop_first=True означает убрать первый столбец)
        for i, value in enumerate(mtrans_values[1:], start=1):  # Пропускаем первый (Public_Transportation)
            col_name = f"MTRANS_{value}"
            df[col_name] = (df["MTRANS"] == value).astype(int)
        
        # Удаляем исходную колонку MTRANS
        df = df.drop(columns=["MTRANS"])
    
    # Убеждаемся, что все числовые колонки имеют правильный тип
    numeric_cols = df.select_dtypes(include=[np.number]).columns
    for col in numeric_cols:
        df[col] = pd.to_numeric(df[col], errors='coerce')
    
    # Заполняем NaN значения нулями (на случай если что-то пошло не так)
    df = df.fillna(0)
    
    return df


def ensure_feature_order(df: pd.DataFrame, required_features: list) -> pd.DataFrame:
    """
    Убеждается, что DataFrame содержит все необходимые признаки в правильном порядке
    
    Args:
        df: DataFrame с признаками
        required_features: список необходимых признаков в правильном порядке
        
    Returns:
        DataFrame с правильным порядком признаков
    """
    # Добавляем недостающие признаки со значением 0
    for feature in required_features:
        if feature not in df.columns:
            df[feature] = 0
    
    # Переупорядочиваем колонки в правильном порядке
    df = df[required_features]
    
    return df


# =====================================================
# Загрузка модели при старте приложения
# =====================================================

@app.on_event("startup")
async def load_model():
    """Загружает модель при старте приложения"""
    global model_package, feature_names
    
    if not os.path.exists(MODEL_PATH):
        raise FileNotFoundError(
            f"Модель не найдена: {MODEL_PATH}\n"
            "Убедитесь, что файл obesity_model.pkl находится в той же директории, что и api.py"
        )
    
    with open(MODEL_PATH, "rb") as f:
        model_package = pickle.load(f)
    
    feature_names = model_package['feature_names']
    print(f"✓ Модель загружена успешно!")
    print(f"✓ Количество признаков: {len(feature_names)}")
    print(f"✓ Тип модели: {model_package.get('model_type', 'Unknown')}")


# =====================================================
# API Endpoints
# =====================================================

@app.get("/")
async def root():
    """Корневой endpoint"""
    return {
        "message": "Obesity Prediction API",
        "version": "1.0.0",
        "status": "running",
        "endpoints": {
            "predict": "/predict",
            "health": "/health",
            "docs": "/docs"
        }
    }


@app.get("/health")
async def health_check():
    """Проверка здоровья API"""
    if model_package is None:
        return {"status": "error", "message": "Модель не загружена"}
    return {
        "status": "healthy",
        "model_loaded": True,
        "features_count": len(feature_names) if feature_names else 0
    }


@app.post("/predict", response_model=ObesityPrediction)
async def predict_obesity(input_data: ObesityInput):
    """
    Предсказание уровня ожирения
    
    Принимает данные о человеке и возвращает предсказанный уровень ожирения
    с вероятностями для всех классов.
    """
    if model_package is None:
        raise HTTPException(status_code=503, detail="Модель не загружена")
    
    try:
        # Конвертируем Pydantic модель в словарь
        input_dict = input_data.model_dump()
        
        # Предобработка данных
        df_processed = preprocess_data(input_dict)
        
        # Убеждаемся, что признаки в правильном порядке
        df_processed = ensure_feature_order(df_processed, feature_names)
        
        # Получаем модель и label encoder
        model = model_package['model']
        label_encoder = model_package['label_encoder']
        
        # Делаем предсказание
        prediction_encoded = model.predict(df_processed)
        prediction = label_encoder.inverse_transform(prediction_encoded)[0]
        
        # Получаем вероятности для всех классов (если модель поддерживает)
        try:
            probabilities = model.predict_proba(df_processed)[0]
            class_names = label_encoder.classes_
            all_probabilities = {
                class_name: float(prob) 
                for class_name, prob in zip(class_names, probabilities)
            }
            confidence = float(max(probabilities))
        except AttributeError:
            # Если модель не поддерживает predict_proba
            all_probabilities = {prediction: 1.0}
            confidence = 1.0
        
        return ObesityPrediction(
            predicted_class=prediction,
            confidence=confidence,
            all_probabilities=all_probabilities
        )
    
    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail=f"Ошибка при предсказании: {str(e)}"
        )


# =====================================================
# Запуск приложения
# =====================================================

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)

