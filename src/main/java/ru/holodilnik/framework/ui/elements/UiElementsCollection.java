package ru.holodilnik.framework.ui.elements;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.WebElementsCondition;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.visible;

/**
 * Обёртка над ElementsCollection с удобными шагами и типобезопасными операциями.
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
public class UiElementsCollection {

    private final ElementsCollection elements;
    private final String name;

    public UiElementsCollection(final String name, final ElementsCollection elements) {
        this.name = name;
        this.elements = elements;
    }

    @Step("{name} → проверяем условия коллекции")
    public UiElementsCollection shouldHave(final WebElementsCondition... conditions) {
        for (final WebElementsCondition condition : conditions) {
            elements.shouldHave(condition);
        }
        return this;
    }

    @Step("{name} → все элементы видимы")
    public UiElementsCollection shouldAllBeVisible() {
        elements.forEach(el -> el.shouldBe(visible));
        return this;
    }

    @Step("{name} → все элементы, кроме последних {countToSkip}, видимы")
    public UiElementsCollection shouldBeVisibleExceptLast(final int countToSkip) {
        final int size = elements.size();

        for (int i = 0; i < size - countToSkip; i++) {
            elements.get(i).shouldBe(visible);
        }

        return this;
    }

    @Step("{name} → все элементы активны (enabled)")
    public UiElementsCollection shouldAllBeEnabled() {
        elements.forEach(el -> el.shouldBe(enabled));
        return this;
    }

    @Step("{name} → ищем элемент по условию")
    public UiElement findBy(final WebElementCondition condition) {
        return new UiElement(name + " (found)", elements.findBy(condition));
    }

    @Step("{name} → берём элемент с индексом {index}")
    public UiElement get(final int index) {
        return new UiElement(name + "[" + index + "]", elements.get(index));
    }

    @Step("{name} → скроллим до всех элементов")
    public UiElementsCollection scrollToAll() {
        elements.forEach(el -> el.scrollIntoView(true));
        return this;
    }

    @Step("{name} → берём первые {count} элементов")
    public UiElementsCollection first(final int count) {
        final int actualSize = elements.size();

        if (actualSize < count) {
            throw new AssertionError(
                    String.format("Ожидали минимум %d элементов, но нашли %d", count, actualSize)
            );
        }

        return new UiElementsCollection(
                name + " (first " + count + ")",
                elements.first(count)
        );
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
