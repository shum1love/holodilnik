package ru.holodilnik.framework.ui.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import ru.holodilnik.framework.ui.pages.components.HeaderComponent;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThanOrEqual;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
public final class SearchResultsPage extends BasePage<SearchResultsPage> {

    // Якорь страницы — заголовок "Результаты поиска"
    private final SelenideElement resultsHeading = $x("//h1[normalize-space(.)='Результаты поиска']");

    // Блок с количеством найденных товаров (лучше найти реальный класс/id в DevTools)
    private final SelenideElement foundInfo = $x("//*[contains(., 'Найдено товаров:') or contains(., 'Найдено')]");

    // Коллекция карточек товаров — старайся заменить на CSS-класс, если есть (например .product-item, .catalog-product)
    private final ElementsCollection productCards = $$x("//div[@id='view-row']/div");

    private final HeaderComponent header = new HeaderComponent($("header, .site-header, .header"));

    public SearchResultsPage() {
        super("");
    }

    @Override
    protected SelenideElement pageIdentifier() {
        return resultsHeading;
    }

    @Override
    public SearchResultsPage shouldBeOpen() {
        super.shouldBeOpen();  // проверяет якорь + visible
        // Можно добавить дополнительную проверку, если нужно
        return this;
    }

    @Step("Проверяем, что найдено минимум {min} товаров")
    public SearchResultsPage shouldHaveAtLeastResults(int min) {
        productCards.shouldHave(sizeGreaterThanOrEqual(min));
        return this;
    }

    @Step("Проверяем, что отображается количество найденных товаров: {expectedCount}")
    public SearchResultsPage shouldShowFoundCount(int expectedCount) {
        foundInfo.shouldHave(text("Найдено товаров: " + expectedCount));
        return this;
    }

    @Step("Открываем первую карточку товара из результатов")
    public ProductPage openFirstProduct() {
        SelenideElement firstCard = productCards.first();
        // Уточни селектор ссылки на товар внутри карточки
        firstCard.$("a[href*='/product/'], a.product-link, .product-title a, a")
                .shouldBe(visible)
                .click();
        return new ProductPage();
    }

    @Step("Открываем карточку товара под номером {index} (нумерация с 1)")
    public ProductPage openProductByIndex(int index) {
        SelenideElement card = productCards.get(index - 1);
        card.$("a").shouldBe(visible).click();
        return new ProductPage();
    }

    public HeaderComponent header() {
        return header;
    }
}