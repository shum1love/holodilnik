package ru.holodilnik.tests.ui;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.holodilnik.framework.ui.enums.FilterParameters;
import ru.holodilnik.framework.ui.pages.MainPage;
import ru.holodilnik.tests.base.BaseTest;

@Epic("Поиск")
@Feature("Фильтра поиска")
@Story("Проверка поиска товара c применением фильтров и сортировки")
@Owner("Rodion Shumilov")
@Severity(SeverityLevel.CRITICAL)
@Tag("UI")
@Tag("smoke")
@Tag("Search")
public class FilterSearchTest extends BaseTest {
    private static final String REFRIGERATOR = "Холодильник";
    private static final String DOUBLE_CHAMBER_REFRIGERATORS = "Двухкамерные холодильники";
    private static final String LIEBBHERR_FABRIC_NAME = "Liebherr";
    private final MainPage main = new MainPage();

    @Test
    @DisplayName("Пользователь может найти товар с применением фильтров и сортировки")
    public void filterSearchTest() {
        main
                .open()
                .checkMainElements()
                .inputValueSearchField(REFRIGERATOR)
                .checkProductCardsArePresent()
                .checkParameterBlock()
                .selectCategory(DOUBLE_CHAMBER_REFRIGERATORS)
                .setMinPrice(999)
                .setMaxPrice(40999)
                .selectFabric(LIEBBHERR_FABRIC_NAME)
                .clickShowButton()
                .sortByParam(FilterParameters.PRICE_HIGH_TO_LOW)
                .checkProductCardsArePresent();
    }
}
