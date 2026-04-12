package ru.holodilnik.framework.ui.locators;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import ru.holodilnik.framework.ui.elements.UiElement;
import ru.holodilnik.framework.ui.elements.UiElementsCollection;

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
        return ui("Баннер главной страницы", $$("a.promo-box__container").last());
    }

    public static UiElementsCollection downHeaderElements() {
        return uis("Элементы нижнего хедера", $$("a[data-ga-event-category='UpCatBottomMenuClick']"));
    }

    public static UiElementsCollection productCategorySections() {
        return uis("Секции категорий товаров", $$("a.carousel-catalog-products__title-link"));
    }

    public static UiElementsCollection footerElements() {
        return uis("Элементы футера", $$("a[data-ga-event-category='FooterClick']"));
    }

    private static UiElement ui(final String name, final SelenideElement element) {
        return new UiElement(name, element);
    }

    private static UiElementsCollection uis(final String name, final ElementsCollection elements) {
        return new UiElementsCollection(name, elements);
    }

    private MainPageLocators() {
        throw new AssertionError("Этот класс — только статические методы, экземпляры создавать нельзя");
    }
}
