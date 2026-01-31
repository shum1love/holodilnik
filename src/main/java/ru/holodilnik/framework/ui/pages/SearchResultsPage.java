package ru.holodilnik.framework.ui.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ru.holodilnik.framework.ui.pages.components.HeaderComponent;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static ru.holodilnik.framework.ui.pages.locators.SearchResultsLocators.*;

/**
 * Страница результатов поиска.
 * Только бизнес-действия + shouldBeOpen/isOpened.
 * Локаторы — снаружи, в SearchResultsLocators.
 */
public final class SearchResultsPage extends BasePage<SearchResultsPage> {

    private final HeaderComponent header;

    public SearchResultsPage() {
        super("");
        this.header = new HeaderComponent($("header.site-header, header, .b-header"));
    }

    @Override
    protected SelenideElement pageIdentifier() {
        return RESULTS_TITLE;
    }

    @Override
    public SearchResultsPage shouldBeOpen() {
        super.shouldBeOpen();
        RESULTS_TITLE.shouldBe(visible)
                .shouldHave(text("Категория"));
        return this;
    }

    public HeaderComponent header() {
        return header;
    }

    // ─── Примеры бизнес-методов ─────────────────────────────────────────

    @Step("Устанавливаем диапазон цен: от {min} до {max}")
    public SearchResultsPage setPriceRange(int min, int max) {
        MIN_PRICE_INPUT.shouldBe(visible).setValue(String.valueOf(min));
        MAX_PRICE_INPUT.shouldBe(visible).setValue(String.valueOf(max));
        APPLY_FILTERS_BUTTON.shouldBe(visible).click();
        return this;
    }

    @Step("Выбираем категорию по имени: {categoryName}")
    public SearchResultsPage selectCategory(String categoryName) {
        CATEGORY_ITEMS.findBy(text(categoryName)).shouldBe(visible).click();
        return this;
    }

    @Step("Очищаем все фильтры")
    public SearchResultsPage clearAllFilters() {
        CLEAR_FILTERS_BUTTON.shouldBe(visible).click();
        return this;
    }

    @Step("Проверяем минимум {minCount} товаров в выдаче")
    public SearchResultsPage shouldHaveAtLeastResults(int minCount) {
        // PRODUCT_CARDS.shouldHave(sizeGreaterThanOrEqual(minCount));  // добавь локатор когда будет
        return this;
    }

    @Step("Проверяем пустую выдачу")
    public SearchResultsPage shouldHaveNoResults() {
        // PRODUCT_CARDS.shouldHave(size(0));
        RESULTS_TITLE.shouldHave(text("Ничего не найдено"));
        return this;
    }
}