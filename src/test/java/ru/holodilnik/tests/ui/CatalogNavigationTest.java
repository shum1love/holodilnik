package ru.holodilnik.tests.ui;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.holodilnik.framework.ui.pages.MainPage;

@Epic("Каталог")
@Feature("Поиск в каталоге")
@Story("Проверка поиска товара и открытия карточки товара")
@Owner("Rodion Shumilov")
@Severity(SeverityLevel.CRITICAL)
@Tag("UI")
@Tag("smoke")
@Tag("Catalog")
public class CatalogNavigationTest {

    private static final String REFRIGERATOR = "Двухкамерный холодильник";
    private static final String REFRIGERATOR_TITLE = "Холодильники и морозильники";
    private static final String DOUBLE_CHAMBER_REFRIGERATORS = "Двухкамерные холодильники";

    private static final MainPage main = new MainPage();


    @Test
    @DisplayName("Пользователь может найти и открыть карточку товара")
    public void searchTest() {
        main
                .open()
                .openCatalogMenu()
                .checkMainSections()
                .clickRefrigerator()
                .checkTitle(REFRIGERATOR_TITLE)
                .selectCategory(DOUBLE_CHAMBER_REFRIGERATORS)
                .openProductCard(1)
                .checkMainCardElements()
                .checkLowerSections()
                .checkPrintButton()
                .checkCardName(REFRIGERATOR);
    }
}
