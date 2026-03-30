package ru.holodilnik.tests.ui;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.holodilnik.framework.ui.pages.CartPage;
import ru.holodilnik.framework.ui.pages.MainPage;
import ru.holodilnik.framework.ui.pages.SearchResultsPage;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Epic("Корзина")
@Feature("Изменение количества товара")
@Story("Проверка изменения количества товара в корзине")
@Owner("Rodion Shumilov")
@Severity(SeverityLevel.CRITICAL)
@Tag("UI")
@Tag("smoke")
@Tag("Cart")
public class ChangeQuantityCardTest {
    private static final String REFRIGERATOR = "Двухкамерный холодильник";


    @Test
    @DisplayName("Пользователь может изменить количество товара в корзине")
    public void changeQuantityCardTest() {

        final MainPage main = new MainPage();
        final SearchResultsPage searchResultPage = new SearchResultsPage();
        final CartPage cardPage = new CartPage();

        int firstTotalPrice = 0;
        int secondTotalPrice = 0;

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

        cardPage.increaseQuantityOfItems();

        searchResultPage.cartCountShouldBe(2);

        secondTotalPrice = cardPage.getSummaryCost();

        assertEquals(firstTotalPrice * 2, secondTotalPrice);

        cardPage.reduceQuantityOfItems();

        searchResultPage.cartCountShouldBe(1);

        secondTotalPrice = cardPage.getSummaryCost();

        assertEquals(firstTotalPrice, secondTotalPrice);
    }
}
