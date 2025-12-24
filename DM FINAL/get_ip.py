"""
Утилита для определения IP адреса компьютера
для подключения из Android приложения
"""

import socket

def get_local_ip():
    """Получает локальный IP адрес компьютера"""
    try:
        # Подключаемся к внешнему серверу, чтобы узнать наш IP
        s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        s.connect(("8.8.8.8", 80))
        ip = s.getsockname()[0]
        s.close()
        return ip
    except Exception as e:
        # Fallback на локальный IP
        hostname = socket.gethostname()
        ip = socket.gethostbyname(hostname)
        return ip

def get_all_ips():
    """Получает все IP адреса компьютера"""
    import socket
    hostname = socket.gethostname()
    ips = []
    
    # Получаем все IP адреса
    try:
        for addr in socket.getaddrinfo(hostname, None):
            ip = addr[4][0]
            if ip not in ips and not ip.startswith('127.'):
                ips.append(ip)
    except:
        pass
    
    # Также пробуем получить через подключение
    try:
        s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        s.connect(("8.8.8.8", 80))
        ip = s.getsockname()[0]
        s.close()
        if ip not in ips:
            ips.append(ip)
    except:
        pass
    
    return ips

if __name__ == "__main__":
    print("=" * 60)
    print("🌐 IP АДРЕСА ДЛЯ ПОДКЛЮЧЕНИЯ ИЗ ANDROID")
    print("=" * 60)
    print()
    
    main_ip = get_local_ip()
    all_ips = get_all_ips()
    
    print(f"✅ Основной IP адрес: {main_ip}")
    print()
    print("📱 Используйте в Android приложении:")
    print(f"   http://{main_ip}:8000")
    print()
    print("🔗 Endpoint для предсказания:")
    print(f"   http://{main_ip}:8000/predict")
    print()
    
    if len(all_ips) > 1:
        print("📋 Все доступные IP адреса:")
        for ip in all_ips:
            marker = " ← Рекомендуется" if ip == main_ip else ""
            print(f"   • {ip}:8000{marker}")
        print()
    
    print("=" * 60)
    print("⚠️  ВАЖНО:")
    print("=" * 60)
    print("1. Убедитесь, что компьютер и Android устройство")
    print("   подключены к ОДНОЙ Wi-Fi сети")
    print()
    print("2. Проверьте, что файрвол разрешает подключения")
    print("   на порт 8000")
    print()
    print("3. Если используете эмулятор Android:")
    print("   • Android Studio Emulator: используйте 10.0.2.2")
    print("   • Genymotion: используйте 10.0.3.2")
    print("   • Реальное устройство: используйте IP выше")
    print("=" * 60)

