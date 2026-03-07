# Базовый образ — текущий Jenkins
FROM jenkins/jenkins:lts-jdk21

# Переключаемся на root
USER root

# Установка jq: обновляем репозитории, ставим jq, чистим кэш
RUN apt-get update && \
    apt-get install -y jq && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Возвращаемся к user jenkins
USER jenkins