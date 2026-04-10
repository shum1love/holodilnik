package ru.holodilnik.tests.ui;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.holodilnik.framework.ui.pages.MainPage;

@Epic("Главная страница")
@Feature("Рекламный банер")
@Story("Проверка видимости главного рекламного банера")
@Owner("Rodion Shumilov")
@Severity(SeverityLevel.CRITICAL)
@Tag("UI")
@Tag("smoke")
@Tag("Banner")
public class BannerTest {

    private static final MainPage main = new MainPage();

    @Test
    @DisplayName("Пользователь видит запись 'не найдено' при неудачном поиске")
    public void bannerVisibilityTest() {
        main
                .open()
                .checkMainElements()
                .checkBannerVisibility();
    }
}
