package ru.holodilnik.framework.ui.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ru.holodilnik.framework.ui.elements.UiElement;
import ru.holodilnik.framework.ui.pages.components.HeaderComponent;
import ru.holodilnik.framework.ui.pages.locators.SearchResultsLocators;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public final class SearchResultsPage extends BasePage<SearchResultsPage> {

    private final HeaderComponent header =
            new HeaderComponent($("header.site-header, header, .b-header"));

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

    // ─── Business actions ─────────────────────────────────

    @Step("Проверить, что отображается блок фильтров")
    public SearchResultsPage checkParameterBlock() {
        SearchResultsLocators.parameterBlock().shouldBeVisible();
        return this;
    }

    @Step("Выбрать категорию '{categoryName}'")
    public SearchResultsPage selectCategory(String categoryName) {
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

    @Step("Очищаем все фильтры")
    public SearchResultsPage clearAllFilters() {
        SearchResultsLocators.clearFiltersButton().click();
        return this;
    }

    @Step("Проверяем пустую выдачу")
    public SearchResultsPage shouldHaveNoResults() {
        SearchResultsLocators.title().shouldContainText("Ничего не найдено");
        return this;
    }
}