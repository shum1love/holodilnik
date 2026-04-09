package ru.holodilnik.framework.ui.locators;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import ru.holodilnik.framework.ui.elements.UiElement;
import ru.holodilnik.framework.ui.elements.UiElementsCollection;

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

    public static UiElement parameterBlock() {
        return ui("Блок фильтров", $("div.filter-c__body"));
    }

    public static UiElement categorySectionTitle() {
        return ui("Фильтр «Категория»", $$("span").filterBy(text("Категория")).first());
    }

    public static UiElement categoryQuickSearch() {
        return ui("Поиск по категориям", $("input[id='quick-searchcategory']"));
    }

    public static UiElementsCollection categoryItems() {
        return uis("Список категорий", $$("label[id*='cfilter_search_category']"));
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

    public static UiElementsCollection manufacturerItems() {
        return uis("Список производителей", $$("label[id*='cfilter_search_vendor']"));
    }

    public static UiElement showButton() {
        return ui("Применить фильтры", $("input[id='cfilter_btnsubmit']"));
    }

    public static UiElement clearFiltersButton() {
        return ui("Очистить фильтры", $("a[id='cfilter_btnclear']"));
    }

    public static UiElement sortingFilterDropdown(){return ui("Фильтр сортировки", $("button#dropdownItemSorting"));}

    public static UiElementsCollection sortingFilterParameters(){return uis("Параметры в фильтре сортировки", $$("div[aria-labelledby='dropdownItemSorting'] > a"));}

    public static UiElementsCollection productCards() {
        return uis("Карточки товаров", $$(".goods-tile.preview-product"));
    }

    public static UiElementsCollection addToFavoriteList() {
        return uis("Кнопки добавления в избранное", $$("span[data-action-button='favorite']"));
    }

    public static UiElementsCollection addToCartButtons() {
        return uis("Кнопки добавления в корзину", $$("a[data-smoke='AddToCartListing']"));
    }

    public static UiElementsCollection titleCarts() {
        return uis("Названия карточек товаров", $$("span[class*='product-name']"));
    }

    private static UiElement ui(String name, SelenideElement element) {
        return new UiElement(name, element);
    }

    private static UiElementsCollection uis(String name, ElementsCollection elements) {
        return new UiElementsCollection(name, elements);
    }

    private SearchResultsLocators() {
        throw new AssertionError("Этот класс — только статические методы, экземпляры создавать нельзя");
    }
}
