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

## 2) Как заново зайти в Jenkins

Если Jenkins уже был поднят ранее, но доступ «потерялся», используйте этот порядок:

1. Убедитесь, что контейнер запущен:

```bash
docker ps --filter name=jenkins-local
```

2. Если контейнер остановлен — поднимите его:

```bash
docker start jenkins-local
```

3. Откройте Jenkins в браузере:
- `http://localhost:8080`

4. Если забыли пароль администратора:
- Вариант A (первый вход после установки):

```bash
docker exec jenkins-local cat /var/jenkins_home/secrets/initialAdminPassword
```

- Вариант B (сброс пользователя через emergency flow):
  - остановите контейнер,
  - добавьте в `/var/jenkins_home/init.groovy.d/` скрипт создания админа,
  - запустите контейнер и войдите новым пользователем,
  - удалите временный groovy-скрипт.

5. Если не открывается UI:
- проверьте, что порт не занят: `lsof -i :8080`;
- проверьте логи Jenkins:

```bash
docker logs --tail 200 jenkins-local
```

## 3) Добавить toolchain в Jenkins

В Jenkins:
`Manage Jenkins -> Tools`

- JDK 17
- Maven 3.9+

## 4) Подключить репозиторий

Вариант A (проще):
- Создайте **Pipeline** job
- В `Pipeline script from SCM` укажите этот репозиторий
- `Script Path`: `Jenkinsfile`

Вариант B (если хотите без SCM):
- вставьте содержимое `Jenkinsfile` напрямую в UI job.

## 5) Разделить тесты на несколько job

В проекте тесты уже размечены JUnit5 тегами (`Smoke`, `Cart`, `UI`).

Сделайте 3 pipeline job из одного `Jenkinsfile`, но с разными значениями параметра `TEST_TAG`:

1. `ui-smoke` → `TEST_TAG=Smoke`
2. `ui-cart` → `TEST_TAG=Cart`
3. `ui-full` → `TEST_TAG=UI`

`Jenkinsfile` уже принимает параметры:
- `TEST_TAG`
- `SELENOID_REMOTE`
- `BROWSER_VERSION` (опционально, можно оставить пустым)

## 6) Настроить триггер

Рекомендуемые типы триггеров для job:

- `Build periodically`: `H 8 * * 1-5` (запуск по расписанию в будни)
- `Poll SCM`: `H/10 * * * *` (проверка коммитов каждые ~10 минут)

Если нужен webhook-trigger (по push в ветку), подключите webhook Git-провайдера на Jenkins job или используйте Multibranch Pipeline.

## 7) Рекомендуемая схема веток/триггеров

- `ui-smoke`: на каждый commit в feature/main
- `ui-cart`: по расписанию или nightly
- `ui-full`: nightly + перед релизом

## 8) Диагностика

Проверить, что тесты с тегом находятся локально:

```bash
mvn -B -Dtest=*Test -Djunit.jupiter.tags=Smoke test
mvn -B -Dtest=*Test -Djunit.jupiter.tags=Cart test
mvn -B -Dtest=*Test -Djunit.jupiter.tags=UI test
```

Если видите ошибку вида:
`No such image: selenoid/vnc:chrome_126.0`

Это означает, что Selenoid пытается поднять несуществующий браузерный image.
Решение:

```bash
# посмотреть активную конфигурацию браузеров
cat selenoid-config/browsers.json

# перезапустить Selenoid после правок
docker compose up -d selenoid

# (опционально) заранее скачать image
docker pull selenoid/chrome:latest
```

При необходимости фиксированной версии укажите `BROWSER_VERSION` в Jenkins job
(например `126.0`) и убедитесь, что эта версия есть в `selenoid-config/browsers.json`.

Важно: если в Jenkins-консоли видите команду с пустыми значениями
`-Djunit.jupiter.tags=` и/или `-Dselenide.remote=`, значит параметры job передались пустыми.
В актуальном `Jenkinsfile` это обработано fallback-значениями:
- `TEST_TAG=Smoke`
- `SELENOID_REMOTE=http://selenoid:4444/wd/hub`

Если после этого ошибка про `chromedriver` и `storage.googleapis.com` всё равно появляется,
значит тест пошёл локально (не через Selenoid) — проверьте, что `SELENOID_REMOTE` доступен
из Jenkins-контейнера и сервис `selenoid` поднят в той же docker-сети.

Если в окружении ограничен доступ к Maven Central, Jenkins не скачает зависимости.
В таком случае нужен доступ к `https://repo.maven.apache.org/maven2` или прокси-репозиторий (Nexus/Artifactory).
