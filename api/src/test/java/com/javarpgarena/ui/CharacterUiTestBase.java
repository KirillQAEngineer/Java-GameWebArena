package com.javarpgarena.ui;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Общий базовый класс функциональных Web UI тестов.
 * Все тесты наследуются Chrome, Firefox и Safari классами.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class CharacterUiTestBase {

    // Объект управления браузером.
    protected WebDriver driver;

    // Ожидание появления элементов страницы.
    protected WebDriverWait wait;

    // ID персонажа, созданного текущим тестом.
    private String createdCharacterId;

    // Дочерние классы выбирают конкретный браузер.
    protected abstract WebDriver createDriver();

    // Запускаем браузер один раз перед всеми тестами класса.
    @BeforeAll
    void setUpBrowser() {
        driver = createDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // Перед каждым тестом открываем главную страницу.
    // Браузер при этом не перезапускается.
    @BeforeEach
    void openHomePage() {
        createdCharacterId = null;
        driver.get("http://127.0.0.1:8080");

        // Ждём, пока список персонажей загрузится с сервера.
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("[data-testid='character-card']")
        ));
    }

    // Удаляем тестового персонажа после каждого теста,
    // если он был создан и ещё существует.
    @AfterEach
    void cleanUpTestCharacter() {
        if (createdCharacterId == null) {
            return;
        }

        By cardLocator = By.cssSelector(
                "article[data-testid='character-card'][data-character-id='"
                        + createdCharacterId + "']"
        );

        List<WebElement> cards = driver.findElements(cardLocator);

        if (cards.isEmpty()) {
            return;
        }

        // Находим кнопку удаления внутри нужной карточки.
        WebElement deleteButton = cards.get(0).findElement(
                By.cssSelector("[data-testid='delete-button']")
        );

        // Прокручиваем кнопку в видимую область и нажимаем её.
        clickElement(deleteButton);

        // Подтверждаем системный диалог браузера.
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.accept();

        // Ждём, пока карточка исчезнет со страницы.
        wait.until(ExpectedConditions.invisibilityOfElementLocated(cardLocator));
    }

    // Закрываем браузер один раз после всех проверок.
    @AfterAll
    void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    // Создаёт уникального тестового персонажа через UI.
    private String createTestCharacter() {
        String name = "TestHero-" + UUID.randomUUID();

        fillField("name", name);
        fillField("health", "120");
        fillField("attack", "25");
        fillField("experience", "50");
        fillField("level", "2");

        clickElement(driver.findElement(By.id("submit-button")));

        // Проверяем уведомление об успешном создании.
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.id("notification"),
                "Персонаж успешно создан."
        ));

        // Находим карточку созданного персонажа.
        By cardLocator = cardByName(name);
        WebElement card = wait.until(
                ExpectedConditions.visibilityOfElementLocated(cardLocator)
        );

        // Сохраняем ID для последующей очистки.
        createdCharacterId = card.getAttribute("data-character-id");

        return name;
    }

    // Формирует локатор карточки по имени персонажа.
    private By cardByName(String name) {
        return By.xpath(
                "//article[@data-testid='character-card']"
                        + "[.//h3[normalize-space()='" + name + "']]"
        );
    }

    // Прокручиваем элемент в центр видимой области без плавной анимации.
    private void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center', inline: 'nearest', behavior: 'instant'});",
                element
        );

        // Ждём, пока элемент станет видимым и доступным для взаимодействия.
        wait.until(ExpectedConditions.visibilityOf(element));
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    // Надёжно заполняем текстовые и числовые поля во всех браузерах.
    private void fillField(String id, String value) {
    // Находим поле по его уникальному HTML-идентификатору.
    WebElement element = driver.findElement(By.id(id));

    // Прокручиваем страницу к полю.
    scrollToElement(element);

    // Кликаем по полю, чтобы Safari перевёл его в активное состояние.
    element.click();

    // Очищаем старое значение средствами Selenium.
    element.clear();

    // Вводим новое значение.
    element.sendKeys(value);
}

    // Нажимаем кнопку только после её появления в видимой области.
    private void clickElement(WebElement element) {
        scrollToElement(element);
        element.click();
    }

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
        String heading = driver.findElement(
                By.tagName("h1")
        ).getText();

        assertEquals(
                "ВЫБЕРИ СВОЕГО ГЕРОЯ",
                heading,
                "Главный заголовок должен отображаться"
        );
    }

    // 3. Проверяем работу кнопки обновления списка.
    @Test
    void shouldRefreshCharactersList() {
        WebElement refreshButton = driver.findElement(
                By.id("refresh-button")
        );

        assertTrue(refreshButton.isDisplayed());

        // Запрашиваем список персонажей повторно.
        refreshButton.click();

        // Проверяем, что карточки снова отображаются.
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("[data-testid='character-card']")
        ));
    }

    // 4. Проверяем загрузку списка персонажей.
    @Test
    void shouldDisplayCharactersList() {
        List<WebElement> cards = driver.findElements(
                By.cssSelector("[data-testid='character-card']")
        );

        assertTrue(
                cards.size() >= 2,
                "В начальном списке должны присутствовать два персонажа"
        );
    }

    // 5. Проверяем имя и характеристики стартового Warrior.
    @Test
    void shouldDisplayWarriorCharacterData() {
        WebElement warriorCard = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        cardByName("Warrior")
                )
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

    // 6. Проверяем видимость полей формы и значения по умолчанию.
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
                ExpectedConditions.visibilityOfElementLocated(cardByName(name))
        );

        assertTrue(createdCard.isDisplayed());
        assertEquals(name, createdCard.findElement(By.tagName("h3")).getText());
    }

    // 8. Проверяем заполнение формы при нажатии кнопки редактирования.
    @Test
    void shouldFillFormWhenEditingCharacter() {
        String name = createTestCharacter();

        WebElement card = wait.until(
                ExpectedConditions.visibilityOfElementLocated(cardByName(name))
        );

        // Находим кнопку редактирования персонажа.
        WebElement editButton = card.findElement(
                By.cssSelector("[data-testid='edit-button']")
        );

        // Прокручиваем кнопку в видимую область и нажимаем её.
        clickElement(editButton);

        assertEquals(
                "Редактировать персонажа",
                driver.findElement(By.id("form-title")).getText()
        );

        assertEquals(
                name,
                driver.findElement(By.id("name")).getAttribute("value")
        );

        assertEquals(
                "120",
                driver.findElement(By.id("health")).getAttribute("value")
        );

        assertTrue(driver.findElement(By.id("cancel-button")).isDisplayed());
    }

    // 9. Проверяем изменение и сохранение данных персонажа.
    @Test
    void shouldUpdateExistingCharacter() {
        String originalName = createTestCharacter();

        WebElement card = wait.until(
                ExpectedConditions.visibilityOfElementLocated(cardByName(originalName))
        );

        card.findElement(By.cssSelector("[data-testid='edit-button']")).click();

        String updatedName = originalName + "-Updated";

        fillField("name", updatedName);
        fillField("health", "150");

        clickElement(driver.findElement(By.id("submit-button")));

        // Проверяем уведомление об успешном обновлении.
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.id("notification"),
                "Персонаж успешно обновлён."
        ));

        // Проверяем новые данные в карточке.
        WebElement updatedCard = wait.until(
                ExpectedConditions.visibilityOfElementLocated(cardByName(updatedName))
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

        By cardLocator = cardByName(name);

        WebElement card = wait.until(
                ExpectedConditions.visibilityOfElementLocated(cardLocator)
        );

        card.findElement(By.cssSelector("[data-testid='delete-button']")).click();

        // Подтверждаем удаление в диалоговом окне браузера.
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.accept();

        // Проверяем, что карточка исчезла.
        wait.until(ExpectedConditions.invisibilityOfElementLocated(cardLocator));

        // Проверяем уведомление об успешном удалении.
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.id("notification"),
                "Персонаж успешно удалён."
        ));

        // Сообщаем очистке, что персонаж уже удалён.
        createdCharacterId = null;

        assertFalse(
                driver.findElements(cardLocator).size() > 0,
                "Удалённый персонаж не должен отображаться"
        );
    }
}
