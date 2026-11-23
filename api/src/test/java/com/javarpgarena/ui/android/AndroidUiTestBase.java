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
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AndroidUiTestBase {

    // Драйвер управляет браузером Chrome внутри Android Emulator.
    protected AndroidDriver driver;

    // Объект ожидания помогает дождаться загрузки элементов страницы.
    protected WebDriverWait wait;

    // Адрес приложения на компьютере, доступный из Android Emulator.
    protected static final String BASE_URL = "http://10.0.2.2:8080";

    // ID персонажа, созданного текущим тестом.
    protected String createdCharacterId;

    // Создаём Appium-сессию один раз перед запуском тестового класса.
    @BeforeAll
    void setUpAndroid() throws Exception {
        UiAutomator2Options options = new UiAutomator2Options();
        options.setPlatformName("Android");
        options.setAutomationName("UiAutomator2");
        options.setDeviceName("Android Emulator");
        options.setCapability("browserName", "Chrome");
        options.setCapability("appium:chromedriverAutodownload", true);

        // Подключаемся к локальному Appium Server.
        driver = new AndroidDriver(
                URI.create("http://127.0.0.1:4723").toURL(),
                options
        );

        // Устанавливаем максимальное время ожидания элементов — 15 секунд.
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // Перед каждым тестом открываем страницу заново.
    @BeforeEach
    void openPageBeforeTest() {
        createdCharacterId = null;
        driver.get(BASE_URL);

        // Отключаем плавную прокрутку только в Android-тестах.
        ((JavascriptExecutor) driver).executeScript(
                "document.documentElement.style.scrollBehavior = 'auto';"
        );

        // Ждём появления карточек персонажей перед началом теста.
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("[data-testid=\"character-card\"]")
        ));
    }

    // После каждого теста удаляем персонажа, созданного этим тестом.
    @AfterEach
    void cleanUpAfterTest() {
        if (createdCharacterId == null) {
            return;
        }

        // Находим карточку по сохранённому ID персонажа.
        By characterCard = By.cssSelector(
                "[data-testid=\"character-card\"][data-character-id=\"" + createdCharacterId + "\"]"
        );

        // Если карточка существует, запускаем её удаление.
        if (!driver.findElements(characterCard).isEmpty()) {
            WebElement card = driver.findElement(characterCard);
            WebElement deleteButton = card.findElement(
                    By.cssSelector("[data-testid=\"delete-button\"]")
            );

            clickElement(deleteButton);

            // Подтверждаем удаление во всплывающем окне браузера.
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            alert.accept();

            // Ждём, пока удалённая карточка исчезнет со страницы.
            wait.until(ExpectedConditions.invisibilityOfElementLocated(characterCard));
        }

        // Очищаем ID после завершения процедуры.
        createdCharacterId = null;
    }

    // Закрываем Appium-сессию после завершения всех тестов класса.
    @AfterAll
    void tearDownAndroid() {
        if (driver != null) {
            driver.quit();
        }
    }

    // Заполняем поле формы с прокруткой к нужному элементу.
    protected void fillField(String fieldId, String value) {
        WebElement field = driver.findElement(By.id(fieldId));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: \"center\"});", field
        );
        field.clear();
        field.sendKeys(value);
    }

    // Прокручиваем страницу к элементу и выполняем пользовательское нажатие.
    // Прокручиваем страницу к элементу и выполняем обычный клик Selenium.
    protected void clickElement(WebElement element) {
        // Закрываем виртуальную клавиатуру, если она открыта.
        try {
            if (driver.isKeyboardShown()) {
                driver.hideKeyboard();
            }
        } catch (Exception ignored) {
            // Продолжаем, если клавиатура уже закрыта.
        }

        // Мгновенно размещаем элемент в центре экрана.
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center', inline: 'nearest', behavior: 'auto'});", element
        );

        // Ждём доступности элемента и выполняем обычный клик.
        wait.until(ExpectedConditions.elementToBeClickable(element)).click();
    }

    // Создаём уникального тестового персонажа и сохраняем его ID для очистки.
    protected String createTestCharacter() {
        String characterName = "AndroidTest-" + UUID.randomUUID();

        fillField("name", characterName);
        fillField("health", "120");
        fillField("attack", "25");
        fillField("experience", "50");
        fillField("level", "2");

        clickElement(driver.findElement(By.id("submit-button")));

        // Ждём уведомления об успешном создании персонажа.
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.id("notification"), "Персонаж успешно создан."
        ));

        // Находим созданную карточку и сохраняем её ID.
        WebElement card = wait.until(currentDriver -> {
            for (WebElement item : currentDriver.findElements(
                    By.cssSelector("[data-testid=\"character-card\"]"))) {
                WebElement heading = item.findElement(By.tagName("h3"));
                if (heading.getText().equals(characterName)) {
                    return item;
                }
            }
            return null;
        });

        createdCharacterId = card.getAttribute("data-character-id");
        return characterName;
    }

    // Находим карточку персонажа по отображаемому имени.
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
