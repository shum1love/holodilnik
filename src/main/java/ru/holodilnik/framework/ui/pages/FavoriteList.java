package ru.holodilnik.framework.ui.pages;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ru.holodilnik.framework.ui.locators.FavoriteListLocators;

/**
 * Класс описывающий страницу "Лист желаний".
 */
public class FavoriteList extends BasePage<FavoriteList> {

    @Step("Проверить, что название карточки товара содержит текст '{productName}'")
    public FavoriteList checkProductNameInCard(final String productName) {
        FavoriteListLocators.productName().shouldContainText(productName);
        return this;
    }

    @Step("Нажать на кнопку сердца и удалить товар из избранного")
    public FavoriteList removeProductFromFavorites() {
        FavoriteListLocators.removeFavoriteButton().click();
        Selenide.sleep(2000);
        Selenide.refresh();
        return this;
    }

    @SuppressWarnings("MethodMayBeStatic")
    @Step("Проверить пустую страницу Избранное")
    public void checkEmptyFavoriteList() {
        FavoriteListLocators.favoriteEmptyInfo().shouldContainText("Пока ничего нет");
    }

    @Step("Закрыть рекламный банер")
    public FavoriteList closeBannerAdvertising() {
        if (FavoriteListLocators.advertisingBannerClose().getSelenideElement().exists()) {
            FavoriteListLocators.advertisingBannerClose().click();
        }
        return this;
    }

    // ─── Helper methods ─────────────────────────────────

    public FavoriteList() {
        super("/favorites/");
    }

    @Override
    protected SelenideElement pageIdentifier() {
        return FavoriteListLocators.title().getSelenideElement();
    }

    @Override
    public FavoriteList shouldBeOpen() {
        super.shouldBeOpen();
        FavoriteListLocators.title()
                .shouldBeVisible()
                .shouldContainText("Избранное");
        return this;
    }
}
