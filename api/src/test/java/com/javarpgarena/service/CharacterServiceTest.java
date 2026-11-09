package com.javarpgarena.service;

// Импортируем модель персонажа.
import com.javarpgarena.model.Character;

// Импортируем тестируемый сервис.
import com.javarpgarena.service.CharacterService;

// Импортируем JUnit 5.
import org.junit.jupiter.api.Test;

// Импортируем проверки результатов тестов.
import static org.junit.jupiter.api.Assertions.*;

// Тестируем бизнес-логику CharacterService.
class CharacterServiceTest {

    // Проверяем получение всех персонажей.
    @Test
    void shouldReturnAllCharacters() {
        CharacterService service = new CharacterService();

        var characters = service.getAllCharacters();

        assertEquals(2, characters.size());
        assertEquals("Warrior", characters.get(0).getName());
        assertEquals("Mage", characters.get(1).getName());
    }

    // Проверяем поиск существующего персонажа.
    @Test
    void shouldReturnCharacterById() {
        CharacterService service = new CharacterService();

        Character character = service.getCharacterById(1L);

        assertNotNull(character);
        assertEquals("Warrior", character.getName());
    }

    // Проверяем поиск персонажа с несуществующим ID.
    @Test
    void shouldReturnNullForUnknownId() {
        CharacterService service = new CharacterService();

        Character character = service.getCharacterById(999L);

        assertNull(character);
    }

    // Проверяем создание нового персонажа.
    @Test
    void shouldCreateCharacter() {
        CharacterService service = new CharacterService();

        Character newCharacter = new Character(null, "Archer", 80, 25, 0, 1);

        Character createdCharacter = service.createCharacter(newCharacter);

        assertNotNull(createdCharacter);
        assertEquals(3L, createdCharacter.getId());
        assertEquals("Archer", createdCharacter.getName());
        assertEquals(3, service.getAllCharacters().size());
    }

    // Проверяем обновление существующего персонажа.
    @Test
    void shouldUpdateCharacter() {
        CharacterService service = new CharacterService();

        Character updatedCharacter = new Character(
                1L, "Super Warrior", 150, 40, 100, 2
        );

        Character result = service.updateCharacter(1L, updatedCharacter);

        assertNotNull(result);
        assertEquals("Super Warrior", result.getName());
        assertEquals(150, result.getHealth());
        assertEquals(40, result.getAttack());
        assertEquals(100, result.getExperience());
        assertEquals(2, result.getLevel());
    }

    // Проверяем попытку обновить несуществующего персонажа.
    @Test
    void shouldReturnNullWhenUpdatingUnknownCharacter() {
        CharacterService service = new CharacterService();

        Character updatedCharacter = new Character(
                999L, "Unknown", 100, 20, 0, 1
        );

        Character result = service.updateCharacter(999L, updatedCharacter);

        assertNull(result);
    }

    // Проверяем удаление существующего персонажа.
    @Test
    void shouldDeleteCharacter() {
        CharacterService service = new CharacterService();

        boolean deleted = service.deleteCharacter(1L);

        assertTrue(deleted);
        assertNull(service.getCharacterById(1L));
        assertEquals(1, service.getAllCharacters().size());
    }

    // Проверяем попытку удалить несуществующего персонажа.
    @Test
    void shouldReturnFalseWhenDeletingUnknownCharacter() {
        CharacterService service = new CharacterService();

        boolean deleted = service.deleteCharacter(999L);

        assertFalse(deleted);
        assertEquals(2, service.getAllCharacters().size());
    }
}
