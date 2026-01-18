package ru.holodilnik.framework.ui.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import ru.holodilnik.framework.ui.pages.components.HeaderComponent; // если хедер везде

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThanOrEqual;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$    ;

public final class SearchResultsPage extends BasePage<SearchResultsPage> {

    private final SelenideElement searchTitle = $("h1");
    private final ElementsCollection productCards = $$(".product-card"); // карточки товаров

    private final HeaderComponent header = new HeaderComponent($("header, .site-header"));

    public SearchResultsPage() {
        super(""); // relativePath пустой, т.к. URL динамический после поиска
    }

    @Override
    protected SelenideElement pageIdentifier() {
        return searchTitle; // или первый продукт: productCards.first()
    }

    // Бизнес-методы
    public SearchResultsPage shouldHaveResultsCountAtLeast(int min) {
        productCards.shouldHave(sizeGreaterThanOrEqual(min));
        return this;
    }

    /*public ProductPage openFirstProduct() {
        productCards.first().$("a").click();
        return new ProductPage();
    }*/

    public HeaderComponent header() {
        return header;
    }
}