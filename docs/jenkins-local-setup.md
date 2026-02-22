# Локальный Jenkins + запуск автотестов по триггерам

Ниже рабочая схема для локального запуска Jenkins и разделения тестов на несколько job.

## 1) Поднять Jenkins локально

```bash
docker volume create jenkins_home

docker run -d --name jenkins-local \
  -p 8080:8080 -p 50000:50000 \
  -v jenkins_home:/var/jenkins_home \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -v "$PWD":/workspace \
  jenkins/jenkins:lts-jdk17
```

Проверка:

```bash
docker ps --filter name=jenkins-local
```

Получить initial admin password:

```bash
docker exec jenkins-local cat /var/jenkins_home/secrets/initialAdminPassword
```

Откройте `http://localhost:8080`, завершите initial setup и поставьте плагины:
- **Pipeline**
- **Git**
- **JUnit**
- (опционально) **Allure Jenkins Plugin**

## 2) Добавить toolchain в Jenkins

В Jenkins:
`Manage Jenkins -> Tools`

- JDK 17
- Maven 3.9+

## 3) Подключить репозиторий

Вариант A (проще):
- Создайте **Pipeline** job
- В `Pipeline script from SCM` укажите этот репозиторий
- `Script Path`: `ci/Jenkinsfile`

Вариант B (если хотите без SCM):
- вставьте содержимое `ci/Jenkinsfile` напрямую в UI job.

## 4) Разделить тесты на несколько job

В проекте тесты уже размечены JUnit5 тегами (`Smoke`, `Cart`, `UI`).

Сделайте 3 pipeline job из одного `ci/Jenkinsfile`, но с разными значениями параметра `TEST_TAG`:

1. `ui-smoke` → `TEST_TAG=Smoke`
2. `ui-cart` → `TEST_TAG=Cart`
3. `ui-full` → `TEST_TAG=UI`

Каждая job будет запускать:

```bash
mvn -B -Dtest=*Test -Djunit.jupiter.tags=${TEST_TAG} test
```

## 5) Настроить триггер

В `ci/Jenkinsfile` уже включены 2 типа триггера:

- `cron('H 8 * * 1-5')` — запуск по расписанию в будни
- `pollSCM('H/10 * * * *')` — проверка коммитов каждые ~10 минут

Если нужен webhook-trigger (по push в ветку), подключите webhook Git-провайдера на Jenkins job или используйте Multibranch Pipeline.

## 6) Рекомендуемая схема веток/триггеров

- `ui-smoke`: на каждый commit в feature/main
- `ui-cart`: по расписанию или nightly
- `ui-full`: nightly + перед релизом

## 7) Диагностика

Проверить, что тесты с тегом находятся локально:

```bash
mvn -B -Dtest=*Test -Djunit.jupiter.tags=Smoke test
mvn -B -Dtest=*Test -Djunit.jupiter.tags=Cart test
mvn -B -Dtest=*Test -Djunit.jupiter.tags=UI test
```

Если в окружении ограничен доступ к Maven Central, Jenkins не скачает зависимости.
В таком случае нужен доступ к `https://repo.maven.apache.org/maven2` или прокси-репозиторий (Nexus/Artifactory).
