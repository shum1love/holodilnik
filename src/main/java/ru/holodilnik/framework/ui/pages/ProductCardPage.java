package ru.holodilnik.framework.ui.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ru.holodilnik.framework.ui.locators.ProductCardLocators;

/**
 * Класс описывающий страницу "Страница товара".
 */
public final class ProductCardPage extends BasePage<ProductCardPage> {

    public ProductCardPage() {
        super("");
    }

    @Override
    protected SelenideElement pageIdentifier() {
        return ProductCardLocators.productTitle().getSelenideElement();
    }

    @Override
    public ProductCardPage shouldBeOpen() {
        super.shouldBeOpen();
        ProductCardLocators.productTitle().shouldBeVisible();
        return this;
    }

    // ─── Business actions & checks ─────────────────────────────────

    @Step("Проверить основные элементы карточки товара")
    public ProductCardPage checkMainCardElements() {
        ProductCardLocators.productTitle().shouldBeVisible();
        ProductCardLocators.productRating().shouldBeVisible();
        ProductCardLocators.productReviews().shouldBeVisible();
        ProductCardLocators.productPhoto().shouldBeVisible();
        ProductCardLocators.productPrice().shouldBeVisible();
        ProductCardLocators.addToCartButton().shouldBeEnabled();
        ProductCardLocators.addToFavoritesButton().shouldBeEnabled();
        return this;
    }

    @Step("Проверить доступность 3х нижних секций")
    public ProductCardPage checkLowerSections() {
        ProductCardLocators.specificationsTab().shouldBeEnabled();
        ProductCardLocators.reviewsTab().shouldBeEnabled();
        ProductCardLocators.certificatesTab().shouldBeEnabled();
        return this;
    }

    @Step("Проверить кнопку 'Распечатать'")
    public ProductCardPage checkPrintButton() {
        ProductCardLocators.printButton().shouldBeEnabled();
        return this;
    }

    @Step("Проверить, что название товара содержит {name}")
    public void checkCardName(final String name) {
        ProductCardLocators.productTitle().shouldContainText(name);
    }

    @Step("Получить цену товара")
    public String getPrice() {
        return ProductCardLocators.productPrice().getText();
    }

    @Step("Получить рейтинг товара")
    public String getRating() {
        return ProductCardLocators.productRating().getText();
    }

    @Step("Нажать 'В корзину'")
    public ProductCardPage addToCart() {
        ProductCardLocators.addToCartButton().click();
        return this;
    }

    @Step("Нажать 'В избранное'")
    public ProductCardPage addToFavorites() {
        ProductCardLocators.addToFavoritesButton().click();
        return this;
    }

    @Step("Закрыть рекламный банер, если он отображается")
    public ProductCardPage closeAdBanner() {
        ProductCardLocators.advertisingBannerCloseButton().click();
        return this;
    }
}