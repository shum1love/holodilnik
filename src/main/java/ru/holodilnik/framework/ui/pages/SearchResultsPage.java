package ru.holodilnik.framework.ui.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ru.holodilnik.framework.ui.pages.components.HeaderComponent;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThanOrEqual;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;


/**
 * Класс описывающий страницу 'Результаты поиска по каталогу'.
 */
public final class SearchResultsPage extends BasePage<SearchResultsPage> {

    private static final SelenideElement RESULTS_HEADING = $x("//h1[normalize-space(.)='Результаты поиска']");
    private static final SelenideElement FOUND_INFO = $x("//*[contains(., 'Найдено товаров:') or contains(., 'Найдено')]");
    private static final ElementsCollection PRODUCT_CARDS = $$x("//div[@id='view-row']/div");
    private static final HeaderComponent HEADER = new HeaderComponent($("header, .site-header, .header"));

    public SearchResultsPage() {
        super("");
    }

    @Override
    protected SelenideElement pageIdentifier() {
        return RESULTS_HEADING;
    }

    @Override
    public SearchResultsPage shouldBeOpen() {
        super.shouldBeOpen();  // проверяет якорь + visible
        // Можно добавить дополнительную проверку, если нужно
        return this;
    }

    @Step("Проверяем, что найдено минимум {min} товаров")
    public SearchResultsPage shouldHaveAtLeastResults(int min) {
        PRODUCT_CARDS.shouldHave(sizeGreaterThanOrEqual(min));
        return this;
    }

    @Step("Проверяем, что отображается количество найденных товаров: {expectedCount}")
    public SearchResultsPage shouldShowFoundCount(int expectedCount) {
        FOUND_INFO.shouldHave(text("Найдено товаров: " + expectedCount));
        return this;
    }

    @Step("Открываем первую карточку товара из результатов")
    public SearchResultsPage openFirstProduct() {
        SelenideElement firstCard = PRODUCT_CARDS.first();
        // Уточни селектор ссылки на товар внутри карточки
        firstCard.$("a[href*='/product/'], a.product-link, .product-title a, a")
                .shouldBe(visible)
                .click();
        return this;
    }

    @Step("Открываем карточку товара под номером {index} (нумерация с 1)")
    public SearchResultsPage openProductByIndex(int index) {
        SelenideElement card = PRODUCT_CARDS.get(index - 1);
        card.$("a").shouldBe(visible).click();
        return this;
    }

    public HeaderComponent header() {
        return HEADER;
    }
}