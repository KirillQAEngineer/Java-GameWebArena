package com.javarpgarena.ui;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import org.junit.jupiter.api.TestInstance;

/**
 * Запускает общий набор UI-тестов в Firefox.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FirefoxCharacterUiTest extends CharacterUiTestBase {

    @Override
    protected WebDriver createDriver() {
        // Создаём настройки Firefox.
        FirefoxOptions options = new FirefoxOptions();

        // Получаем путь к Firefox, установленному GitHub Actions.
        String firefoxBinary = System.getenv("FIREFOX_BIN");

        // Если путь передан из CI, указываем Selenium именно этот браузер.
        if (firefoxBinary != null && !firefoxBinary.isBlank()) {
            options.setBinary(firefoxBinary);
        }

        // В CI запускаем браузер без графического интерфейса.
        if (Boolean.parseBoolean(System.getenv().getOrDefault("CI", "false"))) {
            options.addArguments("-headless");
        }

        // Локально Firefox остаётся видимым.
        return new FirefoxDriver(options);
    }
}
