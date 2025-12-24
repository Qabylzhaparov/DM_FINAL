import pandas as pd
from tkinter import Tk, filedialog
from ydata_profiling import ProfileReport

# --- Скрываем окно tkinter ---
root = Tk()
root.withdraw()

# --- Выбор Excel-файла через проводник ---
file_path = filedialog.askopenfilename(
    title="Выберите Excel файл",
    filetypes=[("Excel files", "*.xlsx *.xls")]
)

if not file_path:
    raise Exception("Файл не выбран")

# --- Загрузка Excel ---
df = pd.read_excel(file_path)

# --- Базовый обзор ---
print("📊 Размер датасета:", df.shape)
print("\n📌 Типы данных:")
print(df.dtypes)

print("\n❗ Пропущенные значения:")
print(df.isnull().sum())

print("\n📈 Статистика числовых признаков:")
print(df.describe())

# --- Автоматический ML-отчёт ---
profile = ProfileReport(
    df,
    title="ML Dataset Report",
    explorative=True
)

profile.to_file("ml_dataset_report.html")

print("\n✅ Готово!")
print("📄 Отчёт сохранён как ml_dataset_report.html")
