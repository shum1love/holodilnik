package ru.holodilnik.framework.ui.locators;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import ru.holodilnik.framework.ui.elements.UiElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

/**
 * Локаторы главной страницы "Холодильник.ру".
 */
public final class MainPageLocators {

    public static UiElement logo() {
        return ui("Логотип 'Холодильник.ру'", $("span.site-header__logo-brand"));
    }
    public static UiElement banner() {
        return ui("Логотип 'Холодильник.ру'", $$("a.promo-box__container").last());
    }
    public static ElementsCollection downHeaderElements = $$("a[data-ga-event-category='UpCatBottomMenuClick']");
    public static ElementsCollection productCategorySections = $$("a.carousel-catalog-products__title-link");
    public static ElementsCollection footerElements = $$("a[data-ga-event-category='FooterClick']");


    private static UiElement ui(String name, SelenideElement element) {
        return new UiElement(name, element);
    }
    private MainPageLocators() {
        throw new AssertionError("Этот класс — только статические методы, экземпляры создавать нельзя");
    }
}
