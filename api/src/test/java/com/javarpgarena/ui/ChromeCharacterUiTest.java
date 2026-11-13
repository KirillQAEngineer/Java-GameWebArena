package com.javarpgarena.ui;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import org.junit.jupiter.api.TestInstance;

/**
 * Запускает общий набор UI-тестов в Chrome.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ChromeCharacterUiTest extends CharacterUiTestBase {

    @Override
    protected WebDriver createDriver() {
        // Создаём настройки Chrome.
        ChromeOptions options = new ChromeOptions();

        // В CI запускаем браузер без графического интерфейса.
        if (Boolean.parseBoolean(System.getenv().getOrDefault("CI", "false"))) {
            options.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage");
        }

        // Локально Chrome остаётся видимым.
        return new ChromeDriver(options);
    }
}
