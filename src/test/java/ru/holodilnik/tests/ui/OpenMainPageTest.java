package ru.holodilnik.tests.ui;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.holodilnik.framework.ui.pages.MainPage;

@Epic("Главная")
@Feature("Главная страница")
@Story("Проверка открытия главной страницы")
@Owner("Rodion Shumilov")
@Severity(SeverityLevel.CRITICAL)
@Tag("UI")
@Tag("smoke")
@Tag("Favorite")
public class OpenMainPageTest {

    private static final MainPage main = new MainPage();

    @Test
    @DisplayName("Пользователь может удалить товар из раздела Избранное")
    public void removeFromFavoritesTest() {
        main
                .open()
                .checkHeaderVisible()
        // TODO: Видимость банера
        // TODO: Кнопка техПоддержки
        // TODO: Раздел новинки
        // TODO: скролл до футтера сайта вниз
                ;
    }
}
