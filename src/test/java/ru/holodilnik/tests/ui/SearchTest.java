package ru.holodilnik.tests.ui;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.holodilnik.framework.ui.pages.MainPage;
import ru.holodilnik.tests.base.BaseTest;

@Epic("Каталог")
@Feature("Поиск в каталоге")
@Story("Проверка поиска товара и открытия карточки товара")
@Owner("Rodion Shumilov")
@Severity(SeverityLevel.CRITICAL)
@Tag("UI")
@Tag("smoke")
@Tag("Search")
public class SearchTest extends BaseTest {
    private static final String REFRIGERATOR_ATLANT = "Двухкамерный холодильник Atlant";

    MainPage main = new MainPage();

    @Test
    @DisplayName("Пользователь может найти и открыть карточку товара")
    public void searchTest() {
        main
                .open()
                .checkHeaderVisible()
                .inputValueSearchField(REFRIGERATOR_ATLANT)
                .checkProductCardsArePresent()
                .openProductCard(1)
                .checkMainCardElements()
                .checkLowerSections()
                .checkPrintButton()
                .checkCardName(REFRIGERATOR_ATLANT);
    }
}
