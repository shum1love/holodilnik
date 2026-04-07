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
    private final String humanName;

    public UiElementsCollection(String humanName, ElementsCollection elements) {
        this.humanName = humanName;
        this.elements = elements;
    }

    @Step("{humanName} → проверяем условие коллекции")
    public UiElementsCollection shouldHave(WebElementsCondition condition) {
        elements.shouldHave(condition);
        return this;
    }

    @Step("{humanName} → фильтруем по условию")
    public UiElementsCollection filterBy(WebElementCondition condition) {
        return new UiElementsCollection(humanName + " (filtered)", elements.filterBy(condition));
    }

    @Step("{humanName} → ищем элемент по условию")
    public UiElement findBy(WebElementCondition condition) {
        return new UiElement(humanName + " (found)", elements.findBy(condition));
    }

    @Step("{humanName} → берём элемент с индексом {index}")
    public UiElement get(int index) {
        return new UiElement(humanName + "[" + index + "]", elements.get(index));
    }

    public UiElement first() {
        return new UiElement(humanName + "[first]", elements.first());
    }

    public UiElement last() {
        return new UiElement(humanName + "[last]", elements.last());
    }

    public int size() {
        return elements.size();
    }

    public ElementsCollection getSelenideCollection() {
        return elements;
    }
}
