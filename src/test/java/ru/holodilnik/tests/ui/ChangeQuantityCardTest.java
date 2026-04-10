package ru.holodilnik.tests.ui;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.holodilnik.framework.ui.pages.CartPage;
import ru.holodilnik.framework.ui.pages.MainPage;
import ru.holodilnik.tests.base.BaseTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Epic("Корзина")
@Feature("Изменение количества товара")
@Story("Проверка изменения количества товара в корзине")
@Owner("Rodion Shumilov")
@Severity(SeverityLevel.CRITICAL)
@Tag("UI")
@Tag("smoke")
@Tag("Cart")
public class ChangeQuantityCardTest extends BaseTest {
    private static final String REFRIGERATOR = "Двухкамерный холодильник";

    private static final  MainPage main = new MainPage();
    private static final CartPage cardPage = new CartPage();

    @Test
    @DisplayName("Пользователь может изменить количество товара в корзине")
    public void shouldChangeProductQuantityInCart() {



        int firstTotalPrice;

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
                .checkPriceAndOrderButton();

        firstTotalPrice = cardPage.getSummaryCost();

        cardPage
                .increaseQuantityOfItems()
                .cartCountShouldBe(2);

        assertEquals(firstTotalPrice * 2, cardPage.getSummaryCost());

        cardPage
                .reduceQuantityOfItems()
                .cartCountShouldBe(1);

        assertEquals(firstTotalPrice, cardPage.getSummaryCost());
    }
}
