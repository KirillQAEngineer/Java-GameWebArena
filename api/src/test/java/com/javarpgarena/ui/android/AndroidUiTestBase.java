package com.javarpgarena.ui.android;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URI;
import java.time.Duration;
import java.util.UUID;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AndroidUiTestBase {

    // WebDriver для управления Chrome в Android Emulator.
    protected WebDriver driver;

    // Ожидание появления элементов страницы.
    protected WebDriverWait wait;

    // Адрес приложения, доступный из Android Emulator.
    protected static final String BASE_URL = "http://10.0.2.2:8080";

    // ID персонажа, созданного текущим тестом.
    protected String createdCharacterId;

    // Настраиваем Android Chrome через UiAutomator2.
    @BeforeAll
    void setUpAndroid() throws Exception {
        UiAutomator2Options options = new UiAutomator2Options();
        options.setPlatformName("Android");
        options.setAutomationName("UiAutomator2");
        options.setDeviceName("Android Emulator");
        options.setCapability("browserName", "Chrome");
        options.setCapability("appium:chromedriverAutodownload", true);

        driver = new AndroidDriver(
                URI.create("http://127.0.0.1:4723").toURL(),
                options
        );

        // Максимальное ожидание элементов — 15 секунд.
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // Перед каждым тестом открываем страницу заново.
    @BeforeEach
    void openPageBeforeTest() {
        createdCharacterId = null;
        driver.get(BASE_URL);

        // Отключаем плавную прокрутку.
        ((JavascriptExecutor) driver).executeScript(
                "document.documentElement.style.scrollBehavior = 'auto';"
        );

        // Ждём появления карточек персонажей.
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("[data-testid=\"character-card\"]")
        ));
    }

    // Удаляем персонажа, созданного тестом.
    @AfterEach
    void cleanUpAfterTest() {
        if (createdCharacterId == null) {
            return;
        }

        // Ищем карточку по ID.
        By characterCard = By.cssSelector(
                "[data-testid=\"character-card\"][data-character-id=\"" + createdCharacterId + "\"]"
        );

        if (!driver.findElements(characterCard).isEmpty()) {
            WebElement card = driver.findElement(characterCard);
            WebElement deleteButton = card.findElement(
                    By.cssSelector("[data-testid=\"delete-button\"]")
            );

            clickElement(deleteButton);

            // Подтверждаем удаление.
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            alert.accept();

            // Ждём исчезновения карточки.
            wait.until(ExpectedConditions.invisibilityOfElementLocated(characterCard));
        }

        createdCharacterId = null;
    }

    // Закрываем браузер после всех тестов класса.
    @AfterAll
    void tearDownAndroid() {
        if (driver != null) {
            driver.quit();
        }
    }

    // Заполняем поле формы.
    protected void fillField(String fieldId, String value) {
        WebElement field = driver.findElement(By.id(fieldId));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center'});",
                field
        );

        field.clear();
        field.sendKeys(value);
    }

    // Прокручиваем страницу и нажимаем элемент.
    protected void clickElement(WebElement element) {

        // Закрываем клавиатуру, если используется Android Chrome.
        if (driver instanceof AndroidDriver androidDriver) {
            try {
                if (androidDriver.isKeyboardShown()) {
                    androidDriver.hideKeyboard();
                }
            } catch (Exception ignored) {
                // Продолжаем, если клавиатура уже закрыта.
            }
        }

        // Прокручиваем элемент в центр экрана.
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center', inline: 'nearest', behavior: 'auto'});",
                element
        );

        // Ждём доступности элемента и нажимаем.
        wait.until(ExpectedConditions.elementToBeClickable(element)).click();
    }

    // Создаём уникального персонажа.
    protected String createTestCharacter() {
        String characterName = "AndroidTest-" + UUID.randomUUID();

        fillField("name", characterName);
        fillField("health", "120");
        fillField("attack", "25");
        fillField("experience", "50");
        fillField("level", "2");

        clickElement(driver.findElement(By.id("submit-button")));

        // Ждём уведомления об успешном создании.
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.id("notification"), "Персонаж успешно создан."
        ));

        // Находим карточку напрямую по имени.
        By cardLocator = By.xpath("//article[@data-testid='character-card'][.//h3[normalize-space()='" + characterName + "']]");
        WebElement card = wait.until(
                ExpectedConditions.visibilityOfElementLocated(cardLocator)
        );

        // Сохраняем ID для последующей очистки.
        createdCharacterId = card.getAttribute("data-character-id");

        return characterName;
    }

    // Ищем карточку по имени персонажа.
    protected WebElement cardByName(String name) {
        for (WebElement card : driver.findElements(
                By.cssSelector("[data-testid=\"character-card\"]"))) {

            if (card.findElement(By.tagName("h3")).getText().equals(name)) {
                return card;
            }
        }

        throw new AssertionError("Карточка персонажа не найдена: " + name);
    }
}
