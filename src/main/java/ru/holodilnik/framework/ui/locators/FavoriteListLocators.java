package ru.holodilnik.framework.ui.locators;

import com.codeborne.selenide.SelenideElement;
import ru.holodilnik.framework.ui.elements.UiElement;

import static com.codeborne.selenide.Selenide.$;

/**
 * Локаторы страницы "Избранное".
 */
public final class FavoriteListLocators {

    public static UiElement title() {
        return ui("Заголовок раздела 'Избранное'", $("h1"));
    }

    public static UiElement productName() {
        return ui("Имя товара в карточке", $("span[class*='product-name__category']"));
    }

    public static UiElement removeFavoriteButton() {
        return ui("Удалить товар из Избранного", $("span[data-action-button='favorite']"));
    }

    public static UiElement favoriteEmptyInfo() {
        return ui("Информация о пустом разделе 'Избранное'", $("div.favorites-empty__info"));
    }

    public static UiElement advertisingBannerClose() {
        return ui("Кнопка закрытия рекламного банера", $("div.ap-close"));
    }

    // ─── Helper methods ─────────────────────────────────
    private static UiElement ui(final String name, final SelenideElement element) {
        return new UiElement(name, element);
    }

    private FavoriteListLocators() {
        throw new AssertionError("Этот класс — только статические методы, экземпляры создавать нельзя");
    }
}
