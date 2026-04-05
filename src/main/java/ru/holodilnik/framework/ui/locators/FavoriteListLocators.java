package ru.holodilnik.framework.ui.locators;

import com.codeborne.selenide.SelenideElement;
import ru.holodilnik.framework.ui.elements.UiElement;

import static com.codeborne.selenide.Selenide.$;

/**
 * Локаторы страницы "Лист желаний".
 */
public final class FavoriteListLocators {

    public static UiElement title() {
        return ui("Заголовок раздела 'Избранное'", $("span.cabinet-navigation__link_state_active"));
    }
    public static UiElement favoriteMenuCounter() {
        return ui("Счётчик в разделе меню 'Избранное'", $("#favorite_products_count_menu"));
    }
    // ─── Helper methods ─────────────────────────────────
    private static UiElement ui(String name, SelenideElement element) {
        return new UiElement(name, element);
    }

    private FavoriteListLocators() {
        throw new AssertionError("Этот класс — только статические методы, экземпляры создавать нельзя");
    }
}
