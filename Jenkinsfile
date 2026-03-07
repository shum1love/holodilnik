pipeline {
    agent any

    tools {
        jdk 'jdk17'
        maven 'maven3'
        allure 'allure'
    }

    environment {
        ALLURE_RESULTS_DIR = 'target/allure-results'
    }

    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/shum1love/holodilnik.git', branch: 'main'
            }
        }

        stage('Detect Selenoid') {
            steps {
                sh '''
                    detect_selenoid_url() {
                        [ -n "${SELENOID_URL}" ] && { echo "${SELENOID_URL}"; return 0; }
                        for candidate in \
                            http://host.docker.internal:4444/wd/hub \
                            http://selenoid:4444/wd/hub \
                            http://localhost:4444/wd/hub
                        do
                            curl -fsS --max-time 3 "${candidate%/wd/hub}/status" >/dev/null && { echo "${candidate}"; return 0; }
                        done
                        return 1
                    }

                    SELENOID_URL_EFFECTIVE="$(detect_selenoid_url)" || { echo "Selenoid not found"; exit 1; }
                    echo "SELENOID_URL_EFFECTIVE=${SELENOID_URL_EFFECTIVE}" > .selenoid.env
                '''
            }
        }

        stage('Run UI Tests & Allure Report') {
            steps {
                sh '''
                    # Загружаем Selenoid URL
                    . ./.selenoid.env

                    # Запуск UI тестов
                    mvn clean test -Dselenide.remote=${SELENOID_URL_EFFECTIVE}

                    # Генерация Allure отчета
                    mvn allure:report
                '''

                // Добавляем описание билда с ссылкой на Allure
                script {
                    def allureLink = "${BUILD_URL}allure/"
                    currentBuild.description = "<a href='${allureLink}'>Алюр отчёт</a>"
                }
            }
        }
    }
}