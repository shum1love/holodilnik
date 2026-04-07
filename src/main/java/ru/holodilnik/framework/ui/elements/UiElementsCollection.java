package ru.holodilnik.framework.ui.elements;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.WebElementsCondition;
import io.qameta.allure.Step;

/**
 * Обёртка над ElementsCollection с удобными шагами и типобезопасными операциями.
 */
public class UiElementsCollection {

    private final ElementsCollection elements;
    private final String name;

    public UiElementsCollection(String name, ElementsCollection elements) {
        this.name = name;
        this.elements = elements;
    }

    @Step("{name} → проверяем условие коллекции")
    public UiElementsCollection shouldHave(WebElementsCondition condition) {
        elements.shouldHave(condition);
        return this;
    }

    @Step("{name} → фильтруем по условию")
    public UiElementsCollection filterBy(WebElementCondition condition) {
        return new UiElementsCollection(name + " (filtered)", elements.filterBy(condition));
    }

    @Step("{name} → ищем элемент по условию")
    public UiElement findBy(WebElementCondition condition) {
        return new UiElement(name + " (found)", elements.findBy(condition));
    }

    @Step("{name} → берём элемент с индексом {index}")
    public UiElement get(int index) {
        return new UiElement(name + "[" + index + "]", elements.get(index));
    }

    public UiElement first() {
        return new UiElement(name + "[first]", elements.first());
    }

    public UiElement last() {
        return new UiElement(name + "[last]", elements.last());
    }

    public int size() {
        return elements.size();
    }

    public ElementsCollection getSelenideCollection() {
        return elements;
    }
}
