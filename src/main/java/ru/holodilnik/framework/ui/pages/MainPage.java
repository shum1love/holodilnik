package ru.holodilnik.framework.ui.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ru.holodilnik.framework.ui.locators.MainPageLocators;
import ru.holodilnik.framework.ui.pages.components.HeaderComponent;

import static com.codeborne.selenide.Selenide.$;

/**
 * Главная страница сайта holodilnik.ru
 */
public final class MainPage extends BasePage<MainPage> {
    private static HeaderComponent header;

    public MainPage() {
        super("/");
        this.header = new HeaderComponent($(".site-header__main"));
    }

    @Override
    protected SelenideElement pageIdentifier() {
        return MainPageLocators.logo().getSelenideElement();
    }

    @Step("Проверить видимость основных элементов главной страницы")
    public MainPage checkMainElements() {
        // TODO: Дописать метод
        return this;
    }

    @Step("Проверить видимость хедера и всех его элементов")
    public MainPage checkHeaderVisible() {
        header.shouldBeVisible();
        return this;
    }

    @Step("Ввести значение {value} в поле 'Поиск по каталогу'")
    public SearchResultsPage inputValueSearchField(final String value) {
        return header.inputValueSearchField(value);
    }

    @Step("Нажать на кнопку 'Каталог'")
    public CatalogueMenuPage openCatalogMenu() {
        header.goToCatalogue();
        return new CatalogueMenuPage().shouldBeOpen();
    }

    /**
     * Явный доступ к компоненту.
     */
    public HeaderComponent header() {
        return header;
    }
}
