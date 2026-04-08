package ru.holodilnik.tests.ui;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.holodilnik.framework.ui.pages.MainPage;
import ru.holodilnik.tests.base.BaseTest;

@Epic("Поиск")
@Feature("Фильтра поиска")
@Story("Проверка поиска товара c применением фильтров и сортировки")
@Owner("Rodion Shumilov")
@Severity(SeverityLevel.CRITICAL)
@Tag("UI")
@Tag("smoke")
@Tag("Favorite")
public class FilterSearchTest extends BaseTest {

    private static final String REFRIGERATOR = "Двухкамерный холодильник";
    private static final MainPage main = new MainPage();

    @Test
    @DisplayName("Пользователь может найти товар с применением фильтров и сортировки")
    public void removeFromFavoritesTest() {
        main
                .open()
                .checkMainElements()
                .inputValueSearchField(REFRIGERATOR)
                .checkProductCardsArePresent()
                .checkParameterBlock()
                .selectCategory(REFRIGERATOR)
                .clickShowButton()
                ;
    }
}
