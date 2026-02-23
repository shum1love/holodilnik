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

`Jenkinsfile` максимально упрощён: запускает только smoke-набор (`TEST_TAG=Smoke`)
в удалённом Chrome `126.0` через `http://selenoid:4444/wd/hub`.

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

Это означает, что на Docker-host не скачан нужный образ браузера для Selenoid.

```bash
# скачать ровно тот образ, который ждёт текущий browsers.json
docker pull selenoid/vnc:chrome_126.0

# перезапустить Selenoid после правок/скачивания
docker compose up -d selenoid

# проверить, что Selenoid видит версию 126.0
curl -s http://localhost:4444/status
```

Важно: в упрощённом Jenkinsfile версия браузера зафиксирована в pipeline (`126.0`),
чтобы исключить ошибки из-за пустых/сломанных параметров job.

Если в окружении ограничен доступ к Maven Central, Jenkins не скачает зависимости.
В таком случае нужен доступ к `https://repo.maven.apache.org/maven2` или прокси-репозиторий (Nexus/Artifactory).
