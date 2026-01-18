package ru.holodilnik.framework.ui.pages;

import com.codeborne.selenide.SelenideElement;
import ru.holodilnik.framework.ui.pages.components.HeaderComponent;

import static com.codeborne.selenide.Selenide.$;

/**
 * Главная страница сайта holodilnik.ru.
 *
 * Отвечает ТОЛЬКО за:
 * - контракт страницы
 * - бизнес-действия, доступные пользователю с главной
 *
 * НЕ отвечает за:
 * - технические клики
 * - ассерты уровня тестов
 * - внутреннюю структуру компонентов
 */
public final class MainPage extends BasePage<MainPage> {

    // Якорный элемент страницы
    private final SelenideElement logo = $("span.site-header__logo-brand");

    // В конструкторе HeaderComponent
    private final HeaderComponent header =
            new HeaderComponent($("header, .site-header, .header"));  // несколько вариантов на выбор

    public MainPage() {
        super("/");
    }

    @Override
    protected SelenideElement pageIdentifier() {
        return logo;
    }

    /**
     * Поиск товара через хедер.
     */
    public SearchResultsPage search(String query) {
        header.search(query);
        return new SearchResultsPage();
    }

    /**
     * Открытие каталога через хедер.
     */
    /*public CatalogPage openCatalog() {
        header.openCatalog();
        return new CatalogPage();
    }*/

    /**
     * Явный доступ к компоненту (использовать осознанно).
     */
    public HeaderComponent header() {
        return header;
    }
}
