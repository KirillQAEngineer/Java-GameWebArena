package com.javarpgarena.ui.ios;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
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
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class IosUiTestBase {

    // WebDriver для управления Safari в iOS Simulator.
    protected WebDriver driver;

    // Ожидание загрузки элементов страницы.
    protected WebDriverWait wait;

    // Адрес приложения. В iOS Simulator localhost указывает на Mac.
    protected static final String BASE_URL = System.getenv().getOrDefault(
            "BASE_URL", "http://localhost:8080"
    );

    // ID персонажа, созданного текущим тестом, для последующей очистки.
    protected String createdCharacterId;

    // Создаём Safari-сессию через Appium и XCUITest.
    @BeforeAll
    void setUpIos() throws Exception {
        XCUITestOptions options = new XCUITestOptions();

        // Указываем платформу и движок автоматизации.
        options.setPlatformName("iOS");
        options.setAutomationName("XCUITest");

        // Настройки устройства можно переопределить переменными CI.
        options.setDeviceName(System.getenv().getOrDefault(
                "IOS_DEVICE_NAME", "iPhone 16"
        ));
        options.setPlatformVersion(System.getenv().getOrDefault(
                "IOS_PLATFORM_VERSION", "18.2"
        ));

        // UDID нужен локально для выбора конкретного Simulator.
        String udid = System.getenv("IOS_UDID");
        if (udid != null && !udid.isBlank()) {
            options.setUdid(udid);
        }

        // Запускаем именно мобильный Safari, а не нативное приложение.
        options.setCapability("browserName", "Safari");
        options.setCapability("appium:newCommandTimeout", 120);

        // Адрес Appium можно изменить для CI или другого окружения.
        String appiumUrl = System.getenv().getOrDefault(
                "APPIUM_URL", "http://127.0.0.1:4723"
        );

        // Создаём сессию управления Safari.
        driver = new IOSDriver(URI.create(appiumUrl).toURL(), options);

        // Максимальное ожидание появления элементов — 15 секунд.
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // Перед каждым тестом открываем главную страницу приложения.
    @BeforeEach
    void openPageBeforeTest() {
        createdCharacterId = null;
        driver.get(BASE_URL);

        // Отключаем плавную прокрутку для стабильности UI-тестов.
        ((JavascriptExecutor) driver).executeScript(
                "document.documentElement.style.scrollBehavior = 'auto';"
        );

        // Ждём, пока загрузятся карточки персонажей.
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("[data-testid=\"character-card\"]")
        ));
    }

    // Удаляем тестового персонажа через API после каждого теста.
    @AfterEach
    void cleanUpAfterTest() throws Exception {
        if (createdCharacterId == null) {
            return;
        }

        // Сохраняем ID перед выполнением запроса.
        String characterId = createdCharacterId;

        try {
            // Формируем DELETE-запрос к API приложения.
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/api/characters/" + characterId))
                    .DELETE()
                    .build();

            // Отправляем запрос и получаем HTTP-ответ.
            HttpResponse<Void> response = HttpClient.newHttpClient().send(
                    request,
                    HttpResponse.BodyHandlers.discarding()
            );

            // Принимаем 204 (удалено) или 404 (персонаж уже отсутствует).
            if (response.statusCode() != 204 && response.statusCode() != 404) {
                throw new AssertionError(
                        "Не удалось очистить тестового персонажа. HTTP: "
                                + response.statusCode()
                );
            }
        } finally {
            // Сбрасываем ID, чтобы очистка не повторялась.
            createdCharacterId = null;
        }
    }

    // Закрываем Safari после завершения всех тестов класса.
    @AfterAll
    void tearDownIos() {
        if (driver != null) {
            driver.quit();
        }
    }

    // Заполняем поле формы с прокруткой к нему.
    protected void fillField(String fieldId, String value) {
        WebElement field = driver.findElement(By.id(fieldId));

        // Перемещаем поле в видимую область экрана.
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center'});", field
        );

        // Активируем поле, очищаем старое значение и вводим новое.
        field.click();
        field.clear();
        field.sendKeys(value);
    }

    // Прокручиваем элемент и нажимаем его после проверки доступности.
    protected void clickElement(WebElement element) {
        // Убираем фокус с поля ввода, чтобы закрыть экранную клавиатуру.
        ((JavascriptExecutor) driver).executeScript(
                "if (document.activeElement) document.activeElement.blur();"
        );

        // Прокручиваем нужный элемент в центр экрана.
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", element
        );

        // Ждём, пока элемент станет доступен для нажатия.
        wait.until(ExpectedConditions.elementToBeClickable(element)).click();
    }

    // Создаём уникального персонажа через пользовательскую форму.
    protected String createTestCharacter() {
        String characterName = "IosTest-" + UUID.randomUUID();

        // Заполняем все поля валидными значениями.
        fillField("name", characterName);
        fillField("health", "120");
        fillField("attack", "25");
        fillField("experience", "50");
        fillField("level", "2");

        // Отправляем форму создания персонажа.
        clickElement(driver.findElement(By.id("submit-button")));

        // Ждём подтверждения успешного создания.
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.id("notification"), "Персонаж успешно создан."
        ));

        // Находим карточку нового персонажа.
        WebElement card = wait.until(currentDriver -> {
            for (WebElement item : currentDriver.findElements(
                    By.cssSelector("[data-testid=\"character-card\"]"))) {
                if (item.findElement(By.tagName("h3")).getText().equals(characterName)) {
                    return item;
                }
            }
            return null;
        });

        // Сохраняем ID для очистки после теста.
        createdCharacterId = card.getAttribute("data-character-id");
        return characterName;
    }

    // Находим карточку персонажа по точному имени.
    protected By cardByName(String name) {
        return By.xpath("//article[@data-testid='character-card'][.//h3[normalize-space()='" + name + "']]");
    }
}
