package ru.holodilnik.framework.ui.locators;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import ru.holodilnik.framework.ui.elements.UiElement;
import ru.holodilnik.framework.ui.elements.UiElementsCollection;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class CatalogueMenuLocators {

    public static UiElement mobileAppSelection() {
        return ui("Раздел мобильное приложение", $$("a").findBy(text("Мобильное приложение")));
    }

    public static UiElement popularBrandsSelection() {
        return ui("Популярные бренды", $$("a").findBy(text("Популярные бренды")));
    }

    public static UiElement technicalInstallationSelection() {
        return ui("Установка техники", $$("a").findBy(text("Установка техники")));
    }

    public static UiElement replacementSelection() {
        return ui("Заменим, если техника сломалась", $$("a").findBy(text("Заменим если техника")));
    }

    public static UiElement discountedProductsSelection() {
        return ui("Уценённые товары", $$("a[data-ga-event-action='repair']").last());
    }

    public static UiElement refrigerator() {
        return ui("Холодильники и морозильники", $$("a[data-ga-event-action='refrigerator']").last());
    }

    public static UiElement title() {
        return ui("Заголовок", $("h1"));
    }

    public static UiElementsCollection categories() {
        return uis("Категории каталога", $$("div.categories__item-list"));
    }

    private static UiElement ui(String name, SelenideElement element) {
        return new UiElement(name, element);
    }

    private static UiElementsCollection uis(String name, ElementsCollection elements) {
        return new UiElementsCollection(name, elements);
    }

    private CatalogueMenuLocators() {
        throw new AssertionError("Этот класс — только статические методы, экземпляры создавать нельзя");
    }
}
