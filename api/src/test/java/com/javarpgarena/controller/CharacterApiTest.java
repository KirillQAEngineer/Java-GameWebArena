
package com.javarpgarena.controller;

// Импортируем аннотацию для запуска теста через JUnit 5
import org.junit.jupiter.api.Test;

// Импортируем given() для подготовки HTTP-запроса
import static io.restassured.RestAssured.given;

// Импортируем equalTo() для сравнения ожидаемого и фактического значений
import static org.hamcrest.Matchers.equalTo;

// Класс с API-автотестами персонажа
class CharacterApiTest {

    // JUnit 5 распознаёт этот метод как отдельный тест
    @Test
    void shouldReturnCharacter() {

        // Подготавливаем HTTP-запрос
        given()

        // Указываем, что отправляем GET-запрос
        .when()
            .get("http://localhost:8080/api/character")

        // Начинаем проверять полученный HTTP-ответ
        .then()

            // Проверяем, что сервер вернул HTTP 200 OK
            .statusCode(200)

            // Проверяем, что поле name в JSON равно "Warrior"
            .body("name", equalTo("Warrior"))

            // Проверяем, что поле health равно 100
            .body("health", equalTo(100))

            // Проверяем, что поле attack равно 20
            .body("attack", equalTo(20));
    }
}
