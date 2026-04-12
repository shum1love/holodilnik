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
@Tag("Main")
public class OpenMainPageTest extends BaseTest {
    private final MainPage main = new MainPage();

    @Test
    @DisplayName("Пользователь может открыть главную страницу")
    public void openMainPageTest() {
        main
                .open()
                .checkHeaderVisible()
                .checkDownHeaderElements()
                .checkBannerVisibility()
                .checkMainElements()
                .scrollAndCheckFooterItems();
    }
}
