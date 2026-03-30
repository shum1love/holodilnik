package ru.holodilnik.framework.ui.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ru.holodilnik.framework.ui.locators.CartLocators;
import ru.holodilnik.framework.ui.pages.components.HeaderComponent;

import static com.codeborne.selenide.Selenide.$;

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

    @Step("Увеличить количество товара в корзине")
    public CartPage increaseQuantityOfItems() {
        CartLocators.plusItem().click();
        return this;
    }

    @Step("Уменьшить количество товара в корзине")
    public CartPage reduceQuantityOfItems() {
        CartLocators.minusItem().click();
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

    @Step("Получить итоговую цену товара")
    public int getSummaryCost() {
        String text = CartLocators.totalSummeryCosts()
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
        super("https://www.holodilnik.ru/basket/");
    }

    private final HeaderComponent header = new HeaderComponent($("header.site-header, header, .b-header"));

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
