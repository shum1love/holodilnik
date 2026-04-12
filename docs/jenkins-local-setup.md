# Jenkins + Selenoid (коротко)

Документ для быстрого локального поднятия CI-прогона.

## 1) Поднять Selenoid

```bash
docker compose up -d selenoid selenoid-ui
curl http://localhost:4444/status
```

Если браузерных образов нет:

```bash
docker pull selenoid/vnc:chrome_128.0
docker pull selenoid/vnc:firefox_120.0
```

## 2) Поднять Jenkins

```bash
docker volume create jenkins_home
docker run -d --name jenkins-local -p 8081:8080 -p 50000:50000 -v jenkins_home:/var/jenkins_home -v /var/run/docker.sock:/var/run/docker.sock -v "$PWD":/workspace -w /workspace jenkins/jenkins:lts-jdk21
```

Первый пароль:

```bash
docker exec jenkins-local cat /var/jenkins_home/secrets/initialAdminPassword
```

Jenkins UI: [http://localhost:8081](http://localhost:8081)

## 3) Настроить Jenkins

Плагины:

- Pipeline
- Git
- JUnit
- Allure Jenkins Plugin

Tools (`Manage Jenkins -> Tools`):

- `jdk17`
- `maven3`
- `allure`

## 4) Создать и запустить job

Тип job: `Pipeline script from SCM`.

`Script Path`:

- `Jenkinsfile-smoke` — smoke-запуск
- `Jenkinsfile-regression` — полный регресс
- `manual` — запуск конкретного класса

Для `manual` задайте параметр `TEST_CLASS`, например:

- `ru.holodilnik.tests.ui.SearchTest`

## 5) Если тесты не стартуют

- Проверьте Selenoid: `curl http://localhost:4444/status`
- Проверьте контейнеры: `docker ps`
- Проверьте URL remote webdriver:
  - одна сеть с Jenkins: `http://selenoid:4444/wd/hub`
  - другая сеть/host: `http://host.docker.internal:4444/wd/hub`