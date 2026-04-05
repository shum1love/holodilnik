package ru.holodilnik.framework.ui.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ru.holodilnik.framework.ui.locators.SearchResultsLocators;
import ru.holodilnik.framework.ui.pages.components.HeaderComponent;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThanOrEqual;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

/**
 * Класс описывающий страницу "Поиск товара".
 */
public final class SearchResultsPage extends BasePage<SearchResultsPage> {

    @Step("Проверить, что отображается блок фильтров")
    public SearchResultsPage checkParameterBlock() {
        SearchResultsLocators.parameterBlock().shouldBeVisible();
        return this;
    }

    @Step("Выбрать категорию '{categoryName}'")
    public SearchResultsPage selectCategory(final String categoryName) {
        SearchResultsLocators.categoryItems()
                .findBy(text(categoryName))
                .shouldBe(visible)
                .click();
        return this;
    }

    @Step("Нажать кнопку 'Показать'")
    public SearchResultsPage clickShowButton() {
        SearchResultsLocators.showButton().click();
        return this;
    }

    @Step("Проверить, что отображается несколько карточек товаров")
    public SearchResultsPage checkProductCardsArePresent() {
        SearchResultsLocators.productCards()
                .shouldHave(sizeGreaterThan(10));
        return this;
    }

    @Step("Открыть {number}-ю карточку товара")
    public ProductCardPage openProductCard(final int number) {
        SearchResultsLocators.titleCarts()
                .shouldHave(sizeGreaterThanOrEqual(number))
                .get(number - 1)
                .click();

        return new ProductCardPage().shouldBeOpen();
    }

    @Step("Добавить {number}-ю карточку товара в лист желаний")
    public SearchResultsPage addFavoriteList(final int number) {
        SearchResultsLocators
                .addToCartButtons()
                .shouldHave(sizeGreaterThanOrEqual(number))
                .get(number - 1)
                .click();

        return this;
    }

    @Step("Счётчик листа желаний показывает {expectedCount} товаров")
    public SearchResultsPage favoriteListCountShouldBe(final Integer expectedCount) {
        header.favoriteListCountShouldBe(expectedCount);
        return this;
    }

    @Step("Добавить {number}-ю карточку товара в корзину")
    public SearchResultsPage addProductCard(final int number) {
        SearchResultsLocators
                .addToFavoriteList()
                .shouldHave(sizeGreaterThanOrEqual(number))
                .get(number - 1)
                .click();

        return this;
    }

    @Step("Счётчик корзины показывает {expectedCount} товаров")
    public SearchResultsPage cartCountShouldBe(final int expectedCount) {
        header.cartCountShouldBe(expectedCount);
        return this;
    }

    @Step("Перейти в раздел 'Избранное'")
    public FavoriteList goToFavoriteList() {
        return header.goToFavoriteList();
    }

    @Step("Перейти в раздел 'Корзина'")
    public CartPage goToCardPage() {
        return header.goToShoppingCart();
    }

    // ─── Helper methods ─────────────────────────────────
    private final HeaderComponent header = new HeaderComponent($("header.site-header, header, .b-header"));

    public SearchResultsPage() {
        super("");
    }

    @Override
    protected SelenideElement pageIdentifier() {
        return SearchResultsLocators.title().getSelenideElement();
    }

    @Override
    public SearchResultsPage shouldBeOpen() {
        super.shouldBeOpen();
        SearchResultsLocators.title()
                .shouldBeVisible()
                .shouldContainText("Результаты поиска");
        return this;
    }
}