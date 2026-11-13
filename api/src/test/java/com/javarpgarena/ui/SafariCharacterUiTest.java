package com.javarpgarena.ui;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.safari.SafariDriver;

import org.junit.jupiter.api.TestInstance;

/**
 * Запускает общий набор UI-тестов в Safari.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SafariCharacterUiTest extends CharacterUiTestBase {

    @Override
    protected WebDriver createDriver() {
        return new SafariDriver();
    }
}
