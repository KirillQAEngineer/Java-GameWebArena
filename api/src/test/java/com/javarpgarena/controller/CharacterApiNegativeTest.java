package com.javarpgarena.controller;

// Импортируем модель персонажа и сервис.
import com.javarpgarena.model.Character;
import com.javarpgarena.service.CharacterService;

// Импортируем инструменты JUnit 5.
import org.junit.jupiter.api.Test;

// Импортируем инструменты Spring MVC для тестирования HTTP.
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

// Импортируем Mockito для настройки и проверки вызовов сервиса.
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

// Импортируем методы формирования HTTP-запросов.
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

// Импортируем проверки HTTP-ответов.
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Загружаем только веб-слой приложения.
@WebMvcTest(CharacterController.class)
class CharacterApiNegativeTest {

    // Инструмент для отправки HTTP-запросов без запуска сервера.
    @Autowired
    private MockMvc mockMvc;

    // Создаём имитацию сервиса, чтобы контролировать его вызовы.
    @MockitoBean
    private CharacterService characterService;

    // Проверяем POST без обязательного имени.
    @Test
    void shouldRejectCharacterWithoutName() throws Exception {
        mockMvc.perform(post("/api/characters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "health": 100,
                                    "attack": 20,
                                    "experience": 0,
                                    "level": 1
                                }
                                """))
                .andExpect(status().isBadRequest());

        // Проверяем, что невалидный объект не попал в сервис.
        verify(characterService, never()).createCharacter(any(Character.class));
    }

    // Проверяем POST с пустым именем.
    @Test
    void shouldRejectCharacterWithBlankName() throws Exception {
        mockMvc.perform(post("/api/characters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "   ",
                                    "health": 100,
                                    "attack": 20,
                                    "experience": 0,
                                    "level": 1
                                }
                                """))
                .andExpect(status().isBadRequest());

        // Проверяем, что создание персонажа не запускалось.
        verify(characterService, never()).createCharacter(any(Character.class));
    }

    // Проверяем POST с нулевым здоровьем.
    @Test
    void shouldRejectCharacterWithZeroHealth() throws Exception {
        mockMvc.perform(post("/api/characters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Warrior",
                                    "health": 0,
                                    "attack": 20,
                                    "experience": 0,
                                    "level": 1
                                }
                                """))
                .andExpect(status().isBadRequest());

        // Проверяем, что невалидный персонаж не создавался.
        verify(characterService, never()).createCharacter(any(Character.class));
    }

    // Проверяем POST с отрицательной атакой.
    @Test
    void shouldRejectCharacterWithNegativeAttack() throws Exception {
        mockMvc.perform(post("/api/characters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Warrior",
                                    "health": 100,
                                    "attack": -1,
                                    "experience": 0,
                                    "level": 1
                                }
                                """))
                .andExpect(status().isBadRequest());

        // Проверяем, что сервис не получил запрос на создание.
        verify(characterService, never()).createCharacter(any(Character.class));
    }

    // Проверяем POST с отрицательным опытом.
    @Test
    void shouldRejectCharacterWithNegativeExperience() throws Exception {
        mockMvc.perform(post("/api/characters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Warrior",
                                    "health": 100,
                                    "attack": 20,
                                    "experience": -1,
                                    "level": 1
                                }
                                """))
                .andExpect(status().isBadRequest());

        // Проверяем, что персонаж не был передан в сервис.
        verify(characterService, never()).createCharacter(any(Character.class));
    }

    // Проверяем POST с нулевым уровнем.
    @Test
    void shouldRejectCharacterWithZeroLevel() throws Exception {
        mockMvc.perform(post("/api/characters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Warrior",
                                    "health": 100,
                                    "attack": 20,
                                    "experience": 0,
                                    "level": 0
                                }
                                """))
                .andExpect(status().isBadRequest());

        // Проверяем, что создание не было вызвано.
        verify(characterService, never()).createCharacter(any(Character.class));
    }

    // Проверяем PUT с отрицательным здоровьем.
    @Test
    void shouldRejectUpdateWithNegativeHealth() throws Exception {
        mockMvc.perform(put("/api/characters/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Warrior",
                                    "health": -10,
                                    "attack": 20,
                                    "experience": 0,
                                    "level": 1
                                }
                                """))
                .andExpect(status().isBadRequest());

        // Проверяем, что обновление не дошло до сервиса.
        verify(characterService, never())
                .updateCharacter(eq(1L), any(Character.class));
    }

    // Проверяем POST с синтаксически некорректным JSON.
    @Test
    void shouldRejectMalformedJson() throws Exception {
        mockMvc.perform(post("/api/characters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json"))
                .andExpect(status().isBadRequest());

        // Проверяем, что сервис не пытался создать персонажа.
        verify(characterService, never()).createCharacter(any(Character.class));
    }

    // Проверяем GET для несуществующего персонажа.
    @Test
    void shouldReturnNotFoundForMissingCharacter() throws Exception {
        // Настраиваем сервис: персонаж с ID 999 не существует.
        given(characterService.getCharacterById(999L)).willReturn(null);

        mockMvc.perform(get("/api/characters/999"))
                .andExpect(status().isNotFound());

        // Проверяем, что контроллер обратился к сервису с нужным ID.
        verify(characterService).getCharacterById(999L);
    }

    // Проверяем DELETE для несуществующего персонажа.
    @Test
    void shouldReturnNotFoundWhenDeletingMissingCharacter() throws Exception {
        // Настраиваем сервис: удалить несуществующего персонажа невозможно.
        given(characterService.deleteCharacter(999L)).willReturn(false);

        mockMvc.perform(delete("/api/characters/999"))
                .andExpect(status().isNotFound());

        // Проверяем, что контроллер передал сервису нужный ID.
        verify(characterService).deleteCharacter(999L);
    }
}
