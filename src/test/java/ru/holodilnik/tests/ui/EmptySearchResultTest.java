package ru.holodilnik.tests.ui;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.holodilnik.framework.ui.pages.MainPage;

@Epic("Поиск")
@Feature("Поиск товара")
@Story("Проверка надписи 'не найдено' при неудачном поиске")
@Owner("Rodion Shumilov")
@Severity(SeverityLevel.CRITICAL)
@Tag("UI")
@Tag("smoke")
@Tag("Search")
public class EmptySearchResultTest {
    private static final MainPage main = new MainPage();

    @Test
    @DisplayName("Пользователь видит запись 'не найдено' при неудачном поиске")
    public void emptySearchResultTest() {
        main
                .open()
                .checkMainElements()
                .inputValueSearchField("asdfghjkl")
                .checkNothingWasFound()
        ;
    }
}
