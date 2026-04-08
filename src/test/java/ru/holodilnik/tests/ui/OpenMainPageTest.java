package ru.holodilnik.tests.ui;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.holodilnik.framework.ui.pages.MainPage;
import ru.holodilnik.tests.base.BaseTest;

@Epic("Главная")
@Feature("Главная страница")
@Story("Проверка открытия главной страницы")
@Owner("Rodion Shumilov")
@Severity(SeverityLevel.CRITICAL)
@Tag("UI")
@Tag("smoke")
@Tag("Favorite")
public class OpenMainPageTest extends BaseTest {
    private static final MainPage main = new MainPage();

    @Test
    @DisplayName("Пользователь может удалить товар из раздела Избранное")
    public void removeFromFavoritesTest() {
        main
                .open()
                .checkHeaderVisible()
                .checkDownHeaderElements()
                .checkBannerVisibility()
                .checkMainElements()
                .scrollAndCheckFooterItems();
    }
}
