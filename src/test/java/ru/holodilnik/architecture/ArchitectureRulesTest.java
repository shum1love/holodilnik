package ru.holodilnik.architecture;

import com.codeborne.selenide.Selenide;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;
import ru.holodilnik.tests.base.BaseTest;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Тесты для проверки архитектуры проекта.
 */
class ArchitectureRulesTest {

    private static final JavaClasses IMPORTED_CLASSES = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_JARS)
            .importPackages("ru.holodilnik");

    /**
     * Проверяет, что все бизнес-тесты используют общий контракт BaseTest.
     */
    @Test
    void business_tests_must_extend_base_test() {
        final ArchRule rule = classes()
                .that().resideInAnyPackage(
                        "ru.holodilnik.tests.ui..",
                        "ru.holodilnik.tests.api..",
                        "ru.holodilnik.tests.integration.."
                )
                .and().haveSimpleNameEndingWith("Test")
                .should().beAssignableTo(BaseTest.class)
                .because("business tests should use one shared setup and teardown contract");

        rule.check(IMPORTED_CLASSES);
    }

    /**
     * Проверяет, что бизнес-тесты не зависят напрямую от Selenide или Selenium.
     */
    @Test
    void business_tests_should_not_use_selenide_or_webdriver_directly() {
        final ArchRule rule = noClasses()
                .that().resideInAnyPackage(
                        "ru.holodilnik.tests.ui..",
                        "ru.holodilnik.tests.api..",
                        "ru.holodilnik.tests.integration.."
                )
                .should().dependOnClassesThat().resideInAnyPackage(
                        "com.codeborne.selenide..",
                        "org.openqa.selenium.."
                )
                .because("business tests should stay at scenario level");

        rule.check(IMPORTED_CLASSES);
    }

    /**
     * Проверяет, что бизнес-тесты не используют напрямую локаторы, UI-элементы или core-классы.
     * Тесты должны работать через Page Object или API-клиенты.
     * Предотвращает жёсткую связность тестов с внутренностями фреймворка.
     */
    @Test
    void business_tests_should_use_interaction_layer_instead_of_framework_internals() {
        final ArchRule rule = noClasses()
                .that().resideInAnyPackage(
                        "ru.holodilnik.tests.ui..",
                        "ru.holodilnik.tests.api..",
                        "ru.holodilnik.tests.integration.."
                )
                .should().dependOnClassesThat().resideInAnyPackage(
                        "ru.holodilnik.framework.ui.locators..",
                        "ru.holodilnik.framework.ui.elements..",
                        "ru.holodilnik.framework.core.."
                )
                .because("tests should work through the interaction layer only");

        rule.check(IMPORTED_CLASSES);
    }

    /**
     * Проверяет, что Page Object и API-клиенты не зависят от тестов.
     */
    @Test
    void interaction_layer_should_not_depend_on_test_layer() {
        final ArchRule rule = noClasses()
                .that().resideInAnyPackage(
                        "ru.holodilnik.framework.ui.pages..",
                        "ru.holodilnik.framework.api.clients.."
                )
                .should().dependOnClassesThat().resideInAnyPackage("ru.holodilnik.tests..")
                .because("framework interaction classes should not know about test scenarios");

        rule.check(IMPORTED_CLASSES);
    }

    /**
     * Проверяет, что Page Object и API-клиенты не используют библиотеки для ассертов.
     */
    @Test
    void interaction_layer_should_not_depend_on_assertion_libraries() {
        final ArchRule rule = noClasses()
                .that().resideInAnyPackage(
                        "ru.holodilnik.framework.ui.pages..",
                        "ru.holodilnik.framework.api.clients.."
                )
                .should().dependOnClassesThat().resideInAnyPackage(
                        "org.junit..",
                        "org.assertj..",
                        "org.hamcrest.."
                )
                .because("assertions belong to tests, not to technical interaction classes");

        rule.check(IMPORTED_CLASSES);
    }

    /**
     * Проверяет, что Page Object и API-клиенты не используют явные sleep.
     */
    @Test
    void interaction_layer_should_not_use_explicit_sleep() {
        final ArchRule rule = noClasses()
                .that().resideInAnyPackage(
                        "ru.holodilnik.framework.ui.pages..",
                        "ru.holodilnik.framework.api.clients.."
                )
                .should().callMethod(Thread.class, "sleep", long.class)
                .orShould().callMethod(Selenide.class, "sleep", long.class)
                .because("waits should be condition-based, not time-based");

        rule.check(IMPORTED_CLASSES);
    }

    /**
     * Проверяет, что core-слой не зависит от UI и тестов.
     */
    @Test
    void core_layer_should_not_depend_on_ui_or_tests() {
        final ArchRule rule = noClasses()
                .that().resideInAnyPackage("ru.holodilnik.framework.core..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "ru.holodilnik.framework.ui..",
                        "ru.holodilnik.tests.."
                )
                .because("core should stay independent from UI and test layers");

        rule.check(IMPORTED_CLASSES);
    }
}
