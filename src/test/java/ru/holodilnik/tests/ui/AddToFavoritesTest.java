package ru.holodilnik.tests.ui;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.holodilnik.framework.ui.pages.MainPage;
import ru.holodilnik.tests.base.BaseTest;

@Epic("Избранное")
@Feature("Добавление в избранное")
@Story("Проверка добавления товара в Избранное")
@Owner("Rodion Shumilov")
@Severity(SeverityLevel.CRITICAL)
@Tag("UI")
@Tag("smoke")
@Tag("Favorite")
public class AddToFavoritesTest extends BaseTest {
    private static final String REFRIGERATOR = "Двухкамерный холодильник";

    private final MainPage main = new MainPage();

    @Test
    @DisplayName("Пользователь может найти товар и добавить его в Избранное")
    public void addToFavoritesTest() {
        main
                .open()
                .checkHeaderVisible()
                .inputValueSearchField(REFRIGERATOR)
                .checkProductCardsArePresent()
                .addFavoriteList(1)
                .favoriteListCountShouldBe(1)
                .goToFavoriteList()
                .checkProductNameInCard(REFRIGERATOR);
    }
}
