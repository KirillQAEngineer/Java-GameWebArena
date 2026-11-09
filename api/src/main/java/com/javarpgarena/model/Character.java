package com.javarpgarena.model;

// Импортируем аннотации Bean Validation.
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// Модель игрового персонажа.
public class Character {

    // Уникальный ID назначается сервером.
    private Long id;

    // Имя обязательно и не может состоять только из пробелов.
    @NotBlank(message = "Name must not be blank")
    private String name;

    // Здоровье должно быть не меньше 1.
    @Min(value = 1, message = "Health must be at least 1")
    private int health;

    // Атака не может быть отрицательной.
    @Min(value = 0, message = "Attack must not be negative")
    private int attack;

    // Опыт не может быть отрицательным.
    @Min(value = 0, message = "Experience must not be negative")
    private int experience;

    // Уровень должен быть не меньше 1.
    @Min(value = 1, message = "Level must be at least 1")
    private int level;

    // Пустой конструктор для преобразования JSON в объект.
    public Character() {
    }

    // Конструктор для создания персонажа со всеми характеристиками.
    public Character(Long id, String name, int health, int attack, int experience, int level) {
        this.id = id;
        this.name = name;
        this.health = health;
        this.attack = attack;
        this.experience = experience;
        this.level = level;
    }

    // Возвращает ID персонажа.
    public Long getId() {
        return id;
    }

    // Устанавливает ID персонажа.
    public void setId(Long id) {
        this.id = id;
    }

    // Возвращает имя персонажа.
    public String getName() {
        return name;
    }

    // Изменяет имя персонажа.
    public void setName(String name) {
        this.name = name;
    }

    // Возвращает здоровье персонажа.
    public int getHealth() {
        return health;
    }

    // Изменяет здоровье персонажа.
    public void setHealth(int health) {
        this.health = health;
    }

    // Возвращает силу атаки.
    public int getAttack() {
        return attack;
    }

    // Изменяет силу атаки.
    public void setAttack(int attack) {
        this.attack = attack;
    }

    // Возвращает накопленный опыт.
    public int getExperience() {
        return experience;
    }

    // Изменяет накопленный опыт.
    public void setExperience(int experience) {
        this.experience = experience;
    }

    // Возвращает уровень персонажа.
    public int getLevel() {
        return level;
    }

    // Изменяет уровень персонажа.
    public void setLevel(int level) {
        this.level = level;
    }
}
