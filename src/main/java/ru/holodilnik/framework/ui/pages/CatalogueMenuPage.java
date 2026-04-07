package ru.holodilnik.framework.ui.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ru.holodilnik.framework.ui.locators.CatalogueMenuLocators;

import static com.codeborne.selenide.Condition.text;

/**
 * Класс описывающий меню каталога
 */
public class CatalogueMenuPage extends BasePage<CatalogueMenuPage> {
    public CatalogueMenuPage() {
        super("");
    }

    @Override
    protected SelenideElement pageIdentifier() {
        return CatalogueMenuLocators.popularBrandsSelection().getSelenideElement();
    }

    @Override
    public CatalogueMenuPage shouldBeOpen() {
        super.shouldBeOpen();
        CatalogueMenuLocators.popularBrandsSelection().shouldBeVisible();
        return this;
    }

    // ─── Business actions & checks ─────────────────────────────────

    @Step("Проверить основные элементы бокового меню каталога")
    public CatalogueMenuPage checkMainSections() {
        CatalogueMenuLocators.mobileAppSelection().shouldBeEnabled();
        CatalogueMenuLocators.popularBrandsSelection().shouldBeEnabled();
        CatalogueMenuLocators.technicalInstallationSelection().shouldBeEnabled();
        CatalogueMenuLocators.replacementSelection().shouldBeEnabled();
        CatalogueMenuLocators.discountedProductsSelection().shouldBeEnabled();
        CatalogueMenuLocators.refrigerator().shouldBeEnabled();
        return this;
    }

    @Step("Нажать на раздел 'Холодильники и морозильники'")
    public CatalogueMenuPage clickRefrigerator() {
        CatalogueMenuLocators.refrigerator().click();
        return this;
    }

    @Step("Проверь что в заголовке текст равен '{text}'")
    public CatalogueMenuPage checkTitle(final String text) {
        CatalogueMenuLocators.title().shouldContainText(text);
        return this;
    }

    @Step("Выбрать категорию '{categoryName}'")
    public SearchResultsPage selectCategory(final String categoryName) {
        CatalogueMenuLocators.categories()
                .findBy(text(categoryName))
                .shouldBeVisible()
                .click();

        return new SearchResultsPage();
    }
}
