# Java-GameWebArena

Учебный проект AQA на Java для практики автоматизированного тестирования Web, Android и iOS.

## 1. Назначение проекта

Проект представляет собой игровое приложение с REST API и минимальным пользовательским интерфейсом. Он предназначен для разработки и запуска автоматизированных тестов, изучения Java Automation Testing и CI/CD.

Цель — не менее 50 автотестов.

## 2. Технологический стек

- Java 23
- Spring Boot 4.1.1
- Spring MVC
- Bean Validation
- Maven
- JUnit 5
- Spring Boot Test
- MockMvc
- REST Assured 6.2.0
- Mockito
- Git и GitHub
- GitHub Actions

## 3. Архитектура

Серверная часть разделена на три основных слоя.

### Model

`Character.java` описывает игрового персонажа: ID, имя, здоровье, силу атаки, опыт и уровень. Для проверки входящих данных используются Bean Validation.

### Controller

`CharacterController.java` принимает HTTP-запросы, вызывает сервисный слой и возвращает HTTP-ответы.

### Service

`CharacterService.java` содержит бизнес-логику создания, чтения, обновления и удаления персонажей.

На текущем этапе персонажи хранятся в `LinkedHashMap` в оперативной памяти. Данные не сохраняются после перезапуска приложения.

### Схема взаимодействия

Клиент или автотест отправляет HTTP-запрос в Controller. Controller вызывает Service. Service работает с Model и возвращает результат контроллеру.

## 4. Структура проекта

```text
Java-GameWebArena/
├── .github/workflows/maven-tests.yml
├── api/
│   ├── pom.xml
│   └── src/
│       ├── main/java/
│       │   ├── Main.java
│       │   └── com/javarpgarena/
│       │       ├── JavaRpgArenaApplication.java
│       │       ├── controller/CharacterController.java
│       │       ├── model/Character.java
│       │       └── service/CharacterService.java
│       └── test/java/com/javarpgarena/
│           ├── controller/
│           │   ├── CharacterApiTest.java
│           │   ├── CharacterApiNegativeTest.java
│           │   └── CharacterControllerTest.java
│           └── service/CharacterServiceTest.java
├── web/
└── README.md

## 5. REST API

Базовый адрес: /api/characters.

| Метод | Endpoint | Назначение | Ответ |
|---|---|---|---|
| GET | /api/characters | Получить всех персонажей | 200 OK |
| GET | /api/characters/{id} | Получить персонажа | 200 OK |
| POST | /api/characters | Создать персонажа | 201 Created |
| PUT | /api/characters/{id} | Обновить персонажа | 200 OK |
| DELETE | /api/characters/{id} | Удалить персонажа | 204 No Content |

Дополнительные ответы:
- 400 Bad Request — некорректные входящие данные.
- 404 Not Found — персонаж не найден.

### Начальные данные

| ID | Имя | Здоровье | Атака | Опыт | Уровень |
|---:|---|---:|---:|---:|---:|
| 1 | Warrior | 100 | 20 | 0 | 1 |
| 2 | Mage | 70 | 35 | 0 | 1 |

## 6. План автоматизированного тестирования

Цель проекта — не менее 50 автотестов.

| Категория | Количество | Статус |
|---|---:|---|
| API CRUD | 14 | Готово |
| API негативные сценарии и валидация | 10 | Готово |
| UI функциональные сценарии | 10 | Запланировано |
| UI негативные сценарии | 5 | Запланировано |
| Интеграционные и дополнительные проверки | 5 | Запланировано |
| **Всего** | **50** | |

## 7. Локальный запуск

Требуются Java 23 и Maven.

Проверка Java: `java -version`

Проверка Maven: `mvn -version`

### Запуск приложения

Из корня проекта:

```bash
cd api
mvn spring-boot:run
```

API будет доступно по адресу: http://localhost:8080/api/characters

### Запуск тестов

Из корня проекта:

```bash
cd api
mvn clean test
```

`clean` удаляет результаты предыдущей сборки, `test` запускает тесты.

Отчёты Maven находятся в `api/target/surefire-reports/`.

## 8. CI/CD — GitHub Actions

Workflow расположен в `.github/workflows/maven-tests.yml`.

Он запускается при push в ветки `test` и `master`, а также при создании или обновлении Pull Request в `master`.

Этапы CI:
1. Получение исходного кода.
2. Установка Java 23 Temurin.
3. Настройка кеша Maven.
4. Выполнение `mvn -B clean test` в директории `api`.
5. Публикация результата в GitHub Actions.

Результаты можно посмотреть во вкладке Actions репозитория.

## 9. Git workflow

Основная ветка — `master`. Рабочая ветка — `test`.

Порядок работы:
1. Разработка в ветке `test`.
2. Локальный запуск тестов.
3. Создание коммита.
4. Push в GitHub.
5. Автоматический запуск CI.
6. Проверка Pull Request.
7. Перенос изменений в `master` после проверки и согласования.

Пример отправки изменений из корня проекта:

```bash
git status
git add .
git commit -m "Describe your changes"
git push origin test
```

## 10. Дальнейшее развитие

- Разработка минимального UI.
- 10 функциональных UI-тестов.
- 5 негативных UI-тестов.
- 5 интеграционных и дополнительных проверок.
- Развитие переиспользуемой тестовой архитектуры.
- Расширение CI/CD.
- Подготовка к Web, Android и iOS автоматизации.

---

Учебный проект для практики AQA и Java Automation Testing.
