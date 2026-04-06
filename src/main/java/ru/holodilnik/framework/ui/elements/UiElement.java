package ru.holodilnik.framework.ui.elements;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.openqa.selenium.OutputType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.executeJavaScript;
import static com.codeborne.selenide.Selenide.screenshot;

/*
 * Обёртка над SelenideElement с человеческими шагами в Allure
 */

public class UiElement {

    private static final Logger log = LoggerFactory.getLogger(UiElement.class);
    private static final boolean HIGHLIGHT_ENABLED = Boolean.parseBoolean(System.getProperty("ui.highlight", "false"));
    private static final boolean SCREENSHOT_ON_FAIL = Boolean.parseBoolean(System.getProperty("ui.screenshot.on.fail", "true"));
    private static final int ACTION_RETRY_COUNT = 2;
    private static final Duration RETRY_DELAY = Duration.ofMillis(400);

    private final SelenideElement element;
    private final String humanName;

    public UiElement(String humanName, SelenideElement element) {
        this.humanName = humanName;
        this.element = element;
    }

    // ─── Действия ──────────────────────────────────────────────────────

    @Step("{humanName} → кликаем")
    public UiElement click() {
        performAction(() -> element.shouldBe(visible, Condition.enabled).click(), "клик");
        return this;
    }

    @Step("{humanName} → force-клик (JS)")
    public UiElement forceClick() {
        performAction(() -> executeJavaScript("arguments[0].click();", element), "force-клик");
        return this;
    }

    @Step("{text} → вводим '{text}'")
    public UiElement type(String text) {
        performAction(() -> element.shouldBe(visible, Condition.enabled).setValue(text), "ввод: " + text);
        return this;
    }

    @Step("очищаем и вводим '{text}'")
    public UiElement clearAndType(String text) {
        performAction(() -> {
            element.shouldBe(visible, Condition.enabled).clear();
            element.setValue(text);
        }, "clear + ввод: " + text);
        return this;
    }

    @Step("нажимаем Enter")
    public UiElement pressEnter() {
        performAction(element::pressEnter, "нажатие Enter");
        return this;
    }

    // ─── Проверки ──────────────────────────────────────────────────────

    public UiElement shouldBeVisible() {
        element.shouldBe(visible);
        return this;
    }

    public UiElement shouldNotBeVisible() {
        element.shouldNotBe(visible);
        return this;
    }

    public UiElement shouldHaveExactText(String expected) {
        element.shouldHave(Condition.exactText(expected));
        return this;
    }

    public UiElement shouldContainText(String substring) {
        element.shouldHave(Condition.text(substring));
        return this;
    }

    public UiElement shouldHaveValue(String expected) {
        element.shouldHave(Condition.value(expected));
        return this;
    }

    public UiElement shouldHaveClass(String className) {
        element.shouldHave(Condition.cssClass(className));
        return this;
    }

    public UiElement shouldHaveAttribute(String attr, String value) {
        element.shouldHave(Condition.attribute(attr, value));
        return this;
    }

    public UiElement shouldBeEnabled() {
        element.shouldBe(Condition.enabled);
        return this;
    }

    public UiElement shouldBeDisabled() {
        element.shouldBe(Condition.disabled);
        return this;
    }

    // ─── Утилиты ───────────────────────────────────────────────────────

    public UiElement scrollTo() {
        element.scrollTo();
        return this;
    }

    public UiElement hover() {
        element.hover();
        return this;
    }

    public SelenideElement getSelenideElement() {
        return element;
    }

    public String getText() {
        return element.shouldBe(visible).getText();
    }

    // ─── Внутренняя логика ─────────────────────────────────────────────

    private void performAction(Runnable action, String actionDesc) {
        log.debug("{} → {}", humanName, actionDesc);

        if (HIGHLIGHT_ENABLED) highlight();

        Exception lastError = null;
        for (int attempt = 1; attempt <= ACTION_RETRY_COUNT; attempt++) {
            try {
                action.run();
                if (HIGHLIGHT_ENABLED) unhighlight();
                return;
            } catch (Exception e) {
                lastError = e;
                if (attempt < ACTION_RETRY_COUNT && shouldRetry(e)) {
                    log.warn("Попытка {}: {} → {} → {}", attempt, humanName, actionDesc, e.getClass().getSimpleName());
                    // polling вместо sleep
                    element.shouldBe(visible, Duration.ofMillis(400));
                } else {
                    handleFailure(actionDesc, e);
                    throw new RuntimeException("Не удалось выполнить: " + actionDesc, lastError);
                }
            }
        }
    }

    private boolean shouldRetry(Exception e) {
        return e instanceof org.openqa.selenium.StaleElementReferenceException ||
                e instanceof org.openqa.selenium.ElementClickInterceptedException ||
                e instanceof org.openqa.selenium.ElementNotInteractableException;
    }

    private void highlight() {
        executeJavaScript(
                "arguments[0].dataset.origBorder = arguments[0].style.border;" +
                        "arguments[0].dataset.origShadow = arguments[0].style.boxShadow;" +
                        "arguments[0].style.border = '3px solid #ff3366';" +
                        "arguments[0].style.boxShadow = '0 0 12px #ff3366';",
                element
        );
    }

    private void unhighlight() {
        executeJavaScript(
                "arguments[0].style.border = arguments[0].dataset.origBorder || '';" +
                        "arguments[0].style.boxShadow = arguments[0].dataset.origShadow || '';",
                element
        );
    }

    private void handleFailure(String actionDesc, Exception e) {
        String currentUrl = WebDriverRunner.getWebDriver().getCurrentUrl();
        String selector = element.getSearchCriteria();
        String elementText = element.getText().trim();
        if (elementText.length() > 100) elementText = elementText.substring(0, 100) + "...";

        log.error("""
                Ошибка при действии "{}" на элементе "{}"
                URL: {}
                Селектор: {}
                Текст элемента: "{}"
                """, actionDesc, humanName, currentUrl, selector, elementText, e);

        if (SCREENSHOT_ON_FAIL) {
            try {
                byte[] bytes = screenshot(OutputType.BYTES);
                Allure.addAttachment(
                        "FAIL: " + humanName + " → " + actionDesc,
                        new ByteArrayInputStream(bytes)
                );
            } catch (Exception ex) {
                log.warn("Не удалось прикрепить скриншот", ex);
            }
        }
    }
}
