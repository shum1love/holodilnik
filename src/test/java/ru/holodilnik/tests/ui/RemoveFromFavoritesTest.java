package ru.holodilnik.tests.ui;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.holodilnik.framework.ui.pages.FavoriteList;
import ru.holodilnik.framework.ui.pages.MainPage;
import ru.holodilnik.framework.ui.pages.SearchResultsPage;
import ru.holodilnik.tests.base.BaseTest;

@Epic("Избранное")
@Feature("Удаление из избранного")
@Story("Проверка удаления товара из избранного")
@Owner("Rodion Shumilov")
@Severity(SeverityLevel.CRITICAL)
@Tag("UI")
@Tag("smoke")
@Tag("Favorite")
public class RemoveFromFavoritesTest extends BaseTest {
    private static final String REFRIGERATOR = "Двухкамерный холодильник";

    private final MainPage main = new MainPage();
    private final SearchResultsPage searchResultsPage = new SearchResultsPage();
    private final FavoriteList favoriteList = new FavoriteList();

    @Test
    @DisplayName("Пользователь может удалить товар из раздела Избранное")
    public void removeFromFavoritesTest() {
        main
                .open()
                .checkHeaderVisible()
                .inputValueSearchField(REFRIGERATOR);

        searchResultsPage
                .checkProductCardsArePresent()
                .addFavoriteList(1)
                .favoriteListCountShouldBe(1)
                .goToFavoriteList()
                .checkProductNameInCard(REFRIGERATOR)
                .removeProductFromFavorites();

        favoriteList
                .closeBannerAdvertising()
                .checkEmptyFavoriteList();

        searchResultsPage.favoriteListCountShouldBe(null);
    }
}
