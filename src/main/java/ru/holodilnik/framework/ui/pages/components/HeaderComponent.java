package ru.holodilnik.framework.ui.pages.components;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ru.holodilnik.framework.ui.elements.UiElement;
import ru.holodilnik.framework.ui.pages.SearchResultsPage;

/**
 * Компонент шапки сайта (header).
 * Используется на большинстве страниц.
 */
public class HeaderComponent {

    private final UiElement self;
    private final UiElement logo;
    private final UiElement catalogBtn;
    private final UiElement searchField;
    private final UiElement searchSubmitButton;
    private final UiElement personalAccount;
    private final UiElement myOrders;
    private final UiElement favorites;
    private final UiElement shoppingCart;

    public HeaderComponent(SelenideElement container) {
        this.self = new UiElement("Шапка сайта", container);

        this.logo = new UiElement("Логотип", container.$(".site-header__logo"));
        this.catalogBtn = new UiElement("Кнопка каталога", container.$("span[data-ga-event-category='UpCatMenuView']"));
        this.searchField = new UiElement("Поле поиска", container.$("input[id='top_search']"));
        this.searchSubmitButton = new UiElement("Кнопка поиска", container.$("button[data-smoke='GoSearchBtn']"));
        this.personalAccount = new UiElement("Личный кабинет", container.$("div[data-user-block]"));
        this.myOrders = new UiElement("Мои заказы", container.$("a[href='/usercp/history/'] > span:first-child"));
        this.favorites = new UiElement("Избранное", container.$("a[data-ga-event-category='HeaderFavoriteClick'] > span:first-child"));
        this.shoppingCart = new UiElement("Корзина", container.$("a[data-ga-event-category='HeaderBasketClick'] > span:first-child"));
    }

    @Step("Проверяем, что шапка отображается")
    public HeaderComponent shouldBeVisible() {
        self.shouldBeVisible();

        logo.shouldBeVisible();
        catalogBtn.shouldBeVisible();
        searchField.shouldBeVisible();
        searchSubmitButton.shouldBeVisible();
        personalAccount.shouldBeVisible();
        myOrders.shouldBeVisible();
        favorites.shouldBeVisible();
        shoppingCart.shouldBeVisible();

        return this;
    }

    @Step("Ввести значение {value} в поле 'Поиск по каталогу'")
    public SearchResultsPage inputValueSearchField(final String value) {
        searchField.clearAndType(value);
        searchSubmitButton.click();
        return new SearchResultsPage();
    }

    /*@Step("Поиск товара: {query}")
    public SearchResultsPage search(String query) {
        searchField.clearAndType(query);
        searchSubmitButton.click();
        return new SearchResultsPage().shouldBeOpen();
    }*/
}