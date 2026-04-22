package ru.holodilnik.framework.ui.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ru.holodilnik.framework.ui.locators.FavoriteListLocators;

/**
 * Класс описывающий страницу "Лист желаний".
 */
public final class FavoriteList extends BasePage<FavoriteList> {

    @Step("Проверить, что название карточки товара содержит текст '{productName}'")
    public FavoriteList checkProductNameInCard(final String productName) {
        FavoriteListLocators.productName().shouldContainText(productName);
        return this;
    }

    @Step("Нажать на кнопку сердца и удалить товар из избранного")
    public void removeProductFromFavorites() {
        FavoriteListLocators.removeFavoriteButton().click();
    }

    @SuppressWarnings("MethodMayBeStatic")
    @Step("Проверить пустую страницу Избранное")
    public void checkEmptyFavoriteList() {
        FavoriteListLocators.favoriteEmptyInfo().shouldContainText("Пока ничего нет");
    }

    @Step("Закрыть рекламный банер")
    public FavoriteList closeBannerAdvertising() {
        FavoriteListLocators.advertisingBannerClose().click();
        refreshPage();
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
