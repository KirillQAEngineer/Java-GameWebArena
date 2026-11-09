package com.javarpgarena.controller;

// Импортируем аннотацию для запуска теста через JUnit 5.
import org.junit.jupiter.api.Test;

// Импортируем аннотацию для запуска полноценного Spring Boot приложения.
import org.springframework.boot.test.context.SpringBootTest;

// Импортируем аннотацию для получения случайного порта запущенного сервера.
import org.springframework.boot.test.web.server.LocalServerPort;

// Импортируем given() для подготовки HTTP-запроса.
import static io.restassured.RestAssured.given;

// Импортируем equalTo() для сравнения ожидаемых и фактических значений.
import static org.hamcrest.Matchers.equalTo;

// Запускаем Spring Boot на случайном свободном порту перед тестом.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CharacterApiTest {

    // Spring автоматически передаёт номер порта запущенного сервера.
    @LocalServerPort
    private int port;

    // JUnit 5 распознаёт этот метод как отдельный тест.
    @Test
    void shouldReturnCharacter() {

        // Подготавливаем HTTP-запрос и указываем порт приложения.
        given()
            .port(port)

        // Отправляем GET-запрос за персонажем с ID 1.
        .when()
            .get("/api/characters/1")

        // Начинаем проверять полученный HTTP-ответ.
        .then()

            // Проверяем, что сервер вернул HTTP 200 OK.
            .statusCode(200)

            // Проверяем, что имя персонажа равно Warrior.
            .body("name", equalTo("Warrior"))

            // Проверяем, что здоровье персонажа равно 100.
            .body("health", equalTo(100))

            // Проверяем, что сила атаки равна 20.
            .body("attack", equalTo(20));
    }
}
