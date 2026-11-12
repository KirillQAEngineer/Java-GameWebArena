package com.javarpgarena.controller;

// Импортируем JUnit 5 для запуска тестов.
import org.junit.jupiter.api.Test;

// Запускаем полноценное Spring Boot приложение.
import org.springframework.boot.test.context.SpringBootTest;

// Получаем случайный порт запущенного HTTP-сервера.
import org.springframework.boot.test.web.server.LocalServerPort;

// Перезапускаем контекст после каждого теста.
import org.springframework.test.annotation.DirtiesContext;

// Импортируем Rest Assured для реальных HTTP-запросов.
import static io.restassured.RestAssured.given;

// Импортируем проверки ожидаемых значений.
import static org.hamcrest.Matchers.equalTo;

// Запускаем приложение на случайном свободном порту.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

// После каждого теста создаём новый контекст Spring.
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CharacterApiIntegrationTest {

    // Spring автоматически передаёт номер порта.
    @LocalServerPort
    private int port;

    // Проверяем создание персонажа и его последующее получение.
    @Test
    void shouldCreateCharacterAndRetrieveIt() {

        // Отправляем POST-запрос для создания персонажа.
        Number characterId = given()
                // Указываем порт запущенного приложения.
                .port(port)

                // Указываем формат отправляемых данных.
                .contentType("application/json")

                // Передаём характеристики нового персонажа.
                .body("""
                        {
                            "name": "Integration Hero",
                            "health": 120,
                            "attack": 30,
                            "experience": 50,
                            "level": 2
                        }
                        """)

                // Выполняем POST-запрос.
                .when()
                .post("/api/characters")

                // Проверяем успешное создание.
                .then()
                .statusCode(201)

                // Получаем ID созданного персонажа из ответа.
                .extract()
                .path("id");

        // Отправляем GET-запрос за созданным персонажем.
        given()
                // Используем тот же порт приложения.
                .port(port)

                // Выполняем GET-запрос с полученным ID.
                .when()
                .get("/api/characters/" + characterId)

                // Начинаем проверять HTTP-ответ.
                .then()

                // Проверяем статус 200 OK.
                .statusCode(200)

                // Проверяем имя персонажа.
                .body("name", equalTo("Integration Hero"))

                // Проверяем здоровье.
                .body("health", equalTo(120))

                // Проверяем силу атаки.
                .body("attack", equalTo(30))

                // Проверяем опыт.
                .body("experience", equalTo(50))

                // Проверяем уровень.
                .body("level", equalTo(2));
    }

    // Проверяем обновление персонажа и сохранение новых данных.
    @Test
    void shouldUpdateCharacterAndRetrieveUpdatedData() {

        // Отправляем PUT-запрос для обновления Warrior с ID 1.
        given()
                .port(port)
                .contentType("application/json")
                .body("""
                        {
                            "name": "Super Warrior",
                            "health": 150,
                            "attack": 45,
                            "experience": 200,
                            "level": 3
                        }
                        """)
                .when()
                .put("/api/characters/1")
                .then()
                // Проверяем успешное обновление.
                .statusCode(200)
                .body("name", equalTo("Super Warrior"))
                .body("health", equalTo(150))
                .body("attack", equalTo(45))
                .body("experience", equalTo(200))
                .body("level", equalTo(3));

        // Повторно запрашиваем персонажа через GET.
        given()
                .port(port)
                .when()
                .get("/api/characters/1")
                .then()
                // Проверяем, что обновлённые данные сохранились.
                .statusCode(200)
                .body("name", equalTo("Super Warrior"))
                .body("health", equalTo(150))
                .body("attack", equalTo(45))
                .body("experience", equalTo(200))
                .body("level", equalTo(3));
    }

    // Проверяем удаление персонажа и его последующую недоступность.
    @Test
    void shouldDeleteCharacterAndReturnNotFound() {

        // Отправляем DELETE-запрос для начального персонажа Warrior.
        given()
                .port(port)
                .when()
                .delete("/api/characters/1")
                .then()
                // Проверяем успешное удаление без тела ответа.
                .statusCode(204);

        // Проверяем, что удалённый персонаж больше не доступен.
        given()
                .port(port)
                .when()
                .get("/api/characters/1")
                .then()
                // Ожидаем HTTP 404 Not Found.
                .statusCode(404);
    }

    // Проверяем, что обновление одного персонажа отражается в общем списке.
    @Test
    void shouldReflectCharacterUpdateInAllCharactersList() {

        // Обновляем характеристики Warrior с ID 1.
        given()
                .port(port)
                .contentType("application/json")
                .body("""
                        {
                            "name": "Updated Warrior",
                            "health": 200,
                            "attack": 60,
                            "experience": 300,
                            "level": 4
                        }
                        """)
                .when()
                .put("/api/characters/1")
                .then()
                .statusCode(200);

        // Получаем полный список персонажей.
        given()
                .port(port)
                .when()
                .get("/api/characters")
                .then()
                // Проверяем успешный ответ.
                .statusCode(200)

                // Проверяем, что количество персонажей осталось равным двум.
                .body("size()", equalTo(2))

                // Проверяем обновлённые данные Warrior.
                .body("[0].id", equalTo(1))
                .body("[0].name", equalTo("Updated Warrior"))
                .body("[0].health", equalTo(200))
                .body("[0].attack", equalTo(60))
                .body("[0].experience", equalTo(300))
                .body("[0].level", equalTo(4))

                // Проверяем, что Mage не изменился.
                .body("[1].id", equalTo(2))
                .body("[1].name", equalTo("Mage"))
                .body("[1].health", equalTo(70))
                .body("[1].attack", equalTo(35));
    }

    // Проверяем уникальность ID и независимость данных персонажей.
    @Test
    void shouldCreateCharactersWithUniqueIdsAndIndependentData() {

        // Создаём первого персонажа Archer и получаем его ID.
        Number archerId = given()
                .port(port)
                .contentType("application/json")
                .body("""
                        {
                            "name": "Archer",
                            "health": 80,
                            "attack": 25,
                            "experience": 10,
                            "level": 1
                        }
                        """)
                .when()
                .post("/api/characters")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        // Создаём второго персонажа Paladin и получаем его ID.
        Number paladinId = given()
                .port(port)
                .contentType("application/json")
                .body("""
                        {
                            "name": "Paladin",
                            "health": 180,
                            "attack": 40,
                            "experience": 50,
                            "level": 3
                        }
                        """)
                .when()
                .post("/api/characters")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        // Проверяем, что ID двух персонажей различаются.
        org.junit.jupiter.api.Assertions.assertNotEquals(archerId, paladinId);

        // Получаем Archer и проверяем его собственные характеристики.
        given()
                .port(port)
                .when()
                .get("/api/characters/" + archerId)
                .then()
                .statusCode(200)
                .body("name", equalTo("Archer"))
                .body("health", equalTo(80))
                .body("attack", equalTo(25));

        // Получаем Paladin и проверяем его собственные характеристики.
        given()
                .port(port)
                .when()
                .get("/api/characters/" + paladinId)
                .then()
                .statusCode(200)
                .body("name", equalTo("Paladin"))
                .body("health", equalTo(180))
                .body("attack", equalTo(40));
    }
}
