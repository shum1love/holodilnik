package ru.holodilnik.framework.ui.locators;

import com.codeborne.selenide.SelenideElement;
import ru.holodilnik.framework.ui.elements.UiElement;

import static com.codeborne.selenide.Selenide.$;

/**
 * Локаторы главной страницы "Холодильник.ру".
 */
public final class MainPageLocators {

    public static UiElement logo() {
        return ui("Логотип 'Холодильник.ру'", $("span.site-header__logo-brand"));
    }


    private static UiElement ui(String name, SelenideElement element) {
        return new UiElement(name, element);
    }
    private MainPageLocators() {
        throw new AssertionError("Этот класс — только статические методы, экземпляры создавать нельзя");
    }
}
