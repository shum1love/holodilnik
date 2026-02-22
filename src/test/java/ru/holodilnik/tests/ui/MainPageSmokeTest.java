package ru.holodilnik.tests.ui;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.holodilnik.framework.ui.pages.MainPage;
import ru.holodilnik.tests.base.BaseTest;

import static io.qameta.allure.Allure.step;

class MainPageSmokeTest extends BaseTest {

    @Tag("UI")
    @Tag("Smoke")
    @Test
    @DisplayName("Открытие главной страницы → видим логотип")
    void mainPageSmokeTest() {
        MainPage main = new MainPage();

        step("Открываем главную", main::open);
        step("Проверяем, что хедер доступен", main::checkHeaderVisible);
    }
}
