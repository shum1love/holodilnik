package ru.holodilnik.tests.ui;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.holodilnik.framework.ui.pages.MainPage;

@Epic("Корзина")
@Feature("Добавление в корзину")
@Story("Проверка добавления товара в корзину")
@Owner("Rodion Shumilov")
@Severity(SeverityLevel.CRITICAL)
@Tag("UI")
@Tag("smoke")
@Tag("Cart")
public class AddToCartTest {

    private static final String REFRIGERATOR = "Двухкамерный холодильник";
    private static final MainPage main = new MainPage();
    @Test
    @DisplayName("Пользователь может найти и открыть карточку товара")
    public void searchTest() {
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
        ;
    }
}
