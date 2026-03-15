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
public class SearchTest {

    private static final String REFRIGERATOR_ATLANT = "холодильник атлант двухкамерный";

    MainPage main = new MainPage();

    @Test
    @DisplayName("Пользователь может найти и открыть карточку товара")
    public void searchTest() {
        main.open()
                .clickSearchButton()
                .checkHeaderVisible()
                .inputValueSearchField("REFRIGERATOR_ATLANT")
                .checkProductCardsArePresent()
                .openProductCard(1)

                .addProductCard(1)
                .cartCountShouldBe(1)
        ;
    }
}
