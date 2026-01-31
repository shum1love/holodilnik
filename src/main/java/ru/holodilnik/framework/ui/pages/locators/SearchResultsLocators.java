package ru.holodilnik.framework.ui.pages.locators;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import ru.holodilnik.framework.ui.elements.UiElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

/**
 * Локаторы страницы результатов поиска.
 * Всё в одном месте, с человеческими именами.
 */
public final class SearchResultsLocators {

    private SearchResultsLocators() {}

    public static final UiElement title = ui("Заголовок страницы результатов", $$("h1").first());

    public static final UiElement parameterBlock = ui("Блок фильтров", $("div.filter-c__body"));

    // Категория
    public static final UiElement categorySectionTitle = ui("Фильтр «Категория»", $$("span").filterBy(text("Категория")).first());
    public static final UiElement categoryQuickSearch = ui("Поиск по категориям", $("input[id='quick-searchcategory']"));
    public static final ElementsCollection categoryItems = $$("label[id*='cfilter_search_category']");

    // Цена
    public static final UiElement priceSectionTitle = ui("Фильтр «Цена»", $$("span").filterBy(text("Цена")).first());
    public static final UiElement minPriceInput = ui("Минимальная цена", $("input[id='min_txt_price']"));
    public static final UiElement maxPriceInput = ui("Максимальная цена", $("input[id='max_txt_price']"));
    public static final UiElement priceSliderValue = ui("Диапазон цен", $("div[id='value_price']"));

    // Производитель
    public static final UiElement manufacturerQuickSearch = ui("Поиск по производителям", $("input[id='quick-searchvendor']"));
    public static final ElementsCollection manufacturerItems = $$("label[id*='cfilter_search_vendor']");

    // Кнопки
    public static final UiElement applyFiltersButton = ui("Применить фильтры", $("input[id='cfilter_btnsubmit']"));
    public static final UiElement clearFiltersButton = ui("Очистить фильтры", $("a[id='cfilter_btnclear']"));

    private static UiElement ui(String name, SelenideElement element) {
        return new UiElement(name, element);
    }
}
