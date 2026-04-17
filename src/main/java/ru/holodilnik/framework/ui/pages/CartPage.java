package ru.holodilnik.framework.ui.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ru.holodilnik.framework.ui.locators.ShoppingCartLocators;
import ru.holodilnik.framework.ui.pages.components.HeaderComponent;

import static com.codeborne.selenide.Selenide.$;

/**
 * Класс описывающий страницу "Корзина".
 */
public final class CartPage extends BasePage<CartPage> {

    @Step("Проверить, что отображается товар с наименованием {name}")
    public CartPage checkItemInCard(final String name) {
        ShoppingCartLocators.itemName().shouldContainText(name);
        return this;
    }

    @Step("Проверить счётчик товаров в корзине")
    public CartPage checkProductCounter() {
        ShoppingCartLocators.minusItem().shouldBeVisible();
        ShoppingCartLocators.plusItem().shouldBeVisible();
        ShoppingCartLocators.productCounter().shouldBeVisible();
        return this;
    }

    @Step("Проверить элементы стоимости и кнопку оформления заказа")
    public CartPage checkPriceAndOrderButton() {
        ShoppingCartLocators.summaryPrices().shouldBeVisible();
        ShoppingCartLocators.orderButton().shouldBeEnabled();
        return this;
    }

    @Step("Увеличить количество товара в корзине")
    public CartPage increaseQuantityOfItems() {
        ShoppingCartLocators.plusItem().click();
        return this;
    }

    @Step("Уменьшить количество товара в корзине")
    public CartPage reduceQuantityOfItems() {
        ShoppingCartLocators.minusItem().click();
        return this;
    }

    @Step("Нажать кнопку удалить товар из корзины")
    public CartPage clickDeleteButton() {
        ShoppingCartLocators.deleteButton().click();
        return this;
    }

    @Step("Проверить отображение надписи 'Корзина пуста' и отсутствие товара")
    public void checkEmptyCart() {
        ShoppingCartLocators.emptyCart().shouldBeVisible();
        ShoppingCartLocators.itemName().shouldNotBeVisible();
    }

    @Step("Получить итоговую цену товара")
    public int getSummaryCost() {
        final String text = ShoppingCartLocators.totalSummeryCosts()
                .getText()
                .replaceAll("[^0-9]", "");

        return Integer.parseInt(text);
    }

    @Step("Счётчик корзины показывает {expectedCount} товаров")
    public void cartCountShouldBe(final int expectedCount) {
        header.cartCountShouldBe(expectedCount);
    }

    // ─── Helper methods ─────────────────────────────────

    public CartPage() {
        super("/basket/");
    }

    private final HeaderComponent header = new HeaderComponent($("header.site-header, header, .b-header"));

    @Override
    protected SelenideElement pageIdentifier() {
        return ShoppingCartLocators.title().getSelenideElement();
    }

    @Override
    public CartPage shouldBeOpen() {
        super.shouldBeOpen();
        ShoppingCartLocators.title()
                .shouldBeVisible()
                .shouldContainText("Корзина");
        return this;
    }
}
