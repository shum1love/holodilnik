package ru.holodilnik.framework.ui.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ru.holodilnik.framework.ui.locators.ProductCardLocators;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;

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
        //ProductCardLocators.productCode().shouldBeVisible();
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
    public ProductCardPage checkCardName(final String name) {
        ProductCardLocators.productTitle().shouldContainText(name);
        return this;
    }

    @Step("Получить название товара")
    public String getProductTitle() {
        return ProductCardLocators.productTitle().getText();
    }

    @Step("Получить цену товара")
    public String getPrice() {
        return ProductCardLocators.productPrice().getText();
    }

    @Step("Получить рейтинг товара")
    public String getRating() {
        return ProductCardLocators.productRating().getText();
    }

    @Step("Получить количество отзывов")
    public String getReviewsCount() {
        return ProductCardLocators.productReviews().getText();
    }

    @Step("Получить артикул/код товара")
    public String getProductCode() {
        return ProductCardLocators.productCode().getText();
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

    @Step("Перейти во вкладку 'Характеристики'")
    public ProductCardPage openSpecificationsTab() {
        ProductCardLocators.specificationsTab().click();
        return this;
    }

    @Step("Перейти во вкладку 'Отзывы'")
    public ProductCardPage openReviewsTab() {
        ProductCardLocators.reviewsTab().click();
        return this;
    }

    @Step("Проверить, что открыта карточка товара с названием '{expectedTitle}'")
    public ProductCardPage shouldHaveTitle(String expectedTitle) {
        ProductCardLocators.productTitle().shouldHaveExactText(expectedTitle);
        return this;
    }

    @Step("Закрыть рекламный банер, если он отображается")
    public ProductCardPage closeAdBannerIfDisplayed(){
        if (ProductCardLocators.advertisingBanner().getSelenideElement().isDisplayed()){
            ProductCardLocators.advertisingBannerCloseButton().click();
        }
        ProductCardLocators.advertisingBannerCloseButton().click();
        return this;
    }
}