#!/usr/bin/env bash
set -Eeuo pipefail

# Корневая папка репозитория в GitHub Actions.
ROOT_DIR="${GITHUB_WORKSPACE:-$(pwd)}"

# Получаем браузер из аргумента запуска.
BROWSER="${1:-}"

# Проверяем допустимое значение браузера.
if [[ "$BROWSER" != "chrome" && "$BROWSER" != "firefox" ]]; then
    echo "Usage: bash .github/scripts/android-ui-tests.sh <chrome|firefox>"
    exit 1
fi

# Определяем путь для временного APK Firefox.
FIREFOX_APK="$ROOT_DIR/firefox-160.0.1-x86_64.apk"

# Сохраняем Android logcat и удаляем временный APK при завершении.
save_android_logs() {
    adb logcat -d > "$ROOT_DIR/android-logcat.log" 2>/dev/null || true
    rm -f "$FIREFOX_APK"
}
trap save_android_logs EXIT

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

# Отключаем Play Store, чтобы исключить фоновые обновления.
adb shell pm disable-user --user 0 com.android.vending

# Устанавливаем и проверяем только выбранный браузер.
if [[ "$BROWSER" == "chrome" ]]; then
    # Проверяем наличие Chrome в эмуляторе.
    adb shell pm path com.android.chrome
else
    # Скачиваем Firefox из официального архива Mozilla.
    curl --fail --location --retry 3 \
        "https://archive.mozilla.org/pub/fenix/releases/160.0.1/android/fenix-160.0.1-android-x86_64/fenix-160.0.1.multi.android-x86_64.apk" \
        --output "$FIREFOX_APK"

    # Устанавливаем Firefox в Android Emulator.
    adb install -r "$FIREFOX_APK"

    # Проверяем установку Firefox.
    adb shell pm path org.mozilla.firefox
fi

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

# Запускаем UI-тесты только выбранного браузера.
echo "Running Android $BROWSER UI tests..."
mvn -B "-Dandroid.browser=$BROWSER" -Dtest=AndroidCharacterUiTest test
