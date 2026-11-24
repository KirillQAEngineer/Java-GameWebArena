# Java Game Web Arena

Учебный Java AQA проект для автоматизации REST API, Web UI, Android Chrome и iOS Safari.

## Технологии

- Java 23
- Spring Boot 4.1.1
- Maven
- JUnit 5
- REST Assured 6.2.0
- Selenium 4.52.0
- Appium Java Client 11.0.0
- Appium 3
- UiAutomator2
- HTML, CSS, JavaScript, SVG
- GitHub Actions

## Архитектура

- Model: Character.java — модель персонажа.
- Controller: CharacterController.java — REST API.
- Service: CharacterService.java — бизнес-логика.
- Данные хранятся в LinkedHashMap в памяти и сбрасываются при перезапуске.
- Frontend находится в web/ и публикуется через Spring Boot.

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
| iOS Safari functional | 10 | 10 | Запланировано |
| iOS Safari negative | 5 | 5 | Запланировано |
| Integration и additional | 5 | 5 | Готово |
| Итого | 74 | 104 | |

Текущий подтверждённый результат: 89 выполнений — 29 API/unit/integration, 45 Web UI, 15 Android Chrome UI.

## Локальный запуск

Требования: JDK 23 и Maven.

```bash
cd api
mvn -B spring-boot:run
```

Web UI: http://localhost:8080

API: http://localhost:8080/api/characters

Запуск тестов:

```bash
cd api
mvn -B clean test
```

## Android Chrome

Требуются Android SDK, Android Emulator, Node.js, Appium 3 и UiAutomator2.

Запуск приложения:

```bash
cd api
mvn -B spring-boot:run
```

В другом терминале:

```bash
appium --allow-insecure=uiautomator2:chromedriver
```

Запуск Android Chrome тестов:

```bash
cd api
mvn -B -Dtest=AndroidCharacterUiTest test
```

Для Android Emulator адрес приложения: http://10.0.2.2:8080

## CI/CD

Workflow: .github/workflows/maven-tests.yml

Текущие jobs:

1. API, unit и integration — Ubuntu.
2. Web Chrome и Firefox — Ubuntu.
3. Web Safari — macOS.
4. Android Chrome — Ubuntu и Android Emulator.

Будущий job для iOS должен называться iOS Safari UI Tests и выполняться на macOS runner с Xcode Simulator, Appium и XCUITest driver.

Android job должен явно называться Android Chrome UI Tests.

При ошибке Android job сохраняются логи Appium, Android Logcat, приложения и Maven Surefire reports.

## Git workflow

Работа выполняется в feature-ветках от актуального master. После завершения этапа запускаются проверки, обновляется документация, создаётся commit, выполняется push и открывается Pull Request. После успешного CI выполняется Merge в master.

## Публичный адрес

https://assessment-moment-bargain-extension.trycloudflare.com

Это временный Cloudflare Quick Tunnel, а не постоянный хостинг. Для доступности MacBook, Spring Boot и tunnel должны быть запущены. Приложение использует 127.0.0.1:8080.

## Следующий этап

- Реализовать 10 функциональных iOS Safari тестов.
- Реализовать 5 негативных iOS Safari тестов.
- Добавить iOS Safari job в GitHub Actions на macOS.
