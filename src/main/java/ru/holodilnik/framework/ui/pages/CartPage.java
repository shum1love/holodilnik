package ru.holodilnik.framework.ui.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ru.holodilnik.framework.ui.locators.CartLocators;

/**
 * Класс описывающий страницу "Корзина".
 */
public class CartPage extends BasePage<CartPage> {

    @Step("Проверить, что отображается товар с наименованием {name}")
    public CartPage checkItemInCard(final String name) {
        CartLocators.itemName().shouldContainText(name);
        return this;
    }

    @Step("Проверить счётчик товаров в корзине")
    public CartPage checkProductCounter() {
        CartLocators.minusItem().shouldBeVisible();
        CartLocators.plusItem().shouldBeVisible();
        CartLocators.productCounter().shouldBeVisible();
        return this;
    }

    @Step("Проверить элементы стоимости и кнопку оформления заказа")
    public CartPage checkPriceAndOrderButton() {
        CartLocators.summaryPrices().shouldBeVisible();
        CartLocators.orderButton().shouldBeEnabled();
        return this;
    }

    @Step("Нажать кнопку удалить товар из корзины")
    public CartPage clickDeleteButton() {
        CartLocators.deleteButton().click();
        return this;
    }

    @Step("Проверить отображение надписи 'Корзина пуста' и отсутствие товара")
    public void checkEmptyCart() {
        CartLocators.emptyCart().shouldBeVisible();
        CartLocators.itemName().shouldNotBeVisible();
    }

    // ─── Helper methods ─────────────────────────────────

    public CartPage() {
        super("https://www.holodilnik.ru/basket/");
    }

    @Override
    protected SelenideElement pageIdentifier() {
        return CartLocators.title().getSelenideElement();
    }

    @Override
    public CartPage shouldBeOpen() {
        super.shouldBeOpen();
        CartLocators.title()
                .shouldBeVisible()
                .shouldContainText("Корзина");
        return this;
    }
}
