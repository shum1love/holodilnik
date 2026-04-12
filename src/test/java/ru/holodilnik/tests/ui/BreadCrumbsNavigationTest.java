package ru.holodilnik.tests.ui;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.holodilnik.framework.ui.pages.CatalogueMenuPage;
import ru.holodilnik.framework.ui.pages.MainPage;
import ru.holodilnik.framework.ui.pages.SearchResultsPage;
import ru.holodilnik.tests.base.BaseTest;

@Epic("Каталог")
@Feature("Навигация по каталогу")
@Story("Проверка навигации по хлебным крошкам")
@Owner("Rodion Shumilov")
@Severity(SeverityLevel.CRITICAL)
@Tag("UI")
@Tag("smoke")
@Tag("Catalog")
public class BreadCrumbsNavigationTest extends BaseTest {
    private static final String REFRIGERATOR = "Двухкамерный холодильник";
    private static final String REFRIGERATOR_TITLE = "Холодильники и морозильники";
    private static final String DOUBLE_CHAMBER_REFRIGERATORS = "Двухкамерные холодильники";

    private static final MainPage main = new MainPage();
    private static final SearchResultsPage searchResultPage = new SearchResultsPage();
    private static final CatalogueMenuPage catalogueMenuPage = new CatalogueMenuPage();

    @Test
    @DisplayName("Пользователь может перемещаться по хлебным крошкам каталога")
    public void addToFavoritesTest() {
        main
                .open()
                .checkHeaderVisible()
                .openCatalogMenu()
                .checkMainSections()
                .clickRefrigerator()
                .checkTitle(REFRIGERATOR_TITLE)
                .selectCategory(DOUBLE_CHAMBER_REFRIGERATORS)
                .checkProductCardsArePresent()
                .openProductCard(1)
                .checkCardName(REFRIGERATOR)
                .closeAdBanner();

        searchResultPage
                .clickBreadcrumb(DOUBLE_CHAMBER_REFRIGERATORS)
                .checkProductCardsArePresent()
                .clickBreadcrumb(REFRIGERATOR_TITLE);

        catalogueMenuPage.checkTitle(REFRIGERATOR_TITLE);

        searchResultPage.clickBreadcrumb("Главная");

        main.checkMainElements();
    }
}
