package ru.holodilnik.tests.ui;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.holodilnik.framework.ui.pages.MainPage;

@Epic("Избранное")
@Feature("Добавление в избранное")
@Story("Проверка добавления товара в корзину")
@Owner("Rodion Shumilov")
@Severity(SeverityLevel.CRITICAL)
@Tag("UI")
@Tag("smoke")
@Tag("Favorite")
public class AddToFavoritesTest {

    private static final String REFRIGERATOR = "Двухкамерный холодильник";
    private static final MainPage main = new MainPage();

    @Test
    @DisplayName("Пользователь может найти найти товар и добавить его в корзину")
    public void addToFavoritesTest() {
        main
                .open()
                .checkHeaderVisible()
                .inputValueSearchField(REFRIGERATOR)
                .checkProductCardsArePresent()
                .addProductCard(1)
                .favoriteListCountShouldBe(1)
                .goToFavoriteList()

                .checkItemInCard(REFRIGERATOR)
                .checkProductCounter()
                .checkPriceAndOrderButton();
    }
}
