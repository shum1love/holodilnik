package ru.holodilnik.framework.ui.locators;

import com.codeborne.selenide.SelenideElement;
import ru.holodilnik.framework.ui.elements.UiElement;

import static com.codeborne.selenide.Selenide.$;

/**
 * Локаторы страницы "Страница товара".
 */
public final class ProductCardLocators {

    public static UiElement productTitle() {
        return ui("Название товара", $("h1"));
    }

    public static UiElement productRating() {
        return ui("Рейтинг", $("span[class='widget-rating__value']"));
    }

    public static UiElement productReviews() {
        return ui("Отзывы", $("span.widget-rating__count"));
    }

    public static UiElement productCode() {
        return ui("Код товара", $("div[class='widget-copy__value']:last-of-type"));
    }

    public static UiElement productPhoto() {
        return ui("Фото товара", $("div.card-product-img__body"));
    }

    public static UiElement productPrice() {
        return ui("Цена товара", $("div.product-price"));
    }

    public static UiElement addToCartButton() {
        return ui("Кнопка 'В корзину'", $("a[data-smoke='btn-buy__product-old']:first-of-type"));
    }

    public static UiElement addToFavoritesButton() {
        return ui("Кнопка добавить в избранное", $("span[data-action-button='favorite']:first-of-type"));
    }

    public static UiElement printButton() {
        return ui("Кнопка 'Распечатать'", $("div.print-out"));
    }

    public static UiElement specificationsTab() {
        return ui("Вкладка 'Характеристики'", $("a[aria-controls='item-description']"));
    }

    public static UiElement reviewsTab() {
        return ui("Вкладка 'Отзывы'", $("a[aria-controls='item-reviews']"));
    }

    public static UiElement certificatesTab() {
        return ui("Вкладка 'Сертификаты'", $("a[aria-controls='item-certificates']"));
    }

    public static UiElement advertisingBanner() {
        return ui("Рекламный банер", $("div.a-popup"));
    }

    public static UiElement advertisingBannerCloseButton() {
        return ui("Кнопка закрыть для рекламного банера", $("div.ap-close"));
    }

    // ─── Helper methods ─────────────────────────────────
    private static UiElement ui(String name, SelenideElement element) {
        return new UiElement(name, element);
    }

    private ProductCardLocators() {
        throw new AssertionError("Этот класс — только статические методы, экземпляры создавать нельзя");
    }
}