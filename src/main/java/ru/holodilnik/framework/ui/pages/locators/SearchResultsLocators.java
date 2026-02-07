package ru.holodilnik.framework.ui.pages.locators;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import ru.holodilnik.framework.ui.elements.UiElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

/**
 * Локаторы страницы "Результаты поиска".
 */
public final class SearchResultsLocators {

    public static UiElement title() {
        return ui("Заголовок страницы результатов", $$("h1").first());
    }

    // parameterBlock
    public static UiElement parameterBlock() {
        return ui("Блок фильтров", $("div.filter-c__body"));
    }

    public static UiElement categorySectionTitle() {
        return ui("Фильтр «Категория»", $$("span").filterBy(text("Категория")).first());
    }

    public static UiElement categoryQuickSearch() {
        return ui("Поиск по категориям", $("input[id='quick-searchcategory']"));
    }

    public static ElementsCollection categoryItems() {
        return $$("label[id*='cfilter_search_category']");
    }

    public static UiElement priceSectionTitle() {
        return ui("Фильтр «Цена»", $$("span").filterBy(text("Цена")).first());
    }

    public static UiElement minPriceInput() {
        return ui("Минимальная цена", $("input[id='min_txt_price']"));
    }

    public static UiElement maxPriceInput() {
        return ui("Максимальная цена", $("input[id='max_txt_price']"));
    }

    public static UiElement priceSliderValue() {
        return ui("Диапазон цен", $("div[id='value_price']"));
    }

    public static UiElement manufacturerQuickSearch() {
        return ui("Поиск по производителям", $("input[id='quick-searchvendor']"));
    }

    public static ElementsCollection manufacturerItems() {
        return $$("label[id*='cfilter_search_vendor']");
    }

    public static UiElement showButton() {
        return ui("Применить фильтры", $("input[id='cfilter_btnsubmit']"));
    }

    public static UiElement clearFiltersButton() {
        return ui("Очистить фильтры", $("a[id='cfilter_btnclear']"));
    }

    // Блок поиска результатов
    public static ElementsCollection productCards() {
        return $$(".goods-tile.preview-product");
    }

    public static ElementsCollection addToCartButtons() {
        return $$("a[data-smoke='AddToCartListing']");
    }

    private static UiElement ui(String name, SelenideElement element) {
        return new UiElement(name, element);
    }

    private SearchResultsLocators() {
        throw new AssertionError("Этот класс — только статические методы, экземпляры создавать нельзя");
    }
}