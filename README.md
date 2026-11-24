# Java Game Web Arena

Учебный Java AQA проект для самостоятельной практики автоматизации REST API, Web UI, Android Chrome и iOS Safari.

## Технологии

- Java 23
- Spring Boot 4.1.1
- Maven
- JUnit 5
- REST Assured 6.2.0
- Selenium 4.52.0
- Appium Java Client 11.0.0
- Appium 3.7.0
- UiAutomator2
- XCUITest
- HTML, CSS, JavaScript, SVG
- GitHub Actions

## Архитектура

- Model: Character.java — модель персонажа.
- Controller: CharacterController.java — REST API.
- Service: CharacterService.java — бизнес-логика.
- Данные хранятся в LinkedHashMap в памяти и сбрасываются при перезапуске.
- Frontend находится в web/ и публикуется через Spring Boot.
- API и UI автотесты находятся в api/src/test/java/com/javarpgarena/.
- Web UI тесты используют Selenium WebDriver.
- Android UI тесты используют Appium и UiAutomator2.
- iOS Safari UI тесты используют Appium и XCUITest.

## Структура проекта

```text
Java-GameWebArena/
├── .github/
│   ├── scripts/android-ui-tests.sh
│   └── workflows/maven-tests.yml
├── api/
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/javarpgarena/
│       │   ├── controller/CharacterController.java
│       │   ├── model/Character.java
│       │   └── service/CharacterService.java
│       └── test/java/com/javarpgarena/
│           ├── controller/
│           ├── service/
│           └── ui/
│               ├── CharacterUiTestBase.java
│               ├── ChromeCharacterUiTest.java
│               ├── FirefoxCharacterUiTest.java
│               ├── SafariCharacterUiTest.java
│               ├── android/
│               │   ├── AndroidUiTestBase.java
│               │   └── AndroidCharacterUiTest.java
│               ├── ios/
│               │   ├── IosUiTestBase.java
│               │   └── IosCharacterUiTest.java
├── web/
│   ├── assets/characters/
│   ├── css/style.css
│   ├── js/app.js
│   └── index.html
└── README.md
```

## REST API

Базовый адрес: http://localhost:8080/api/characters

- GET — получение персонажей.
- POST — создание персонажа.
- PUT — изменение персонажа.
- DELETE — удаление персонажа.
- 400 — ошибка валидации.
- 404 — персонаж не найден.

## Web UI

Адрес: http://localhost:8080

Браузеры: Chrome, Firefox, Safari.

## Матрица автотестов

| Направление | Сценарии | Выполнения | Статус |
|---|---:|---:|---|
| API CRUD | 14 | 14 | Готово |
| API negative | 10 | 10 | Готово |
| Web functional | 10 | 30 | Готово |
| Web negative | 5 | 15 | Готово |
| Android Chrome functional | 10 | 10 | Готово |
| Android Chrome negative | 5 | 5 | Готово |
| iOS Safari functional | 10 | 10 | Готово |
| iOS Safari negative | 5 | 5 | Готово |
| Integration и additional | 5 | 5 | Готово |
| Итого | 74 | 104 | Готово |

Все 104 выполнения подтверждены локальными проверками соответствующих этапов. iOS Safari: 15 тестов, Failures 0, Errors 0, Skipped 0.

## Локальный запуск

Требования: JDK 23 и Maven.

Запуск приложения:

```bash
cd api
mvn -B spring-boot:run
```

Web UI: http://localhost:8080

API: http://localhost:8080/api/characters

Запуск всех Maven тестов:

```bash
cd api
mvn -B clean test
```

## Android Chrome

Требуются Android SDK, Android Emulator, Node.js, Appium 3 и UiAutomator2.

Установка Appium и драйвера:

```bash
npm install --global appium@3
appium driver install uiautomator2
```

Запуск приложения в первом терминале:

```bash
cd api
mvn -B spring-boot:run
```

Запуск Appium во втором терминале:

```bash
appium --allow-insecure=uiautomator2:chromedriver
```

Запуск Android Chrome тестов:

```bash
cd api
mvn -B -Dtest=AndroidCharacterUiTest test
```

Для Android Emulator адрес приложения: http://10.0.2.2:8080

## iOS Safari

Требуются macOS, Xcode, iOS Simulator, Node.js, Appium 3 и XCUITest driver.

Для локальной проверки использовались iPhone 16 Simulator и iOS 18.2.

Установка Appium и XCUITest:

```bash
npm install --global appium@3.7.0
appium driver install xcuitest@12.12.4
```

Запуск iPhone Simulator выполняется через Xcode или команду xcrun simctl.

Запуск приложения в первом терминале:

```bash
cd api
mvn -B spring-boot:run
```

Запуск Appium во втором терминале:

```bash
appium --address 127.0.0.1 --port 4723
```

Запуск iOS Safari тестов в третьем терминале:

```bash
cd api
IOS_DEVICE_NAME="iPhone 16" IOS_PLATFORM_VERSION="18.2" mvn -B -Dtest=IosCharacterUiTest test
```

Поддерживаемые переменные окружения:

- IOS_DEVICE_NAME — название iOS Simulator.
- IOS_PLATFORM_VERSION — версия iOS Simulator.
- IOS_UDID — идентификатор конкретного Simulator, необязательный параметр.
- APPIUM_URL — адрес Appium Server, по умолчанию http://127.0.0.1:4723.
- BASE_URL — адрес приложения, по умолчанию http://localhost:8080.

Тесты используют мобильный Safari и выполняют очистку созданных тестовых персонажей через DELETE API.

## CI/CD

Workflow: .github/workflows/maven-tests.yml

GitHub Actions содержит следующие jobs:

1. API, unit и integration — Ubuntu.
2. Web Chrome и Firefox — Ubuntu, отдельные matrix-запуски.
3. Web Safari — macOS.
4. Android Chrome — Ubuntu и Android Emulator.
5. iOS Safari — macOS 26, Xcode 26.6 и iPhone 17 Simulator с iOS 26.5.

iOS job устанавливает Appium 3.7.0 и XCUITest driver 12.12.4, запускает Simulator, Appium Server и Spring Boot, затем выполняет IosCharacterUiTest.

При ошибках Android и iOS jobs сохраняют диагностические логи и Maven Surefire reports как GitHub Actions artifacts.

## Git workflow

Работа выполняется в feature-ветках от актуального master. После завершения этапа запускаются проверки, обновляется документация, создаётся commit, выполняется push и открывается Pull Request. После успешного CI выполняется Merge в master.

## Публичный доступ и хостинг

Постоянный публичный хостинг пока не настроен. Для локальной разработки приложение запускается на http://localhost:8080.

Временные Cloudflare Quick Tunnel адреса не являются постоянным хостингом и не гарантируют доступность приложения после остановки локального компьютера или tunnel.

## Текущий статус проекта

Реализованы API, Web UI, Android Chrome и iOS Safari автотесты. Всего 74 уникальных сценария и 104 выполнения с учётом трёх браузеров для Web UI.

iOS CI runner: macOS 26 Intel (macos-26-intel), Xcode 26.6, iPhone 17 Simulator with iOS 26.5.
