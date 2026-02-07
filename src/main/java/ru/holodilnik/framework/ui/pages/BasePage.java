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
     * Проверка открытия страницы.
     */
    @SuppressWarnings("unchecked")
    public T shouldBeOpen() {
        pageIdentifier().shouldBe(visible);
        return (T) this;
    }

    /**
     * Перезагрузить страницу.
     */
    @SuppressWarnings("unchecked")
    public T refresh() {
        Selenide.refresh();
        return (T) this;
    }

    /**
     * Вернуться назад.
     */
    @SuppressWarnings("unchecked")
    public T back() {
        Selenide.back();
        return (T) this;
    }

    /**
     * Проверка url.
     */
    @SuppressWarnings("unchecked")
    public T shouldHaveUrl(String expected) {
        webdriver().shouldHave(WebDriverConditions.url(expected));
        return (T) this;
    }

    /**
     * Возвращает текущий URL страницы, открытой в браузере.
     */
    public String getCurrentUrl() {
        return WebDriverRunner.getWebDriver().getCurrentUrl();
    }
}