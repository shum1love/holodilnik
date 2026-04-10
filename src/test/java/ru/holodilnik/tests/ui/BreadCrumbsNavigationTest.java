package ru.holodilnik.tests.ui;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.holodilnik.framework.ui.pages.MainPage;

@Epic("Каталог")
@Feature("Навигация по каталогу")
@Story("Проверка навигации по хлебным крошкам")
@Owner("Rodion Shumilov")
@Severity(SeverityLevel.CRITICAL)
@Tag("UI")
@Tag("smoke")
@Tag("Catalog")
public class BreadCrumbsNavigationTest {
    private static final String REFRIGERATOR = "Двухкамерный холодильник";
    private static final String REFRIGERATOR_TITLE = "Холодильники и морозильники";
    private static final String DOUBLE_CHAMBER_REFRIGERATORS = "Двухкамерные холодильники";
    private static final MainPage main = new MainPage();

    @Test
    @DisplayName("Пользователь может найти найти товар и добавить его в Избранное")
    public void addToFavoritesTest() {
        main
                .open()
                .checkHeaderVisible()
                .openCatalogMenu()
                .checkMainSections()
                .clickRefrigerator()
                .checkTitle(REFRIGERATOR_TITLE)
                .selectCategory(DOUBLE_CHAMBER_REFRIGERATORS)
                .openProductCard(1)
                .checkCardName(REFRIGERATOR);
                ;
    }
}
