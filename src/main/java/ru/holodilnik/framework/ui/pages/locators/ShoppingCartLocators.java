package ru.holodilnik.framework.ui.pages.locators;

import com.codeborne.selenide.SelenideElement;
import ru.holodilnik.framework.ui.elements.UiElement;

import static com.codeborne.selenide.Selenide.$;

/**
 * Локаторы страницы "Корзина".
 */
public final class ShoppingCartLocators {

    public static UiElement pageTitle() {
        return ui("Заголовок страницы корзины", $("h1"));
    }

    // ─── helpers ─────────────────────────────────

    private static UiElement ui(String name, SelenideElement element) {
        return new UiElement(name, element);
    }

    private ShoppingCartLocators() {
        throw new AssertionError("Этот класс — только статические методы, экземпляры создавать нельзя");
    }
}
