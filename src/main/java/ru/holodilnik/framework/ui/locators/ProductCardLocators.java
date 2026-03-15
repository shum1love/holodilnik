package ru.holodilnik.framework.ui.locators;

import com.codeborne.selenide.SelenideElement;
import ru.holodilnik.framework.ui.elements.UiElement;

import static com.codeborne.selenide.Selenide.$;

/**
 * Локаторы страницы "Страница товара".
 */
public final class ProductCardLocators {

    public static UiElement productTitle() {
        return ui("Название товара", $(""));
    }

    public static UiElement productRating() {
        return ui("Рейтинг", $(""));
    }

    public static UiElement productReviews() {
        return ui("Отзывы", $(""));
    }

    public static UiElement productCode() {
        return ui("Код товара", $(""));
    }

    public static UiElement productPhoto() {
        return ui("Фото товара", $(""));
    }

    public static UiElement productPrice() {
        return ui("Цена товара", $(""));
    }

    public static UiElement addToCartButton() {
        return ui("Кнопка 'В корзину'", $(""));
    }

    public static UiElement addToFavoritesButton() {
        return ui("Кнопка добавить в избранное", $(""));
    }

    public static UiElement printButton() {
        return ui("Кнопка 'Распечатать'", $(""));
    }

    public static UiElement specificationsTab() {
        return ui("Вкладка 'Характеристики'", $(""));
    }

    public static UiElement reviewsTab() {
        return ui("Вкладка 'Отзывы'", $(""));
    }

    public static UiElement certificatesTab() {
        return ui("Вкладка 'Сертификаты'", $(""));
    }

    private static UiElement ui(String name, SelenideElement element) {
        return new UiElement(name, element);
    }

    private ProductCardLocators() {
        throw new AssertionError("Этот класс — только статические методы, экземпляры создавать нельзя");
    }
}