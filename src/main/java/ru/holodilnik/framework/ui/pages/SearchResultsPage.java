package ru.holodilnik.framework.ui.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ru.holodilnik.framework.ui.pages.components.HeaderComponent;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThanOrEqual;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

/**
 * Страница результатов поиска по каталогу.
 */
public final class SearchResultsPage extends BasePage<SearchResultsPage> {

    // ─── Элементы страницы ───────────────────────────────────────────
    // Блок параметров
    private static final SelenideElement RESULTS_TITLE = $("h1");
    private static final SelenideElement PARAMETER_BLOCK = $("div.filter-c__body");
    private static final SelenideElement CATEGORY_TITLE = $$("h1").filterBy(text("Категория")).first();
    private static final SelenideElement CATEGORY_SEARCH_FIELD = $("input[id='quick-searchcategory']");
    private static final ElementsCollection CATEGORIES = $$("label[id*='cfilter_search_category']");
    private static final SelenideElement PRICE_TITLE = $$("h1").filterBy(text("Цена")).first();
    private static final SelenideElement MIN_COUNT_PRICE_BLOCK = $("input[id='min_txt_price']");
    private static final SelenideElement MAX_COUNT_PRICE_BLOCK = $("input[id='max_txt_price']");
    private static final SelenideElement SLIDER_COUNT_PRICE_BLOCK = $("div[id='value_price']");
    private static final SelenideElement MANUFACTURER_TITLE = $$("h1").filterBy(text("Производитель")).first();
    private static final SelenideElement MANUFACTURER_SEARCH_FIELD = $("input[id='quick-searchvendor']");
    private static final ElementsCollection MANUFACTURERS_LIST = $$("label[id*='cfilter_search_vendor']");
    private static final SelenideElement SHOW_BUTTON_PARAMETER_BLOCK = $("input[id='cfilter_btnsubmit']");
    private static final SelenideElement CLEAR_BUTTON_PARAMETER_BLOCK = $("a[id='cfilter_btnclear']");
    // Добавить фильтр
    // Добавить карточки товаров

    private final HeaderComponent header;

    public SearchResultsPage() {
        super("");
        this.header = new HeaderComponent($("header.site-header, header, .b-header"));
    }

    @Override
    protected SelenideElement pageIdentifier() {
        return RESULTS_CONTAINER;
    }

    @Override
    public SearchResultsPage shouldBeOpened() {
        super.shouldBeOpened();
        RESULTS_CONTAINER.shouldBe(visible);
        RESULTS_TITLE.shouldBe(visible).shouldHave(text("Поиск", "Найдено", "Результаты"));
        PRODUCT_CARDS.shouldHave(sizeGreaterThanOrEqual(1));
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

    @Step("Проверяем, что результатов поиска нет (пустая выдача)")
    public SearchResultsPage shouldHaveNoResults() {
        PRODUCT_CARDS.shouldHave(size(0));
        RESULTS_TITLE.shouldHave(text("Ничего не найдено", "По вашему запросу"));
        return this;
    }
}