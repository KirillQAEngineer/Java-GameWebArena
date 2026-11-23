package com.javarpgarena.ui.android;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Функциональные UI-тесты Android Chrome.
 * Все сценарии используют общий базовый класс AndroidUiTestBase.
 */
class AndroidCharacterUiTest extends AndroidUiTestBase {

    // 1. Проверяем открытие главной страницы.
    @Test
    void shouldOpenHomePage() {
        assertEquals(
                "Java RPG Arena",
                driver.getTitle(),
                "Название страницы должно быть Java RPG Arena"
        );
    }

    // 2. Проверяем главный заголовок страницы.
    @Test
    void shouldDisplayMainHeading() {
        WebElement heading = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.tagName("h1"))
        );

        assertEquals(
                "ВЫБЕРИ СВОЕГО ГЕРОЯ",
                heading.getText(),
                "Главный заголовок должен отображаться"
        );
    }

    // 3. Проверяем обновление списка персонажей.
    @Test
    void shouldRefreshCharactersList() {
        WebElement refreshButton = driver.findElement(By.id("refresh-button"));

        assertTrue(refreshButton.isDisplayed());

        // Повторно запрашиваем список персонажей.
        clickElement(refreshButton);

        // Ждём, пока карточки персонажей снова появятся.
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("[data-testid=\"character-card\"]")
        ));
    }

    // 4. Проверяем загрузку стартового списка персонажей.
    @Test
    void shouldDisplayCharactersList() {
        List<WebElement> cards = driver.findElements(
                By.cssSelector("[data-testid=\"character-card\"]")
        );

        assertTrue(
                cards.size() >= 2,
                "В начальном списке должны присутствовать минимум два персонажа"
        );
    }

    // 5. Проверяем имя и характеристики стартового Warrior.
    @Test
    void shouldDisplayWarriorCharacterData() {
        WebElement warriorCard = wait.until(
                ExpectedConditions.visibilityOf(cardByName("Warrior"))
        );

        List<WebElement> stats = warriorCard.findElements(
                By.cssSelector(".stat-item .stat-value")
        );

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

    // 7. Проверяем создание нового персонажа через мобильную форму.
    @Test
    void shouldCreateNewCharacter() {
        String name = createTestCharacter();

        WebElement createdCard = wait.until(
                ExpectedConditions.visibilityOf(cardByName(name))
        );

        assertTrue(createdCard.isDisplayed());
        assertEquals(name, createdCard.findElement(By.tagName("h3")).getText());
    }

    // 8. Проверяем заполнение формы при редактировании персонажа.
    @Test
    void shouldFillFormWhenEditingCharacter() {
        String name = createTestCharacter();
        WebElement card = wait.until(
                ExpectedConditions.visibilityOf(cardByName(name))
        );

        // Находим кнопку редактирования внутри карточки.
        WebElement editButton = card.findElement(
                By.cssSelector("[data-testid=\"edit-button\"]")
        );
        clickElement(editButton);

        // Проверяем, что форма редактирования заполнена данными персонажа.
        assertEquals("Редактировать персонажа", driver.findElement(By.id("form-title")).getText());
        assertEquals(name, driver.findElement(By.id("name")).getDomProperty("value"));
        assertEquals("120", driver.findElement(By.id("health")).getDomProperty("value"));
        assertTrue(driver.findElement(By.id("cancel-button")).isDisplayed());
    }

    // 9. Проверяем изменение и сохранение данных персонажа.
    @Test
    void shouldUpdateExistingCharacter() {
        String originalName = createTestCharacter();
        WebElement card = wait.until(
                ExpectedConditions.visibilityOf(cardByName(originalName))
        );

        // Открываем форму редактирования.
        WebElement editButton = card.findElement(
                By.cssSelector("[data-testid=\"edit-button\"]")
        );
        clickElement(editButton);

        // Меняем имя и здоровье персонажа.
        String updatedName = originalName + "-Updated";
        fillField("name", updatedName);
        fillField("health", "150");
        clickElement(driver.findElement(By.id("submit-button")));

        // Проверяем уведомление об успешном обновлении.
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.id("notification"), "Персонаж успешно обновлён."
        ));

        // Проверяем обновлённые данные в карточке.
        WebElement updatedCard = wait.until(
                ExpectedConditions.visibilityOf(cardByName(updatedName))
        );
        List<WebElement> stats = updatedCard.findElements(
                By.cssSelector(".stat-item .stat-value")
        );

        assertEquals(updatedName, updatedCard.findElement(By.tagName("h3")).getText());
        assertEquals("150", stats.get(0).getText());
    }

    // 10. Проверяем удаление персонажа с подтверждением.
    @Test
    void shouldDeleteCharacter() {
        String name = createTestCharacter();
        WebElement card = cardByName(name);

        // Находим кнопку удаления внутри карточки.
        WebElement deleteButton = card.findElement(
                By.cssSelector("[data-testid=\"delete-button\"]")
        );
        clickElement(deleteButton);

        // Подтверждаем удаление во всплывающем окне Chrome.
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.accept();

        // Ждём исчезновения карточки после удаления.
        assertTrue(wait.until(ExpectedConditions.invisibilityOf(card)), "Удалённая карточка должна исчезнуть");

        // Проверяем уведомление об успешном удалении.
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.id("notification"), "Персонаж успешно удалён."
        ));

        // Сообщаем базовому классу, что персонаж уже удалён вручную.
        createdCharacterId = null;


    }
    // ==================================================
    // НЕГАТИВНЫЕ UI-СЦЕНАРИИ ANDROID CHROME
    // ==================================================

    // 11. Проверяем запрет создания персонажа без имени.
    @Test
    void shouldRejectCharacterWithEmptyName() {
        fillField("name", "");
        fillField("health", "120");
        fillField("attack", "25");
        fillField("experience", "50");
        fillField("level", "2");
        assertInvalidForm();
    }

    // 12. Проверяем запрет создания персонажа без здоровья.
    @Test
    void shouldRejectCharacterWithEmptyHealth() {
        fillField("name", "Android-EmptyHealth");
        fillField("health", "");
        fillField("attack", "25");
        fillField("experience", "50");
        fillField("level", "2");
        assertInvalidForm();
    }

    // 13. Проверяем запрет создания персонажа с нулевым здоровьем.
    @Test
    void shouldRejectCharacterWithZeroHealth() {
        fillField("name", "Android-ZeroHealth");
        fillField("health", "0");
        fillField("attack", "25");
        fillField("experience", "50");
        fillField("level", "2");
        assertInvalidForm();
    }

    // 14. Проверяем запрет создания персонажа с отрицательной атакой.
    @Test
    void shouldRejectCharacterWithNegativeAttack() {
        fillField("name", "Android-NegativeAttack");
        fillField("health", "120");
        fillField("attack", "-1");
        fillField("experience", "50");
        fillField("level", "2");
        assertInvalidForm();
    }

    // 15. Проверяем отмену редактирования без сохранения изменений.
    @Test
    void shouldCancelCharacterEditingWithoutSavingChanges() {
        String originalName = createTestCharacter();
        WebElement card = wait.until(ExpectedConditions.visibilityOf(cardByName(originalName)));
        WebElement editButton = card.findElement(By.cssSelector("[data-testid=edit-button]"));
        clickElement(editButton);
        String unsavedName = originalName + "-Unsaved";
        fillField("name", unsavedName);
        clickElement(driver.findElement(By.id("cancel-button")));
        assertEquals("Создать персонажа", driver.findElement(By.id("form-title")).getText());
        assertEquals("", driver.findElement(By.id("character-id")).getDomProperty("value"));
        assertTrue(driver.findElements(By.cssSelector("[data-testid=character-card] h3")).stream().anyMatch(element -> element.getText().equals(originalName)));
        assertFalse(driver.findElements(By.cssSelector("[data-testid=character-card] h3")).stream().anyMatch(element -> element.getText().equals(unsavedName)));
    }

    // Общая проверка встроенной HTML-валидации формы.
    private void assertInvalidForm() {
        int cardsBefore = driver.findElements(By.cssSelector("[data-testid=character-card]")).size();
        boolean formIsValid = (Boolean) ((JavascriptExecutor) driver).executeScript(
                "return document.getElementById('character-form').checkValidity();"
        );
        assertFalse(formIsValid, "Браузер должен отклонить некорректную форму");
        clickElement(driver.findElement(By.id("submit-button")));
        int cardsAfter = driver.findElements(By.cssSelector("[data-testid=character-card]")).size();
        assertEquals(cardsBefore, cardsAfter, "Некорректный персонаж не должен создаваться");
    }

}
