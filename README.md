# Holodilnik UI Automation Framework

Надежный UI-автофреймворк для [holodilnik.ru](https://www.holodilnik.ru) на `Java + Selenide + JUnit 5 + Allure`.

Проект рассчитан на долгосрочную поддержку: читаемые Page Object'ы, стабильные селекторы, воспроизводимый запуск в Docker/Selenoid и готовый Jenkins pipeline.

Правила разработки и архитектурные ограничения: [AGENTS.md](AGENTS.md)

## Содержание

- [Технологический стек](#технологический-стек)
- [Архитектура](#архитектура)
- [Структура репозитория](#структура-репозитория)
- [Быстрый старт](#быстрый-старт)
- [Запуск тестов](#запуск-тестов)
- [Quality Gates (ArchUnit / Checkstyle / SpotBugs)](#quality-gates-archunit--checkstyle--spotbugs)
- [Docker + Selenoid](#docker--selenoid)
- [Jenkins CI](#jenkins-ci)
- [Allure отчеты](#allure-отчеты)
- [Рекомендации по развитию](#рекомендации-по-развитию)

## Технологический стек

- Java 17
- Maven 3
- Selenide
- JUnit 5
- Allure
- Jenkins
- Selenoid (удаленный Selenium WebDriver)

## Архитектура

Ключевая идея: тест описывает бизнес-поведение, а детали UI-реализации инкапсулированы в Page Object слоях.

```text
Tests
  -> Pages / Components
    -> Locators / Elements wrappers
      -> Selenide / Remote WebDriver (Selenoid)
```

Что это дает:

- меньше дублирования в тестах;
- проще поддерживать локаторы;
- легче масштабировать набор сценариев;
- понятный вход для новых участников команды.

## Структура репозитория

```text
src/main/java/ru/holodilnik/framework
  core/config       # загрузка конфигураций
  ui/elements       # обертки над элементами и коллекциями
  ui/locators       # локаторы страниц/компонентов
  ui/pages          # Page Object'ы

src/test/java/ru/holodilnik/tests
  base              # базовая тестовая инфраструктура
  ui                # UI-сценарии

src/test/resources
  config.properties
  selenide.properties
  junit-platform.properties
  allure.properties

selenoid/
  browsers.json     # конфигурация браузеров для Selenoid

Jenkinsfile*        # pipeline сценарии
```

## Быстрый старт

Требования:

- JDK 17
- Maven 3.9+
- Docker Desktop (Linux containers)

Локальная проверка проекта:

```bash
mvn clean test
```

Smoke-запуск по тегу:

```bash
mvn clean test -Djunit.jupiter.tags=smoke
```

## Запуск тестов

Примеры команд:

- Полный запуск: `mvn clean test`
- Только smoke: `mvn clean test -Djunit.jupiter.tags=smoke`
- Конкретный класс: `mvn clean test -Dtest=ru.holodilnik.tests.ui.SearchTest`

## Quality Gates (ArchUnit / Checkstyle / SpotBugs)

Единый минимальный набор проверок качества перед merge:

```bash
# 1) Архитектурные правила
mvn "-Dtest=ru.holodilnik.architecture.ArchitectureRulesTest" test

# 2) Статический стиль кода (src/main/java)
mvn checkstyle:check -DskipTests

# 3) Статический анализ байткода (src/main/java)
mvn spotbugs:check -DskipTests
```

Как интерпретировать результат:

- `BUILD SUCCESS` у всех трёх команд — quality gates пройдены.
- `checkstyle:check` падает — правим нарушения из консоли (`[MagicNumber]`, формат, naming и т.д.).
- `spotbugs:check` падает — устраняем баг-риски (`EI_EXPOSE_REP`, `NP_*`, `DLS_*` и т.д.) или добавляем обоснованный `excludeFilter`.

Примечание: в текущей конфигурации `checkstyle` и `spotbugs` проверяют только `src/main/java` (тестовые исходники исключены).

## Docker + Selenoid

1. Поднять Selenoid и UI:

```bash
docker compose up -d selenoid selenoid-ui
```

2. Проверить статус:

```bash
curl http://localhost:4444/status
```

3. При необходимости подтянуть браузерные образы из `selenoid/browsers.json`:

```bash
docker pull selenoid/vnc:chrome_128.0
```

4. Пример запуска тестов через удаленный WebDriver:

```bash
mvn clean test -Djunit.jupiter.tags=smoke -Dselenide.remote=http://localhost:4444/wd/hub -Dselenide.browser=chrome -Dselenide.browserVersion=128.0
```

UI Selenoid: [http://localhost:8080](http://localhost:8080)

## Jenkins CI

В репозитории есть 3 сценария pipeline:

- `Jenkinsfile-smoke` — smoke прогон;
- `Jenkinsfile-regression` — полный прогон;
- `manual` — ручной запуск выбранного класса (`TEST_CLASS`).

Минимальная настройка Jenkins:

1. Установить плагины `Pipeline`, `Git`, `JUnit`, `Allure Jenkins Plugin`.
2. Добавить tools с именами `jdk17`, `maven3`, `allure`.
3. Создать Pipeline job из SCM и указать нужный `Script Path`.

Подробный пошаговый сценарий: [docs/jenkins-local-setup.md](docs/jenkins-local-setup.md)

## Allure отчеты

Локально:

```bash
mvn allure:report
mvn allure:serve
```

В Jenkins отчет публикуется из `target/allure-results`.

## Рекомендации по развитию

- Добавлять новые сценарии через Page Object слой, не смешивая бизнес-логику и детали DOM.
- Для новых тестов использовать JUnit-теги (`smoke`, `UI`, тематические теги), чтобы удобно собирать наборы в CI.
- Любые изменения инфраструктуры запуска (Docker/Jenkins/Selenoid) фиксировать в `README` и `docs/` одновременно.

---

Если хочешь, можно быстро начать с `OpenMainPageTest` и `SearchTest` как базовых smoke-сценариев для проверки среды.
