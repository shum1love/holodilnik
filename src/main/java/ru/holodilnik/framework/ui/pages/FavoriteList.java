package ru.holodilnik.framework.ui.pages;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ru.holodilnik.framework.ui.locators.FavoriteListLocators;
import ru.holodilnik.framework.ui.pages.components.HeaderComponent;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.Wait;

/**
 * Класс описывающий страницу "Лист желаний".
 */
public class FavoriteList extends BasePage<FavoriteList> {

    @Step("Проверить, что у раздела 'Избранное' счётчик товаров равен {count}")
    public FavoriteList checkFavoriteCount(final int expectedCount) {
        FavoriteListLocators.favoriteMenuCounter().shouldHaveExactText(String.valueOf(expectedCount));
        return this;
    }

    @Step("Проверить, что название карточки товара содержит текст '{productName}'")
    public FavoriteList checkProductNameInCard(final String productName){
        FavoriteListLocators.productName().shouldContainText(productName);
        return this;
    }

    @Step("Нажать на кнопку сердца и удалить товар из избранного")
    public FavoriteList removeProductFromFavorites(){
        FavoriteListLocators.removeFavoriteButton().click();
        Selenide.sleep(2000);
        Selenide.refresh();
        return this;
    }

    @Step("Проверить пустую страницу Избранное")
    public FavoriteList checkEmptyFavoriteList(){
        FavoriteListLocators.favoriteEmptyInfo().shouldContainText("Пока ничего нет");
        return this;
    }

    @Step("Закрыть рекламный банер")
    public FavoriteList closeBannerAdvertising (){
        FavoriteListLocators.advertisingBannerClose().click();
        return this;
    }

    // ─── Helper methods ─────────────────────────────────

    public FavoriteList() {
        super("https://www.holodilnik.ru/basket/");
    }

    private final HeaderComponent header = new HeaderComponent($("header.site-header, header, .b-header"));

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
