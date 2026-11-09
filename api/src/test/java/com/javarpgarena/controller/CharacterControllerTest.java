package com.javarpgarena.controller;

import com.javarpgarena.model.Character;
import com.javarpgarena.service.CharacterService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Загружаем только веб-слой приложения.
@WebMvcTest(CharacterController.class)
class CharacterControllerTest {

    // Инструмент для отправки HTTP-запросов без запуска сервера.
    @Autowired
    private MockMvc mockMvc;

    // Имитация сервиса для изолированного тестирования контроллера.
    @MockitoBean
    private CharacterService characterService;

    // Проверяем получение одного персонажа.
    @Test
    void shouldReturnCharacterById() throws Exception {
        Character character = new Character(1L, "Warrior", 100, 20, 0, 1);

        given(characterService.getCharacterById(1L)).willReturn(character);

        mockMvc.perform(get("/api/characters/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Warrior"))
                .andExpect(jsonPath("$.health").value(100))
                .andExpect(jsonPath("$.attack").value(20))
                .andExpect(jsonPath("$.experience").value(0))
                .andExpect(jsonPath("$.level").value(1));
    }

    // Проверяем получение списка персонажей.
    @Test
    void shouldReturnAllCharacters() throws Exception {
        List<Character> characters = List.of(
                new Character(1L, "Warrior", 100, 20, 0, 1),
                new Character(2L, "Mage", 70, 35, 0, 1)
        );

        given(characterService.getAllCharacters()).willReturn(characters);

        mockMvc.perform(get("/api/characters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Warrior"))
                .andExpect(jsonPath("$[1].name").value("Mage"));
    }

    // Проверяем создание нового персонажа.
    @Test
    void shouldCreateCharacter() throws Exception {
        Character createdCharacter = new Character(3L, "Archer", 80, 25, 0, 1);

        given(characterService.createCharacter(any(Character.class)))
                .willReturn(createdCharacter);

        mockMvc.perform(post("/api/characters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Archer",
                                    "health": 80,
                                    "attack": 25,
                                    "experience": 0,
                                    "level": 1
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("Archer"));
    }

    // Проверяем обновление существующего персонажа.
    @Test
    void shouldUpdateCharacter() throws Exception {
        Character updatedCharacter = new Character(1L, "Super Warrior", 150, 40, 100, 2);

        given(characterService.updateCharacter(eq(1L), any(Character.class)))
                .willReturn(updatedCharacter);

        mockMvc.perform(put("/api/characters/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Super Warrior",
                                    "health": 150,
                                    "attack": 40,
                                    "experience": 100,
                                    "level": 2
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Super Warrior"))
                .andExpect(jsonPath("$.health").value(150))
                .andExpect(jsonPath("$.attack").value(40))
                .andExpect(jsonPath("$.experience").value(100))
                .andExpect(jsonPath("$.level").value(2));
    }

    // Проверяем удаление существующего персонажа.
    @Test
    void shouldDeleteCharacter() throws Exception {
        given(characterService.deleteCharacter(1L)).willReturn(true);

        mockMvc.perform(delete("/api/characters/1"))
                .andExpect(status().isNoContent());

        verify(characterService).deleteCharacter(1L);
    }
}
