package com.javarpgarena.ui.ios;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UI-тесты Java RPG Arena в мобильном Safari на iOS.
 * Все сценарии выполняются через Appium и XCUITest.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IosCharacterUiTest extends IosUiTestBase {

    // 1. Проверяем открытие главной страницы.
    @Test
    void shouldOpenHomePage() {
        assertEquals("Java RPG Arena", driver.getTitle(),
                "Название страницы должно быть Java RPG Arena");
    }

    // 2. Проверяем главный заголовок страницы.
    @Test
    void shouldDisplayMainHeading() {
        String heading = driver.findElement(By.tagName("h1")).getText();
        assertEquals("ВЫБЕРИ СВОЕГО ГЕРОЯ", heading,
                "Главный заголовок должен отображаться");
    }

    // 3. Проверяем работу кнопки обновления списка персонажей.
    @Test
    void shouldRefreshCharactersList() {
        WebElement refreshButton = driver.findElement(By.id("refresh-button"));
        assertTrue(refreshButton.isDisplayed());
        clickElement(refreshButton);
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("[data-testid=\"character-card\"]")));
    }

    // 4. Проверяем наличие начальных карточек персонажей.
    @Test
    void shouldDisplayCharactersList() {
        List<WebElement> cards = driver.findElements(
                By.cssSelector("[data-testid=\"character-card\"]"));
        assertTrue(cards.size() >= 2,
                "В начальном списке должны присутствовать два персонажа");
    }

    // 5. Проверяем характеристики стартового персонажа Warrior.
    @Test
    void shouldDisplayWarriorCharacterData() {
        WebElement warriorCard = wait.until(
                ExpectedConditions.visibilityOfElementLocated(cardByName("Warrior")));
        List<WebElement> stats = warriorCard.findElements(
                By.cssSelector(".stat-item .stat-value"));
        assertEquals("Warrior", warriorCard.findElement(By.tagName("h3")).getText());
        assertEquals("100", stats.get(0).getText(), "Здоровье Warrior");
        assertEquals("20", stats.get(1).getText(), "Атака Warrior");
        assertEquals("0", stats.get(2).getText(), "Опыт Warrior");
        assertEquals("1", stats.get(3).getText(), "Уровень Warrior");
    }

    // 6. Проверяем форму создания персонажа и значения по умолчанию.
    @Test
    void shouldDisplayCharacterFormWithDefaultValues() {
        assertTrue(driver.findElement(By.id("character-form")).isDisplayed());
        assertTrue(driver.findElement(By.id("name")).isDisplayed());
        assertTrue(driver.findElement(By.id("health")).isDisplayed());
        assertTrue(driver.findElement(By.id("attack")).isDisplayed());
        assertTrue(driver.findElement(By.id("experience")).isDisplayed());
        assertTrue(driver.findElement(By.id("level")).isDisplayed());
        assertEquals("100", driver.findElement(By.id("health")).getAttribute("value"));
        assertEquals("10", driver.findElement(By.id("attack")).getAttribute("value"));
        assertEquals("0", driver.findElement(By.id("experience")).getAttribute("value"));
        assertEquals("1", driver.findElement(By.id("level")).getAttribute("value"));
    }

    // 7. Проверяем создание нового персонажа через форму.
    @Test
    void shouldCreateNewCharacter() {
        String name = createTestCharacter();
        WebElement createdCard = wait.until(
                ExpectedConditions.visibilityOfElementLocated(cardByName(name)));
        assertTrue(createdCard.isDisplayed());
        assertEquals(name, createdCard.findElement(By.tagName("h3")).getText());
    }

    // 8. Проверяем заполнение формы при редактировании персонажа.
    @Test
    void shouldFillFormWhenEditingCharacter() {
        String name = createTestCharacter();
        WebElement card = wait.until(
                ExpectedConditions.visibilityOfElementLocated(cardByName(name)));
        WebElement editButton = card.findElement(
                By.cssSelector("[data-testid=\"edit-button\"]"));
        clickElement(editButton);
        assertEquals("Редактировать персонажа",
                driver.findElement(By.id("form-title")).getText());
        assertEquals(name, driver.findElement(By.id("name")).getAttribute("value"));
        assertEquals("120", driver.findElement(By.id("health")).getAttribute("value"));
        assertTrue(driver.findElement(By.id("cancel-button")).isDisplayed());
    }

    // 9. Проверяем изменение и сохранение данных персонажа.
    @Test
    void shouldUpdateExistingCharacter() {
        String originalName = createTestCharacter();
        WebElement card = wait.until(
                ExpectedConditions.visibilityOfElementLocated(cardByName(originalName)));
        WebElement editButton = card.findElement(
                By.cssSelector("[data-testid=\"edit-button\"]"));
        clickElement(editButton);
        String updatedName = originalName + "-Updated";
        fillField("name", updatedName);
        fillField("health", "150");
        clickElement(driver.findElement(By.id("submit-button")));
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.id("notification"), "Персонаж успешно обновлён."));
        WebElement updatedCard = wait.until(
                ExpectedConditions.visibilityOfElementLocated(cardByName(updatedName)));
        List<WebElement> stats = updatedCard.findElements(
                By.cssSelector(".stat-item .stat-value"));
        assertEquals(updatedName, updatedCard.findElement(By.tagName("h3")).getText());
        assertEquals("150", stats.get(0).getText());
    }

    // 10. Проверяем удаление персонажа с подтверждением.
    @Test
    void shouldDeleteCharacter() {
        String name = createTestCharacter();
        By cardLocator = cardByName(name);
        WebElement card = wait.until(
                ExpectedConditions.visibilityOfElementLocated(cardLocator));
        WebElement deleteButton = card.findElement(
                By.cssSelector("[data-testid=\"delete-button\"]"));
        clickElement(deleteButton);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(cardLocator));
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.id("notification"), "Персонаж успешно удалён."));
        createdCharacterId = null;
        assertFalse(driver.findElements(cardLocator).size() > 0,
                "Удалённый персонаж не должен отображаться");
    }

    // 11. Проверяем запрет создания персонажа без имени.
    @Test
    void shouldRejectCharacterWithoutName() {
        WebElement nameInput = driver.findElement(By.id("name"));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = \"\";"
                + "arguments[0].dispatchEvent(new Event(\"input\", {bubbles: true}));"
                + "arguments[0].dispatchEvent(new Event(\"change\", {bubbles: true}));",
                nameInput);
        clickElement(driver.findElement(By.id("submit-button")));
        assertFalse((Boolean) ((JavascriptExecutor) driver).executeScript(
                "return arguments[0].checkValidity();", nameInput),
                "Пустое имя не должно проходить валидацию");
        assertEquals("", driver.findElement(By.id("notification")).getText(),
                "Уведомление об успешном создании не должно появиться");
    }

    // 12. Проверяем отклонение имени, состоящего только из пробелов.
    @Test
    void shouldRejectCharacterWithBlankName() {
        fillField("name", "   ");
        fillField("health", "100");
        fillField("attack", "10");
        fillField("experience", "0");
        fillField("level", "1");
        clickElement(driver.findElement(By.id("submit-button")));
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.id("notification"), "HTTP 400"));
        assertTrue(driver.findElements(
                By.cssSelector("[data-testid=\"character-card\"]")).stream()
                .noneMatch(card -> card.findElement(By.tagName("h3")).getText().isEmpty()),
                "Персонаж с пустым именем не должен появиться");
    }

    // 13. Проверяем запрет нулевого здоровья.
    @Test
    void shouldRejectCharacterWithZeroHealth() {
        fillField("name", "InvalidHealthHero");
        fillField("health", "0");
        WebElement healthInput = driver.findElement(By.id("health"));
        clickElement(driver.findElement(By.id("submit-button")));
        assertFalse((Boolean) ((JavascriptExecutor) driver).executeScript(
                "return arguments[0].checkValidity();", healthInput),
                "Здоровье 0 не должно проходить валидацию");
    }

    // 14. Проверяем запрет отрицательного значения атаки.
    @Test
    void shouldRejectCharacterWithNegativeAttack() {
        fillField("name", "InvalidAttackHero");
        fillField("attack", "-1");
        WebElement attackInput = driver.findElement(By.id("attack"));
        clickElement(driver.findElement(By.id("submit-button")));
        assertFalse((Boolean) ((JavascriptExecutor) driver).executeScript(
                "return arguments[0].checkValidity();", attackInput),
                "Отрицательная атака не должна проходить валидацию");
    }

    // 15. Проверяем запрет нулевого уровня персонажа.
    @Test
    void shouldRejectCharacterWithZeroLevel() {
        fillField("name", "InvalidLevelHero");
        fillField("level", "0");
        WebElement levelInput = driver.findElement(By.id("level"));
        clickElement(driver.findElement(By.id("submit-button")));
        assertFalse((Boolean) ((JavascriptExecutor) driver).executeScript(
                "return arguments[0].checkValidity();", levelInput),
                "Уровень 0 не должен проходить валидацию");
    }
}
