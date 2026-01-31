package ru.holodilnik.framework.ui.pages.locators;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

/**
 * Локаторы страницы результатов поиска.
 * Только селекторы, никаких методов, никаких описаний в runtime.
 * Всё остальное — в компонентах или странице.
 */
public final class SearchResultsLocators {

    private SearchResultsLocators() {}

    /** Общий заголовок страницы */
    public static final SelenideElement RESULTS_TITLE = $("h1");

     /** Блок фильтров целиком */
    public static final SelenideElement PARAMETER_BLOCK = $("div.filter-c__body");

    /** Секция Категория */
    public static final SelenideElement CATEGORY_SECTION_TITLE = $$("span").filterBy(text("Категория")).first();
    public static final SelenideElement CATEGORY_QUICK_SEARCH = $("input[id='quick-searchcategory']");
    public static final ElementsCollection CATEGORY_ITEMS = $$("label[id*='cfilter_search_category']");

     /** Секция Цена */
    public static final SelenideElement PRICE_SECTION_TITLE = $$("span").filterBy(text("Цена")).first();
    public static final SelenideElement MIN_PRICE_INPUT = $("input[id='min_txt_price']");
    public static final SelenideElement MAX_PRICE_INPUT = $("input[id='max_txt_price']");
    public static final SelenideElement PRICE_SLIDER_VALUE = $("div[id='value_price']");

    /** Секция Производитель */
    public static final SelenideElement MANUFACTURER_SECTION_TITLE = $$("h1").filterBy(text("Производитель")).first();
    public static final SelenideElement MANUFACTURER_QUICK_SEARCH = $("input[id='quick-searchvendor']");
    public static final ElementsCollection MANUFACTURER_ITEMS = $$("label[id*='cfilter_search_vendor']");

    /** Кнопки применения/очистки */
    public static final SelenideElement APPLY_FILTERS_BUTTON = $("input[id='cfilter_btnsubmit']");
    public static final SelenideElement CLEAR_FILTERS_BUTTON = $("a[id='cfilter_btnclear']");

    // TODO: добавить карточки товаров, счётчик найденного и т.д. когда появятся стабильные селекторы
}