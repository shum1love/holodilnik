package ru.holodilnik.tests.ui;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.holodilnik.framework.ui.pages.MainPage;

@Epic("Корзина")
@Feature("Добавление товара в корзину")
@Story("Добавление одного товара с главной страницы через кнопку «В корзину»")
@Owner("Rodion Shumilov")
@Severity(SeverityLevel.CRITICAL)
@Tag("UI")
@Tag("Smoke")
@Tag("Cart")
public class AddingItemCartTest {
    MainPage main = new MainPage();

    @Test
    @DisplayName("Пользователь может добавить товар в корзину с главной страницы")
    public void addingItemCartTest() {
        main
                .open()
                .search("машина")
                .selectCategory("Стиральные машины ")
        // TODO:
        ;
    }
}
