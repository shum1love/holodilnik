package ru.holodilnik.framework.ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ru.holodilnik.framework.ui.enums.FilterParameters;
import ru.holodilnik.framework.ui.locators.SearchResultsLocators;
import ru.holodilnik.framework.ui.pages.components.HeaderComponent;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThanOrEqual;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

/**
 * Класс описывающий страницу "Поиск товара".
 */
public final class SearchResultsPage extends BasePage<SearchResultsPage> {

    private static final int ELEMENT_COUNT = 5;

    @Step("Проверить, что отображается блок фильтров и его элементы")
    public SearchResultsPage checkParameterBlock() {
        SearchResultsLocators.parameterBlock().shouldBeVisible();
        SearchResultsLocators.categorySectionTitle().shouldBeVisible();
        SearchResultsLocators.categoryQuickSearch().shouldBeVisible();
        SearchResultsLocators.categoryItems().first(ELEMENT_COUNT).shouldAllBeVisible();
        SearchResultsLocators.priceSectionTitle().shouldBeVisible();
        SearchResultsLocators.minPriceInput().shouldBeVisible();
        SearchResultsLocators.maxPriceInput().shouldBeVisible();
        SearchResultsLocators.priceSliderValue().shouldBeVisible();
        SearchResultsLocators.manufacturerQuickSearch().shouldBeVisible();
        SearchResultsLocators.manufacturerItems().first(ELEMENT_COUNT).shouldAllBeVisible();
        SearchResultsLocators.showButton().shouldBeVisible();
        SearchResultsLocators.clearFiltersButton().shouldBeVisible();
        return this;
    }

    @Step("Выбрать категорию '{categoryName}'")
    public SearchResultsPage selectCategory(final String categoryName) {
        SearchResultsLocators.categoryItems()
                .findBy(text(categoryName))
                .shouldBeVisible()
                .click();
        return this;
    }

    @Step("Выбрать производителя '{fabricName}'")
    public SearchResultsPage selectFabric(final String fabricName) {
        SearchResultsLocators.manufacturerItems()
                .scrollToAll()
                .findBy(text(fabricName))
                .shouldBeVisible()
                .click();
        return this;
    }

    @Step("Установить минимальную цену")
    public SearchResultsPage setMinPrice(final Integer minValue) {
        SearchResultsLocators.minPriceInput().clearAndType(minValue.toString());
        return this;
    }

    @Step("Установить максимальную цену")
    public SearchResultsPage setMaxPrice(final Integer maxValue) {
        SearchResultsLocators.maxPriceInput().clearAndType(maxValue.toString());
        return this;
    }

    @Step("Нажать кнопку 'Показать'")
    public SearchResultsPage clickShowButton() {
        SearchResultsLocators.showButton().click();
        return this;
    }

    @Step("Выбрать параметр {parameter} в фильтре сортировки")
    public SearchResultsPage sortByParam(final FilterParameters parameter) {
        SearchResultsLocators.sortingFilterDropdown().click();
        SearchResultsLocators.sortingFilterParameters()
                .findBy(text(parameter.getDisplayName()))
                .shouldBeVisible()
                .click();
        return this;
    }

    @Step("Проверить, что отображается надпись 'не найдено'")
    public void checkNothingWasFound() {
        SearchResultsLocators.nothingWasFound().shouldContainText("не найдено");
    }

    @Step("Кликнуть по хлебной крошке с текстом '{breadcrumbText}'")
    public SearchResultsPage clickBreadcrumb(final String breadcrumbText) {
        SearchResultsLocators.breadcrumbItems()
                .findBy(Condition.text(breadcrumbText))
                .shouldBeVisible()
                .click();
        return this;
    }

    @Step("Проверить, что отображается несколько карточек товаров")
    public SearchResultsPage checkProductCardsArePresent() {
        SearchResultsLocators.productCards()
                .shouldHave(sizeGreaterThan(ELEMENT_COUNT));
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

    @Step("Добавить {number}-ю карточку товара в Избранное")
    public SearchResultsPage addFavoriteList(final int number) {
        SearchResultsLocators
                .addToFavoriteList()
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
                .addToCartButtons()
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
