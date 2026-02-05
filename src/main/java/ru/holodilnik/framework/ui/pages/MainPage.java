package ru.holodilnik.framework.ui.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ru.holodilnik.framework.ui.elements.UiElement;
import ru.holodilnik.framework.ui.pages.components.HeaderComponent;

import static com.codeborne.selenide.Selenide.$;
import static ru.holodilnik.framework.ui.pages.locators.MainPageLocators.*;

/**
 * Главная страница сайта holodilnik.ru
 */
public final class MainPage extends BasePage<MainPage> {
    private final HeaderComponent header;

    public MainPage() {
        super("/");
        this.header = new HeaderComponent($("site-header__body"));
    }

    @Override
    protected UiElement pageAnchor() {
        return logo();
    }

    @Step("Проверить видимость хедера и всех его элементов")
    public MainPage checkHeaderVisible(){
        header.shouldBeVisible();
        return this;
    }

    /**
     * Явный доступ к компоненту (использовать осознанно).
     */
    public HeaderComponent header() {
        return header;
    }
}
