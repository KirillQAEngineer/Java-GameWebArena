#!/usr/bin/env bash
set -Eeuo pipefail

ROOT_DIR="${GITHUB_WORKSPACE:-$(pwd)}"

trap 'adb logcat -d > "$ROOT_DIR/android-logcat.log" 2>/dev/null || true' EXIT

for _ in $(seq 1 60); do
    DEVICE_STATE="$(adb get-state 2>/dev/null || true)"
    BOOT_STATUS="$(adb shell getprop sys.boot_completed 2>/dev/null | tr -d "\r" || true)"
    if [[ "$DEVICE_STATE" == "device" && "$BOOT_STATUS" == "1" ]]; then
        break
    fi
    sleep 2
done

if [[ "$(adb get-state 2>/dev/null || true)" != "device" ]] || [[ "$(adb shell getprop sys.boot_completed 2>/dev/null | tr -d "\r")" != "1" ]]; then
    adb devices -l
    echo "Android Emulator did not become ready."
    exit 1
fi

adb shell pm path com.android.chrome
# Отключаем Play Store на время тестов, чтобы исключить фоновые обновления приложений.
adb shell pm disable-user --user 0 com.android.vending

nohup appium --allow-insecure=uiautomator2:chromedriver_autodownload > "$ROOT_DIR/appium.log" 2>&1 &

for _ in $(seq 1 30); do
    if curl --silent --fail http://127.0.0.1:4723/status > /dev/null; then
        break
    fi
    sleep 2
done

if ! curl --silent --fail http://127.0.0.1:4723/status > /dev/null; then
    cat "$ROOT_DIR/appium.log"
    exit 1
fi

cd "$ROOT_DIR/api"
mvn -B -Dtest=AndroidCharacterUiTest test
