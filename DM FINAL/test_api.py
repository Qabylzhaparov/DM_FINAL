"""
Тестовый скрипт для проверки API
"""

import requests
import json

# URL API (измените на ваш адрес)
API_URL = "http://localhost:8000"

def test_health():
    """Тест проверки здоровья API"""
    print("=" * 50)
    print("Тест 1: Проверка здоровья API")
    print("=" * 50)
    response = requests.get(f"{API_URL}/health")
    print(f"Status Code: {response.status_code}")
    print(f"Response: {json.dumps(response.json(), indent=2, ensure_ascii=False)}")
    print()

def test_root():
    """Тест корневого endpoint"""
    print("=" * 50)
    print("Тест 2: Корневой endpoint")
    print("=" * 50)
    response = requests.get(f"{API_URL}/")
    print(f"Status Code: {response.status_code}")
    print(f"Response: {json.dumps(response.json(), indent=2, ensure_ascii=False)}")
    print()

def test_predict():
    """Тест предсказания"""
    print("=" * 50)
    print("Тест 3: Предсказание уровня ожирения")
    print("=" * 50)
    
    # Пример данных
    test_data = {
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
    
    print("Входные данные:")
    print(json.dumps(test_data, indent=2, ensure_ascii=False))
    print()
    
    response = requests.post(
        f"{API_URL}/predict",
        json=test_data,
        headers={"Content-Type": "application/json"}
    )
    
    print(f"Status Code: {response.status_code}")
    if response.status_code == 200:
        result = response.json()
        print("Результат предсказания:")
        print(json.dumps(result, indent=2, ensure_ascii=False))
        print()
        print(f"Предсказанный класс: {result['predicted_class']}")
        print(f"Уверенность: {result['confidence']:.2%}")
    else:
        print(f"Ошибка: {response.text}")
    print()

if __name__ == "__main__":
    print("\n🧪 Тестирование Obesity Prediction API\n")
    
    try:
        test_health()
        test_root()
        test_predict()
        
        print("=" * 50)
        print("✅ Все тесты завершены!")
        print("=" * 50)
    except requests.exceptions.ConnectionError:
        print("❌ Ошибка: Не удалось подключиться к API")
        print("Убедитесь, что сервер запущен на", API_URL)
    except Exception as e:
        print(f"❌ Ошибка: {e}")

