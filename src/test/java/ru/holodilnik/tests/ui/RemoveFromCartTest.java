package ru.holodilnik.tests.ui;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.holodilnik.framework.ui.pages.MainPage;
import ru.holodilnik.tests.base.BaseTest;

@Epic("Корзина")
@Feature("Удаление из корзины")
@Story("Проверка удаления товара из корзины")
@Owner("Rodion Shumilov")
@Severity(SeverityLevel.CRITICAL)
@Tag("UI")
@Tag("smoke")
@Tag("Cart")
public class RemoveFromCartTest extends BaseTest {
    private static final String REFRIGERATOR = "Двухкамерный холодильник";
    private final MainPage main = new MainPage();

    @Test
    @DisplayName("Пользователь может удалить товар из корзины")
    public void removeFromCartTest() {
        main
                .open()
                .checkHeaderVisible()
                .inputValueSearchField(REFRIGERATOR)
                .checkProductCardsArePresent()
                .addProductCard(1)
                .cartCountShouldBe(1)
                .goToCardPage()
                .checkItemInCard(REFRIGERATOR)
                .checkProductCounter()
                .checkPriceAndOrderButton()
                .clickDeleteButton()
                .checkEmptyCart();
    }
}
