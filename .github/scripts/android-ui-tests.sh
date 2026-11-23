#!/usr/bin/env bash
set -Eeuo pipefail

# Корневая папка репозитория в GitHub Actions.
ROOT_DIR="${GITHUB_WORKSPACE:-$(pwd)}"

# Сохраняем Android logcat даже при ошибке тестов.
trap 'adb logcat -d > "$ROOT_DIR/android-logcat.log" 2>/dev/null || true' EXIT

# Ждём полной загрузки Android Emulator.
for _ in $(seq 1 60); do
    DEVICE_STATE="$(adb get-state 2>/dev/null || true)"
    BOOT_STATUS="$(adb shell getprop sys.boot_completed 2>/dev/null | tr -d "\r" || true)"
    if [[ "$DEVICE_STATE" == "device" && "$BOOT_STATUS" == "1" ]]; then
        break
    fi
    sleep 2
done

# Проверяем готовность Android Emulator.
if [[ "$(adb get-state 2>/dev/null || true)" != "device" ]] || [[ "$(adb shell getprop sys.boot_completed 2>/dev/null | tr -d "\r")" != "1" ]]; then
    adb devices -l
    echo "Android Emulator did not become ready."
    exit 1
fi

# Проверяем наличие Chrome.
adb shell pm path com.android.chrome

# Отключаем Play Store, чтобы исключить фоновые обновления.
adb shell pm disable-user --user 0 com.android.vending

# Скачиваем Firefox из официального архива Mozilla.
FIREFOX_APK="$ROOT_DIR/firefox-160.0.1-x86_64.apk"
curl --fail --location --retry 3 \
    "https://archive.mozilla.org/pub/fenix/releases/160.0.1/android/fenix-160.0.1-android-x86_64/fenix-160.0.1.multi.android-x86_64.apk" \
    --output "$FIREFOX_APK"

# Устанавливаем Firefox в Android Emulator.
adb install -r "$FIREFOX_APK"
rm -f "$FIREFOX_APK"

# Проверяем установку Firefox.
adb shell pm path org.mozilla.firefox

# Запускаем Appium с поддержкой автоматической загрузки ChromeDriver.
nohup appium --allow-insecure=uiautomator2:chromedriver_autodownload > "$ROOT_DIR/appium.log" 2>&1 &

# Ждём готовности Appium Server.
for _ in $(seq 1 30); do
    if curl --silent --fail http://127.0.0.1:4723/status > /dev/null; then
        break
    fi
    sleep 2
done

# Проверяем, что Appium действительно запустился.
if ! curl --silent --fail http://127.0.0.1:4723/status > /dev/null; then
    cat "$ROOT_DIR/appium.log"
    echo "Appium Server did not become ready."
    exit 1
fi

# Переходим в Maven-модуль с автотестами.
cd "$ROOT_DIR/api"

# Запускаем Android Chrome UI-тесты.
echo "Running Android Chrome UI tests..."
mvn -B -Dandroid.browser=chrome -Dtest=AndroidCharacterUiTest test

# Запускаем Android Firefox UI-тесты в том же эмуляторе.
echo "Running Android Firefox UI tests..."
mvn -B -Dandroid.browser=firefox -Dtest=AndroidCharacterUiTest test
