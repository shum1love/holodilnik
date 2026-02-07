package ru.holodilnik.framework.ui.pages;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverConditions;
import com.codeborne.selenide.WebDriverRunner;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.webdriver;
import static ru.holodilnik.framework.core.config.ConfigLoader.getBaseUrl;

/**
 * Базовый контракт для Page Objects.
 * Отвечает ТОЛЬКО за:
 * - знание своего URL
 * - открытие страницы
 * - проверку факта открытия (по якорному элементу)
 */
public abstract class BasePage<T extends BasePage<T>> {

    private final String url;

    protected BasePage(String relativePath) {
        this.url = getBaseUrl() + relativePath;
    }

    /**
     * Всегда открывает страницу И проверяет, что она открыта.
     */
    @SuppressWarnings("unchecked")
    public T open() {
        Selenide.open(url);
        WebDriverRunner.getWebDriver().manage().window().maximize();
        shouldBeOpen();
        return (T) this;
    }

    /**
     * Контракт: каждая страница определяет свой "якорь"
     */
    protected abstract SelenideElement pageIdentifier();

    /**
     * Явная проверка открытия страницы.
     * Вызывается из open() и может вызываться отдельно.
     */
    @SuppressWarnings("unchecked")
    public T shouldBeOpen() {
        pageIdentifier().shouldBe(visible);
        return (T) this;
    }

    // Навигация — без автоматической проверки (ответственность перекладывается на вызывающий код)
    @SuppressWarnings("unchecked")
    public T refresh() {
        Selenide.refresh();
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T back() {
        Selenide.back();
        return (T) this;
    }

    // Проверка URL — правильно через WebDriverConditions
    @SuppressWarnings("unchecked")
    public T shouldHaveUrl(String expected) {
        webdriver().shouldHave(WebDriverConditions.url(expected));
        return (T) this;
    }

    public String getCurrentUrl() {
        return WebDriverRunner.getWebDriver().getCurrentUrl();
    }
}