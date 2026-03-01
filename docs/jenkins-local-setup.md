# Локальный Jenkins + запуск автотестов по триггерам

Ниже рабочая схема для локального запуска Jenkins и разделения тестов на несколько job.

## 1) Поднять Jenkins локально

Рекомендуемый вариант для этого репозитория — поднимать Jenkins и Selenoid из `docker-compose.yml`:

```bash
docker compose up -d
```

Проверка:

```bash
docker ps --filter name=jenkins-local
docker ps --filter name=selenoid
curl -s http://localhost:4444/status
```

`docker-compose.yml` уже содержит:
- `restart: unless-stopped` для Jenkins и Selenoid (автоподъём после перезапуска Docker Desktop);
- общую сеть `test-net`;
- Selenoid с `-container-network test-net` (чтобы браузерные контейнеры стартовали в той же сети и не было `does not respond in 30s`).

Ниже оставлен альтернативный ручной запуск Jenkins в одном контейнере.

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

Текущий `Jenkinsfile` специально упрощён и запускает только smoke-набор (`TEST_TAG=Smoke`).
Это самый стабильный вариант для ежедневного прогона.

Если позже понадобится разделение на несколько job (`Smoke`, `Cart`, `UI`),
лучше делать это отдельными Jenkinsfile/ветками, а не усложнять основной smoke-пайплайн.

`Jenkinsfile` запускает тесты через удалённый Selenoid `http://selenoid:4444/wd/hub`
и использует браузерную версию по умолчанию из Selenoid.

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
`No such image: ...`

Это означает, что на Docker-host не скачан нужный образ браузера для Selenoid.

```bash
# посмотреть, какую версию/образ ждёт текущий browsers.json
cat selenoid-config/browsers.json

# скачать нужный образ (пример для default=latest)
docker pull selenoid/chrome:latest

# перезапустить Selenoid после правок/скачивания
docker compose up -d selenoid

# проверить статус и доступные браузеры
curl -s http://localhost:4444/status
```

Важно: в упрощённом Jenkinsfile версия браузера **не фиксируется** —
это исключает падения из-за несовпадения `browserVersion` и фактически доступных образов в Selenoid.

Если видите ошибку вида:
`SessionNotCreatedException: ... wait: http://172.x.x.x:4444/ does not respond in 30s`

Это означает, что Jenkins достучался до Selenoid, но Selenoid не смог поднять браузерный контейнер,
либо Jenkins использует неверный URL удалённого WebDriver.

Проверьте по шагам:

```bash
# 1) Selenoid доступен и отвечает статусом
curl -s http://localhost:4444/status

# 2) Нужный браузерный образ скачан
docker images | grep selenoid/chrome

# 3) Контейнер Selenoid запущен без падений
docker ps --filter name=selenoid
docker logs --tail 200 selenoid
```

Для Docker Desktop (Windows/macOS) в Jenkins обычно удобнее использовать URL:
`http://host.docker.internal:4444/wd/hub`.

Если Jenkins и Selenoid находятся в одной Docker-сети, можно использовать:
`http://selenoid:4444/wd/hub`.

Если в окружении ограничен доступ к Maven Central, Jenkins не скачает зависимости.
В таком случае нужен доступ к `https://repo.maven.apache.org/maven2` или прокси-репозиторий (Nexus/Artifactory).


## 9) Как понять, какой `SELENOID_URL` указывать

По умолчанию в `Jenkinsfile` параметр `SELENOID_URL` уже предзаполнен значением
`http://host.docker.internal:4444/wd/hub`, поэтому при ручном запуске job его обычно не нужно менять.

Если хотите полностью убрать ручной ввод в конкретной job:
- оставьте параметр по умолчанию как есть и просто запускайте Build;
- либо удалите параметр в UI job и используйте pipeline из SCM (тогда возьмётся значение из `Jenkinsfile`).

Если запускаете job вручную и не уверены в URL, оставьте параметр `SELENOID_URL` пустым — `Jenkinsfile` попробует автоопределение в таком порядке:

1. `http://host.docker.internal:4444/wd/hub`
2. `http://selenoid:4444/wd/hub`
3. `http://localhost:4444/wd/hub`

Проверить вручную можно из Jenkins-контейнера:

```bash
docker exec -it jenkins-local sh -lc 'for u in \
  http://host.docker.internal:4444/status \
  http://selenoid:4444/status \
  http://localhost:4444/status; do \
  echo "Checking $u"; curl -fsS --max-time 3 "$u" && break; done'
```

Рабочий URL для пайплайна — тот, где `/status` отвечает JSON-ом со списком браузеров.
