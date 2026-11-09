package com.javarpgarena.service;

// Импортируем модель персонажа.
import com.javarpgarena.model.Character;

// Импортируем аннотацию Spring Service.
import org.springframework.stereotype.Service;

// Импортируем коллекции для хранения персонажей.
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Импортируем генератор последовательных идентификаторов.
import java.util.concurrent.atomic.AtomicLong;

// Сообщаем Spring, что этот класс содержит бизнес-логику.
@Service
public class CharacterService {

    // Создаём хранилище персонажей в оперативной памяти.
    // LinkedHashMap сохраняет порядок добавления элементов.
    private final Map<Long, Character> characters = new LinkedHashMap<>();

    // Создаём генератор ID, начиная с третьего идентификатора.
    private final AtomicLong nextId = new AtomicLong(3);

    // Конструктор заполняет хранилище двумя начальными персонажами.
    public CharacterService() {
        characters.put(1L, new Character(1L, "Warrior", 100, 20, 0, 1));
        characters.put(2L, new Character(2L, "Mage", 70, 35, 0, 1));
    }

    // READ: возвращаем список всех персонажей.
    public List<Character> getAllCharacters() {
        return new ArrayList<>(characters.values());
    }

    // READ: ищем персонажа по его идентификатору.
    // Если персонажа нет, метод вернёт null.
    public Character getCharacterById(Long id) {
        return characters.get(id);
    }

    // CREATE: создаём нового персонажа.
    public Character createCharacter(Character character) {

        // Генерируем новый уникальный ID.
        Long id = nextId.getAndIncrement();

        // Присваиваем ID новому персонажу.
        character.setId(id);

        // Добавляем персонажа в хранилище.
        characters.put(id, character);

        // Возвращаем созданного персонажа.
        return character;
    }

    // UPDATE: обновляем характеристики существующего персонажа.
    public Character updateCharacter(Long id, Character updatedCharacter) {

        // Ищем существующего персонажа по ID.
        Character existingCharacter = characters.get(id);

        // Если персонаж не найден, возвращаем null.
        if (existingCharacter == null) {
            return null;
        }

        // Обновляем имя персонажа.
        existingCharacter.setName(updatedCharacter.getName());

        // Обновляем здоровье персонажа.
        existingCharacter.setHealth(updatedCharacter.getHealth());

        // Обновляем силу атаки.
        existingCharacter.setAttack(updatedCharacter.getAttack());

        // Обновляем накопленный опыт.
        existingCharacter.setExperience(updatedCharacter.getExperience());

        // Обновляем уровень персонажа.
        existingCharacter.setLevel(updatedCharacter.getLevel());

        // Возвращаем обновлённого персонажа.
        return existingCharacter;
    }

    // DELETE: удаляем персонажа по его ID.
    public boolean deleteCharacter(Long id) {

        // Удаляем персонажа и проверяем, существовал ли он.
        return characters.remove(id) != null;
    }
}
