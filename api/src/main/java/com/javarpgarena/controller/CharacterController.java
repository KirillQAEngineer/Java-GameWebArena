package com.javarpgarena.controller;

// Импортируем модель персонажа.
import com.javarpgarena.model.Character;

// Импортируем сервис с бизнес-логикой.
import com.javarpgarena.service.CharacterService;

import jakarta.validation.Valid;

// Импортируем инструменты Spring для HTTP-запросов и ответов.
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Импортируем List для списка персонажей.
import java.util.List;

// Объявляем REST-контроллер.
@RestController
@RequestMapping("/api/characters")
public class CharacterController {

    // Сохраняем ссылку на сервис.
    private final CharacterService characterService;

    // Spring передаёт сервис через конструктор.
    public CharacterController(CharacterService characterService) {
        this.characterService = characterService;
    }

    // GET: возвращаем список всех персонажей.
    @GetMapping
    public List<Character> getAllCharacters() {
        return characterService.getAllCharacters();
    }

    // GET: возвращаем персонажа по ID.
    @GetMapping("/{id}")
    public ResponseEntity<Character> getCharacterById(@PathVariable Long id) {
        Character character = characterService.getCharacterById(id);

        // Если персонаж найден — возвращаем 200 OK.
        if (character != null) {
            return ResponseEntity.ok(character);
        }

        // Если персонажа нет — возвращаем 404 Not Found.
        return ResponseEntity.notFound().build();
    }

    // POST: создаём нового персонажа.
    @PostMapping
    public ResponseEntity<Character> createCharacter(@Valid @RequestBody Character character) {
        Character createdCharacter = characterService.createCharacter(character);

        // Возвращаем 201 Created и созданного персонажа.
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCharacter);
    }

    // PUT: обновляем существующего персонажа.
    @PutMapping("/{id}")
    public ResponseEntity<Character> updateCharacter(
            @PathVariable Long id,
            @Valid @RequestBody Character updatedCharacter) {

        Character character = characterService.updateCharacter(id, updatedCharacter);

        // Если персонаж найден и обновлён — возвращаем 200 OK.
        if (character != null) {
            return ResponseEntity.ok(character);
        }

        // Если персонажа нет — возвращаем 404 Not Found.
        return ResponseEntity.notFound().build();
    }

    // DELETE: удаляем персонажа по ID.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCharacter(@PathVariable Long id) {
        boolean deleted = characterService.deleteCharacter(id);

        // Если удаление прошло успешно — возвращаем 204 No Content.
        if (deleted) {
            return ResponseEntity.noContent().build();
        }

        // Если персонажа нет — возвращаем 404 Not Found.
        return ResponseEntity.notFound().build();
    }
}
