# Java-GameWebArena

# ПУБЛИЧНЫЙ ДОСТУП К ПРОЕКТУ

## Демонстрационная ссылка

https://assessment-moment-bargain-extension.trycloudflare.com

Бесплатный Cloudflare Quick Tunnel. Ссылка временная и может измениться после перезапуска туннеля. При изменении адреса обновите документацию.

## Запуск публичного доступа

Терминал 1 — Spring Boot:

```bash
cd ~/Projects/Java-GameWebArena/api
mvn spring-boot:run
```

Терминал 2 — Cloudflare Tunnel:

```bash
cloudflared tunnel --url http://127.0.0.1:8080
```

Оба процесса должны оставаться запущенными. Используйте актуальную HTTPS-ссылку из вывода Cloudflare.

## Остановка

Нажмите Ctrl+C в обоих терминалах.

## Ограничения и безопасность

MacBook должен оставаться включённым и подключённым к интернету. При остановке приложения, туннеля или переходе компьютера в сон сайт станет недоступен.

Quick Tunnel бесплатный, временный и не гарантирует круглосуточную доступность.

Приложение слушает только 127.0.0.1:8080. Открывать порт 8080 на роутере не требуется.

REST API пока не защищён авторизацией. Любой пользователь с публичной ссылкой может читать, создавать, изменять и удалять демонстрационные данные. Не размещайте персональную или конфиденциальную информацию.

Для постоянной публикации потребуются облачный хостинг и защита API.

---


Учебный проект AQA на Java для практики автоматизации Web, Android и iOS.

## 1. Назначение проекта

Java-GameWebArena — игровое приложение с REST API и Web UI для самостоятельной разработки и запуска автотестов.

Основные задачи:
- Практика Java Automation Testing.
- Изучение REST API и HTTP.
- Практика Web UI Automation.
- Подготовка основы для Android и iOS Automation.
- Изучение Maven, Git, GitHub и CI/CD.

Web UI и REST API обслуживаются одним Spring Boot приложением. Отдельный frontend-сервер не требуется.

## 2. Технологический стек

### Backend
- Java 23
- Spring Boot 4.1.1
- Spring MVC
- Bean Validation
- Maven

### Автоматизированное тестирование
- JUnit 5
- Spring Boot Test
- MockMvc
- REST Assured 6.2.0
- Mockito

### Frontend
- HTML5
- CSS3
- JavaScript
- SVG

### Инструменты
- Git и GitHub
- GitHub Actions
- Maven Surefire

## 3. Архитектура проекта

Backend построен по трёхслойной архитектуре:

- Model — Character.java: модель персонажа и валидация.
- Controller — CharacterController.java: обработка HTTP-запросов.
- Service — CharacterService.java: бизнес-логика и управление персонажами.

Данные хранятся в LinkedHashMap в оперативной памяти. После перезапуска приложения изменения теряются.

Frontend:
- web/index.html — структура страницы.
- web/css/style.css — оформление и адаптация.
- web/js/app.js — взаимодействие с REST API.
- web/assets/characters/ — SVG-портреты персонажей.

Папка web подключена к Maven-ресурсам и публикуется как статические ресурсы Spring Boot.

### Схема взаимодействия

Браузер → Spring Boot → CharacterController → CharacterService → Character.

Frontend получает данные через REST API и отображает персонажей.

## 4. Структура проекта

```text
Java-GameWebArena/
├── .github/
│   └── workflows/
│       └── maven-tests.yml
├── api/
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   │   ├── Main.java
│       │   │   └── com/javarpgarena/
│       │   │       ├── JavaRpgArenaApplication.java
│       │   │       ├── controller/
│       │   │       │   └── CharacterController.java
│       │   │       ├── model/
│       │   │       │   └── Character.java
│       │   │       └── service/
│       │   │           └── CharacterService.java
│       │   └── resources/
│       └── test/
│           └── java/com/javarpgarena/
│               ├── controller/
│               │   ├── CharacterApiTest.java
│               │   ├── CharacterApiNegativeTest.java
│               │   ├── CharacterApiIntegrationTest.java
│               │   └── CharacterControllerTest.java
│               └── service/
│                   └── CharacterServiceTest.java
├── web/
│   ├── index.html
│   ├── css/
│   │   └── style.css
│   ├── js/
│   │   └── app.js
│   └── assets/
│       └── characters/
│           ├── adventurer.svg
│           ├── mage.svg
│           └── warrior.svg
└── README.md
```

## 5. REST API

Базовый адрес: `http://localhost:8080/api/characters`

| Метод | Endpoint | Назначение | Ответ |
|---|---|---|---|
| GET | `/api/characters` | Получить всех персонажей | 200 |
| GET | `/api/characters/{id}` | Получить персонажа | 200 |
| POST | `/api/characters` | Создать персонажа | 201 |
| PUT | `/api/characters/{id}` | Обновить персонажа | 200 |
| DELETE | `/api/characters/{id}` | Удалить персонажа | 204 |

Дополнительные ответы:
- 400 Bad Request — ошибка валидации.
- 404 Not Found — персонаж не найден.

### Начальные данные

| ID | Имя | Здоровье | Атака | Опыт | Уровень |
|---:|---|---:|---:|---:|---:|
| 1 | Warrior | 100 | 20 | 0 | 1 |
| 2 | Mage | 70 | 35 | 0 | 1 |

Валидация модели:
- name — не должен быть пустым.
- health — минимум 1.
- attack — минимум 0.
- experience — минимум 0.
- level — минимум 1.

## 6. Web UI

Интерфейс выполнен в стилистике Pixel Art RPG.

Функциональность:
- Просмотр персонажей.
- Создание персонажа.
- Редактирование характеристик.
- Удаление персонажа.
- Обновление списка.
- Отображение HP, ATK, EXP и LVL.
- Полосы здоровья и опыта.

### Селекторы для UI Automation

| Элемент | data-testid |
|---|---|
| Список персонажей | characters-list |
| Карточка персонажа | character-card |
| Кнопка обновления | refresh-button |
| Форма | character-form |
| Поле имени | name-input |
| Поле здоровья | health-input |
| Поле атаки | attack-input |
| Поле опыта | experience-input |
| Поле уровня | level-input |
| Кнопка сохранения | submit-button |
| Кнопка отмены | cancel-button |
| Кнопка редактирования | edit-button |
| Кнопка удаления | delete-button |
| Уведомления | notification |

Selenium и мобильные тестовые фреймворки пока не подключены.

## 7. План автоматизированного тестирования

Целевой ориентир проекта — 80 автотестов.

| Категория | Количество | Статус |
|---|---:|---|
| API CRUD | 14 | Готово |
| API негативные сценарии и валидация | 10 | Готово |
| UI функциональные | 10 | Самостоятельная разработка |
| UI Android негативные | 5 | Самостоятельная разработка |
| UI Android функциональные | 10 | Самостоятельная разработка |
| UI iOS негативные | 5 | Самостоятельная разработка |
| UI iOS функциональные | 10 | Самостоятельная разработка |
| UI негативные | 5 | Самостоятельная разработка |
| Интеграционные и дополнительные | 5 | Готово |

Сумма перечисленных категорий составляет 74 теста. Для достижения цели в 80 тестов необходимо дополнительно определить ещё 6 сценариев.

Текущий результат:
- 29 тестов успешно проходят: API, модульные, контроллерные и интеграционные проверки.
- Интеграционные сценарии проверяют совместную работу HTTP API, контроллера и сервиса.
- Failures: 0.
- Errors: 0.
- Skipped: 0.
- Web UI проверен вручную.

## 8. Локальный запуск

Требования:
- Java 23.
- Maven.
- Git.

### Проверка версий

```bash
java -version
mvn -version
git --version
```

### Запуск приложения

Из корня проекта:

```bash
cd api
mvn spring-boot:run
```

Приложение будет доступно по адресам:

- Web UI: http://localhost:8080/
- REST API: http://localhost:8080/api/characters

Для остановки приложения нажмите Ctrl+C в терминале.

### Проверка API

```bash
curl http://localhost:8080/api/characters
```

### Проверка Web UI

Откройте в браузере:

http://localhost:8080/

### Запуск тестов

Из корня проекта:

```bash
cd api
mvn clean test
```

- clean удаляет предыдущие результаты сборки.
- test компилирует и запускает автоматизированные тесты.
- Maven Surefire формирует отчёты в target/surefire-reports/.

## 9. CI/CD — GitHub Actions

Workflow расположен в:

`.github/workflows/maven-tests.yml`

Он запускается при:
- Push в ветки test и master.
- Создании или обновлении Pull Request в master.

Основные этапы:
1. Получение исходного кода.
2. Установка Java 23 Temurin.
3. Настройка Maven cache.
4. Запуск `mvn -B clean test` в директории api.
5. Проверка результата сборки.

Результаты доступны во вкладке Actions GitHub-репозитория.

## 10. Git workflow

- Основная ветка: master.
- Рабочая ветка: test.
- Разработка ведётся в test.
- Перед публикацией запускаются тесты.
- Документация обновляется вместе с кодом.
- Изменения коммитятся и отправляются в GitHub.
- Pull Request проверяется перед Merge.
- Merge выполняется только после согласования.

Пример публикации изменений:

```bash
git status
git add README.md
git commit -m "Update project documentation"
git push origin test
```

## 11. Дальнейшее развитие

- Самостоятельное написание Web UI тестов.
- Подготовка мобильного UI для Android и iOS.
- Самостоятельное написание мобильных тестов.
- Реализация интеграционных проверок.
- Подключение Selenium.
- Развитие тестовой архитектуры.
- Расширение CI/CD.

---
