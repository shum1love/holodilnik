package ru.holodilnik.framework.ui.pages.locators;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import ru.holodilnik.framework.ui.elements.UiElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$$;

/**
 * Локаторы страницы результатов поиска.
 * Только селекторы, никаких методов, никаких описаний в runtime.
 * Всё остальное — в компонентах или странице.
 */
public class SearchResultsLocators {

    public final UiElement title =
            ui("Заголовок страницы результатов", $$("h1").first());

    public final UiElement parameterBlock =
            ui("Блок фильтров", $$("div.filter-c__body").first());

    // ─── Категория ─────────────────────────────

    public final UiElement categorySectionTitle =
            ui("Фильтр «Категория»", $$("span").filterBy(text("Категория")).first());

    public final UiElement categoryQuickSearch =
            ui("Поиск по категориям", $$("input[id='quick-searchcategory']").first());

    public final ElementsCollection categoryItems =
            $$("label[id*='cfilter_search_category']");

    // ─── Цена ──────────────────────────────────

    public final UiElement priceSectionTitle =
            ui("Фильтр «Цена»", $$("span").filterBy(text("Цена")).first());

    public final UiElement minPriceInput =
            ui("Минимальная цена", $$("input[id='min_txt_price']").first());

    public final UiElement maxPriceInput =
            ui("Максимальная цена", $$("input[id='max_txt_price']").first());

    public final UiElement priceSliderValue =
            ui("Диапазон цен", $$("div[id='value_price']").first());

    // ─── Производитель ─────────────────────────

    public final UiElement manufacturerQuickSearch =
            ui("Поиск по производителям", $$("input[id='quick-searchvendor']").first());

    public final ElementsCollection manufacturerItems =
            $$("label[id*='cfilter_search_vendor']");

    // ─── Кнопки ────────────────────────────────

    public final UiElement applyFiltersButton =
            ui("Применить фильтры", $$("input[id='cfilter_btnsubmit']").first());

    public final UiElement clearFiltersButton =
            ui("Очистить фильтры", $$("a[id='cfilter_btnclear']").first());

    // ─── Внутреннее ────────────────────────────

    private UiElement ui(String name, SelenideElement element) {
        return new UiElement(name, element);
    }
}
