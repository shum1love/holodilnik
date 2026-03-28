package ru.holodilnik.framework.ui.locators;

import com.codeborne.selenide.SelenideElement;
import ru.holodilnik.framework.ui.elements.UiElement;

import static com.codeborne.selenide.Selenide.$;

/**
 * Локаторы страницы "Корзина".
 */
public class CartLocators {

    public static UiElement title() {
        return ui("Заголовок раздела 'Корзина'", $("div.basket__header-title"));
    }

    public static UiElement itemName() {
        return ui("Наименование товара", $("a.basket-item__name-value.link.link_inherit"));
    }

    public static UiElement minusItem() {
        return ui("Уменьшить кол-во товара", $("span[data-smoke='btn-minus__widget-quantity']"));
    }

    public static UiElement plusItem() {
        return ui("Увеличить кол-во товара", $("span[data-smoke='btn-plus__widget-quantity']"));
    }

    public static UiElement productCounter() {
        return ui("Счетчик товара", $("input[data-smoke='CartItemAmountInput']"));
    }

    public static UiElement summaryPrices() {
        return ui("Элементы стоимости товара", $("div.basket-summary__prices"));
    }

    public static UiElement orderButton() {
        return ui("Кнопка Перейти к оформлению", $("a[data-smoke='GoOrderBtn']"));
    }

    private static UiElement ui(String name, SelenideElement element) {
        return new UiElement(name, element);
    }

    private CartLocators() {
        throw new AssertionError("Этот класс — только статические методы, экземпляры создавать нельзя");
    }
}
