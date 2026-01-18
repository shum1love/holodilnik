package ru.holodilnik.framework.ui.pages;

import com.codeborne.selenide.Selenide;

import static ru.holodilnik.framework.core.config.ConfigLoader.getBaseUrl;

/**
 * Базовый класс для всех Page Objects.
 * Только open + isOpened.
 */
public abstract class BasePage {
    protected final String url;

    protected BasePage(String relativePath) {
        this.url = getBaseUrl() + relativePath;
    }

    public void open() {
        Selenide.open(url);
    }

    public abstract void isOpened() throws AssertionError;
}