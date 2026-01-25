package ru.holodilnik.framework.ui.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ru.holodilnik.framework.ui.pages.components.HeaderComponent;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThanOrEqual;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

/**
 * Страница результатов поиска по каталогу.
 */
public final class SearchResultsPage extends BasePage<SearchResultsPage> {

    // ─── Элементы страницы ───────────────────────────────────────────
    private static final SelenideElement RESULTS_CONTAINER = $(".search-results, .catalog-products, [data-block='products-list']");
    private static final SelenideElement RESULTS_TITLE = RESULTS_CONTAINER.$("h1, h2, .title");  // гибче, чем просто h1
    private static final SelenideElement FOUND_COUNT = $(".found-info, .search-results__found, [data-total-products]");
    private static final ElementsCollection PRODUCT_CARDS = $$(".product-card, .catalog-item, .product-item, [data-product-id]");

    private final HeaderComponent header;

    public SearchResultsPage() {
        super("");
        this.header = new HeaderComponent($("header.site-header, header, .b-header"));  // ← подставь реальный контейнер хедера
    }

    @Override
    protected SelenideElement pageIdentifier() {
        return RESULTS_CONTAINER;  // или RESULTS_TITLE, если уверен
    }

    @Override
    public SearchResultsPage shouldBeOpened() {  // isOpened → shouldBeOpened по твоему стилю
        super.shouldBeOpened();
        RESULTS_CONTAINER.shouldBe(visible);
        RESULTS_TITLE.shouldBe(visible).shouldHave(text("Поиск", "Найдено", "Результаты"));  // частичное совпадение — надёжнее
        PRODUCT_CARDS.shouldHave(sizeGreaterThanOrEqual(1));  // базовая защита от пустой страницы
        return this;
    }

    public HeaderComponent header() {
        return header;
    }

    @Step("Проверяем, что найдено минимум {minCount} товаров")
    public SearchResultsPage shouldHaveAtLeastResults(int minCount) {
        PRODUCT_CARDS.shouldHave(sizeGreaterThanOrEqual(minCount));
        return this;
    }

    @Step("Проверяем, что отображается количество найденных товаров: {expectedCount}")
    public SearchResultsPage shouldShowFoundCount(int expectedCount) {
        FOUND_COUNT.shouldBe(visible)
                .shouldHave(text(String.valueOf(expectedCount)));  // или точнее: text("Найдено " + expectedCount + " товаров")
        return this;
    }

    @Step("Открываем первую карточку товара")
    public ProductPage openFirstProduct() {
        SelenideElement firstCard = PRODUCT_CARDS.first();
        firstCard.$("a.product-link, a[href*='/product/'], .product-title a, a.title")
                .shouldBe(visible)
                .click();
        return new ProductPage().shouldBeOpened();
    }

    @Step("Открываем карточку товара №{index} (с 1)")
    public ProductPage openProductByIndex(int index) {
        if (index < 1) throw new IllegalArgumentException("Индекс должен быть >= 1");

        SelenideElement card = PRODUCT_CARDS.get(index - 1);
        card.$("a.product-link, a[href*='/product/'], .product-title a")
                .shouldBe(visible)
                .click();
        return new ProductPage().shouldBeOpened();
    }

    // Можно добавить, если часто нужно
    @Step("Проверяем, что результатов поиска нет (пустая выдача)")
    public SearchResultsPage shouldHaveNoResults() {
        PRODUCT_CARDS.shouldHave(size(0));
        RESULTS_TITLE.shouldHave(text("Ничего не найдено", "По вашему запросу"));
        return this;
    }
}