package ru.holodilnik.framework.ui.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ru.holodilnik.framework.ui.elements.UiElement;
import ru.holodilnik.framework.ui.pages.components.HeaderComponent;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static ru.holodilnik.framework.ui.pages.locators.SearchResultsLocators.*;

public final class SearchResultsPage extends BasePage<SearchResultsPage> {

    // ─── Elements ───────────────────────────────────────────

    private final HeaderComponent header =
            new HeaderComponent($("header.site-header, header, .b-header"));

    // ─── BasePage ──────────────────────────────────────────

    public SearchResultsPage() {
        super("");
    }

    @Override
    protected SelenideElement pageIdentifier() {
        return title().getElement();
    }

    @Override
    public SearchResultsPage shouldBeOpen() {
        super.shouldBeOpen();
        title()
                .shouldBeVisible()
                .shouldContainText("Результаты поиска");
        return this;
    }

    // ─── Business actions ─────────────────────────────────

    @Step("Проверить, что отображается блок фильтров")
    public SearchResultsPage checkParameterBlock(){
        parameterBlock().shouldBeVisible();
        return this;
    }
    @Step("Выбрать категорию '{categoryName}'")
    public SearchResultsPage selectCategory(String categoryName) {
        categoryItems()
                .findBy(text(categoryName))
                .shouldBe(visible)
                .click();
        return this;
    }

    @Step("Нажать кнопку 'Показать'")
    public SearchResultsPage clickShowButton() {
        showButton().click();
        return this;
    }

    @Step("Очищаем все фильтры")
    public SearchResultsPage clearAllFilters() {
        clearFiltersButton().click();
        return this;
    }

    @Step("Проверяем пустую выдачу")
    public SearchResultsPage shouldHaveNoResults() {
        title().shouldContainText("Ничего не найдено");
        return this;
    }

    // ─── Internal ─────────────────────────────────────────

    private UiElement ui(String name, SelenideElement element) {
        return new UiElement(name, element);
    }
}
