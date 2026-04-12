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

/**
 * Обёртка над SelenideElement с шагами в Allure
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
public class UiElement {

    private static final Logger log = LoggerFactory.getLogger(UiElement.class);
    private static final boolean HIGHLIGHT_ENABLED = Boolean.parseBoolean(System.getProperty("ui.highlight", "false"));
    private static final boolean SCREENSHOT_ON_FAIL = Boolean.parseBoolean(System.getProperty("ui.screenshot.on.fail", "true"));
    private static final int ACTION_RETRY_COUNT = 2;
    private final SelenideElement element;
    private final String name;

    public UiElement(final String name, final SelenideElement element) {
        this.name = name;
        this.element = element;
    }

    @Step("{name} → кликаем")
    public UiElement click() {
        performAction(() -> element.shouldBe(visible, Condition.enabled).click(), "клик");
        return this;
    }

    @Step("{name} → force-клик (JS)")
    public UiElement forceClick() {
        performAction(() -> executeJavaScript("arguments[0].click();", element), "force-клик");
        return this;
    }

    @Step("{name} → вводим '{text}'")
    public UiElement type(final String text) {
        performAction(() -> element.shouldBe(visible, Condition.enabled).setValue(text), "ввод: " + text);
        return this;
    }

    @Step("очищаем и вводим '{text}'")
    public UiElement clearAndType(final String text) {
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

    public UiElement shouldBeVisible() {
        element.shouldBe(visible);
        return this;
    }

    public UiElement shouldNotBeVisible() {
        element.shouldNotBe(visible);
        return this;
    }

    public UiElement shouldHaveExactText(final String expected) {
        element.shouldHave(Condition.exactText(expected));
        return this;
    }

    public UiElement shouldContainText(final String substring) {
        element.shouldHave(Condition.text(substring));
        return this;
    }

    public UiElement shouldHaveValue(final String expected) {
        element.shouldHave(Condition.value(expected));
        return this;
    }

    public UiElement shouldHaveClass(final String className) {
        element.shouldHave(Condition.cssClass(className));
        return this;
    }

    public UiElement shouldHaveAttribute(final String attr, final String value) {
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

    private void performAction(final Runnable action, final String actionDesc) {
        log.debug("{} → {}", name, actionDesc);

        if (HIGHLIGHT_ENABLED) highlight();

        Exception lastError = null;
        for (int attempt = 1; attempt <= ACTION_RETRY_COUNT; attempt++) {
            try {
                action.run();
                if (HIGHLIGHT_ENABLED) unhighlight();
                return;
            } catch (final Exception e) {
                lastError = e;
                if (attempt < ACTION_RETRY_COUNT && shouldRetry(e)) {
                    log.warn("Попытка {}: {} → {} → {}", attempt, name, actionDesc, e.getClass().getSimpleName());
                    // polling вместо sleep
                    element.shouldBe(visible, Duration.ofMillis(400));
                } else {
                    handleFailure(actionDesc, e);
                    throw new IllegalStateException("Не удалось выполнить действие: " + actionDesc, lastError);
                }
            }
        }
    }

    private static boolean shouldRetry(final Exception e) {
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

    private void handleFailure(final String actionDesc, final Exception e) {
        final String currentUrl = safeCurrentUrl();
        final String selector = safeSearchCriteria();
        String elementText = safeElementText();
        if (elementText.length() > 100) elementText = elementText.substring(0, 100) + "...";

        log.error("""
                Ошибка при действии "{}" на элементе "{}"
                URL: {}
                Селектор: {}
                Текст элемента: "{}"
                """, actionDesc, name, currentUrl, selector, elementText, e);

        if (SCREENSHOT_ON_FAIL) {
            try {
                final byte[] bytes = screenshot(OutputType.BYTES);
                Allure.addAttachment(
                        "FAIL: " + name + " → " + actionDesc,
                        new ByteArrayInputStream(bytes)
                );
            } catch (final Exception ex) {
                log.warn("Не удалось прикрепить скриншот", ex);
            }
        }
    }

    private static String safeCurrentUrl() {
        try {
            return WebDriverRunner.getWebDriver().getCurrentUrl();
        } catch (final Exception ex) {
            return "<URL недоступен>";
        }
    }

    private String safeSearchCriteria() {
        try {
            return element.getSearchCriteria();
        } catch (final Exception ex) {
            return "<селектор недоступен>";
        }
    }

    private String safeElementText() {
        try {
            return element.getText().trim();
        } catch (final Exception ex) {
            return "<текст недоступен>";
        }
    }
}
