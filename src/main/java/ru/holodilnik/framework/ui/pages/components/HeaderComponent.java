package ru.holodilnik.framework.ui.pages.components;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ru.holodilnik.framework.ui.elements.UiElement;
import ru.holodilnik.framework.ui.pages.CartPage;
import ru.holodilnik.framework.ui.pages.FavoriteList;
import ru.holodilnik.framework.ui.pages.SearchResultsPage;

import static com.codeborne.selenide.Selectors.withText;

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
    private final UiElement cartCount;
    private final UiElement favoriteListCount;

    public HeaderComponent(final SelenideElement container) {
        this.self = new UiElement("Шапка сайта", container);

        this.logo = new UiElement("Логотип", container.$(".site-header__logo"));
        this.catalogBtn = new UiElement("Кнопка каталога", container.$("span[data-ga-event-category='UpCatMenuView']"));
        this.searchField = new UiElement("Поле поиска", container.$("input[id='top_search']"));
        this.searchSubmitButton = new UiElement("Кнопка поиска", container.$("button[data-smoke='GoSearchBtn']"));
        this.personalAccount = new UiElement("Личный кабинет", container.$("div[data-user-block]"));
        this.myOrders = new UiElement("Мои заказы", container.$(withText("Мои заказы")));
        this.favorites = new UiElement("Избранное", container.$("a[data-ga-event-category='HeaderFavoriteClick'] > span:first-child"));
        this.shoppingCart = new UiElement("Корзина", container.$("a[data-ga-event-category='HeaderBasketClick'] > span:first-child"));
        this.cartCount = new UiElement("Счётчик товаров в корзине", container.$("#numInCart"));
        this.favoriteListCount = new UiElement("Счётчик товаров в листе ожиданий", container.$("#favorite_products_count"));
    }

    @Step("Проверяем, что шапка отображается")
    public void shouldBeVisible() {
        self.shouldBeVisible();
        logo.shouldBeVisible();
        catalogBtn.shouldBeVisible();
        searchField.shouldBeVisible();
        searchSubmitButton.shouldBeVisible();
        personalAccount.shouldBeVisible();
        myOrders.shouldBeVisible();
        favorites.shouldBeVisible();
        shoppingCart.shouldBeVisible();
    }

    @Step("Ввести значение {value} в поле поиска")
    public SearchResultsPage inputValueSearchField(final String value) {
        searchField.clearAndType(value);
        searchSubmitButton.click();
        return new SearchResultsPage();
    }

    @Step("Счётчик листа желаний показывает {expectedCount} товаров")
    public void favoriteListCountShouldBe(final Integer expectedCount) {
        if (expectedCount == null) {
            favoriteListCount.shouldHaveExactText("");
            return;
        }

        favoriteListCount.shouldHaveExactText(String.valueOf(expectedCount));
    }

    @Step("Счётчик корзины показывает {expectedCount} товаров")
    public void cartCountShouldBe(final int expectedCount) {
        cartCount.shouldHaveExactText(String.valueOf(expectedCount));
    }

    @Step("Нажать на кнопку 'Каталог'")
    public void goToCatalogue() {
        catalogBtn.click();
    }

    @Step("Перейти в раздел 'Лист желаний'")
    public FavoriteList goToFavoriteList() {
        favorites.click();
        return new FavoriteList().shouldBeOpen();
    }

    @Step("Перейти в раздел 'Корзина'")
    public CartPage goToShoppingCart() {
        shoppingCart.click();
        return new CartPage().shouldBeOpen();
    }

}