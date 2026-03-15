package ru.holodilnik.framework.ui.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ru.holodilnik.framework.ui.pages.components.HeaderComponent;
import ru.holodilnik.framework.ui.locators.ShoppingCartLocators;

import static com.codeborne.selenide.Selenide.$;

public final class ShoppingCardPage extends BasePage<ShoppingCardPage> {

    private final HeaderComponent header =
            new HeaderComponent($("header.site-header, header, .b-header"));

    public ShoppingCardPage() {
        super("/cart");
    }

    @Override
    protected SelenideElement pageIdentifier() {
        return ShoppingCartLocators.pageTitle().getSelenideElement();
    }

    @Override
    public ShoppingCardPage shouldBeOpen() {
        super.shouldBeOpen();
        ShoppingCartLocators.pageTitle()
                .shouldBeVisible()
                .shouldContainText("Корзина");
        return this;
    }

    // ─── Business actions ─────────────────────────────────

    @Step("Счётчик корзины показывает {expectedCount} товаров")
    public ShoppingCardPage cartCountShouldBe(int expectedCount) {
        header.cartCountShouldBe(expectedCount);
        return this;
    }
}
