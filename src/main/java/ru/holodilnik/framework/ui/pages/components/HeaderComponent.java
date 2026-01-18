package ru.holodilnik.framework.ui.pages.components;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;

/**
 * Хедер сайта — переиспользуемый UI-компонент.
 */
public final class HeaderComponent {

    private final SelenideElement searchInput;
    private final SelenideElement catalogButton;

    public HeaderComponent(SelenideElement container) {
        this.searchInput = container.$("#top_search");
        this.catalogButton = container.$("button[data-test='catalog-button']");  // ← проверь реальный селектор!
    }

    /**
     * Поиск через строку поиска в хедере.
     */
    public HeaderComponent search(String query) {
        searchInput.shouldBe(visible)
                .setValue(query)
                .pressEnter();
        return this;
    }

    /**
     * Открытие каталога.
     */
    public HeaderComponent openCatalog() {
        catalogButton.shouldBe(visible).click();
        return this;
    }

    // Опционально: если часто нужно проверять видимость всего хедера
    // public HeaderComponent shouldBeVisible() {
    //     searchInput.shouldBe(visible);
    //     catalogButton.shouldBe(visible);
    //     return this;
    // }

    // НЕ добавляем публичные геттеры на элементы — нарушает инкапсуляцию
}
