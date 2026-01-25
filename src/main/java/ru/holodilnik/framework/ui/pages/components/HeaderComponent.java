package ru.holodilnik.framework.ui.pages.components;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ru.holodilnik.framework.ui.pages.SearchResultsPage;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;

/**
 * Компонент шапки сайта (header).
 * Используется на большинстве страниц.
 */
public class HeaderComponent {

    private final SelenideElement self;

    // Элементы внутри шапки
    private final SelenideElement logo;
    private final SelenideElement catalogBtn;
    private final SelenideElement searchField;
    private final SelenideElement searchSubmitButton;
    private final SelenideElement personalAccount;
    private final SelenideElement myOrders;
    private final SelenideElement favorites;
    private final SelenideElement shoppingCart;

    public HeaderComponent(SelenideElement container) {
        this.self = container;

        this.logo = container.$(".site-header__logo");
        this.catalogBtn = container.$("span[data-ga-event-category='UpCatMenuView']");
        this.searchField = container.$("input[id='top_search']");
        this.searchSubmitButton = container.$("button[data-smoke='GoSearchBtn']");
        this.personalAccount = container.$("div[data-user-block]");
        this.myOrders = container.$("a[href='/usercp/history/'] > span:first-child");
        this.favorites = container.$("a[data-ga-event-category='HeaderFavoriteClick'] > span:first-child");
        this.shoppingCart = container.$("a[data-ga-event-category='HeaderBasketClick'] > span:first-child");
    }

    @Step("Проверяем, что шапка отображается")
    public HeaderComponent shouldBeVisible() {
        self.shouldBe(visible);

        logo.shouldBe(visible);
        catalogBtn.shouldBe(visible);
        searchField.shouldBe(visible);
        searchSubmitButton.shouldBe(visible);
        personalAccount.shouldBe(visible);
        myOrders.shouldBe(visible);
        favorites.shouldBe(visible);
        shoppingCart.shouldBe(visible);

        return this;
    }

    @Step("Поиск товара: {query}")
    public SearchResultsPage search(String query) {
        searchField.shouldBe(visible).clear();
        searchField.setValue(query).pressEnter();
        return new SearchResultsPage().isOpened();
    }

    @Step("Открыть каталог")
    public CatalogMenuComponent openCatalog() {
        catalogBtn.shouldBe(visible).click();
        return new CatalogMenuComponent();  // если это отдельный компонент, добавь .shouldBeVisible() или isOpened()
    }

    @Step("Открыть избранное")
    public FavoritesPage openFavorites() {
        favorites.shouldBe(visible).click();
        return new FavoritesPage().isOpened();
    }

    @Step("Открыть корзину")
    public CartPage openCart() {
        shoppingCart.shouldBe(visible).click();
        return new CartPage().isOpened();
    }

    @Step("Открыть личный кабинет")
    public ProfilePage openProfile() {  // добавил пример, если нужно
        profileDropdown.shouldBe(visible).click();
        return new ProfilePage().isOpened();
    }

    @Step("Проверить счётчик корзины: {expectedCount}")
    public HeaderComponent cartShouldHaveCount(int expectedCount) {
        shoppingCart.$("span").shouldHave(text(String.valueOf(expectedCount)));
        return this;
    }
}