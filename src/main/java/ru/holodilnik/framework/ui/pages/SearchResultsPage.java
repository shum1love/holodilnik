package ru.holodilnik.framework.ui.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ru.holodilnik.framework.ui.elements.UiElement;
import ru.holodilnik.framework.ui.pages.components.HeaderComponent;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.$;

public final class SearchResultsPage extends BasePage<SearchResultsPage> {

    // ─── Elements ───────────────────────────────────────────

    private final UiElement resultsTitle =
            ui("Заголовок страницы результатов", $$("h1").first());

    private final UiElement clearFiltersButton =
            ui("Очистить фильтры", $$("a[id='cfilter_btnclear']").first());

    private final HeaderComponent header =
            new HeaderComponent($("header.site-header, header, .b-header"));

    // Коллекция — без UiElement, это нормально
    private final ElementsCollection categoryItems =
            $$("label[id*='cfilter_search_category']");

    // ─── BasePage ──────────────────────────────────────────

    public SearchResultsPage() {
        super("");
    }

    @Override
    protected SelenideElement pageIdentifier() {
        // BasePage пока живёт в старом мире — отдаём raw element
        return resultsTitle.getElement();
    }

    @Override
    public SearchResultsPage shouldBeOpen() {
        super.shouldBeOpen();
        resultsTitle
                .shouldBeVisible()
                .shouldContainText("Категория");
        return this;
    }

    // ─── Business actions ─────────────────────────────────

    public HeaderComponent header() {
        return header;
    }

    @Step("Выбираем категорию '{categoryName}'")
    public SearchResultsPage selectCategory(String categoryName) {
        categoryItems
                .findBy(text(categoryName))
                .shouldBe(visible)
                .click();
        return this;
    }

    @Step("Очищаем все фильтры")
    public SearchResultsPage clearAllFilters() {
        clearFiltersButton.click();
        return this;
    }

    @Step("Проверяем пустую выдачу")
    public SearchResultsPage shouldHaveNoResults() {
        resultsTitle.shouldContainText("Ничего не найдено");
        return this;
    }

    // ─── Internal ─────────────────────────────────────────

    private UiElement ui(String name, SelenideElement element) {
        return new UiElement(name, element);
    }
}
