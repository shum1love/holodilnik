pipeline {
    agent any

    tools {
        maven 'maven3'
        jdk 'jdk17'
        allure 'allure'
    }

    environment {
        SELENOID_URL = 'http://selenoid:4444/wd/hub'
        BROWSER = 'chrome'
        BROWSER_VERSION = '128.0'
        HEADLESS = 'true'
    }

    stages {
        stage('Run Tests') {
            steps {
                echo "Запуск тестов через Selenoid..."
                sh """
                    mvn clean test \
                    -Dremote.url=$SELENOID_URL \
                    -Dbrowser=$BROWSER \
                    -Dbrowser.version=$BROWSER_VERSION \
                    -Dheadless=$HEADLESS
                """
            }
        }

        stage('Generate Allure Report') {
            steps {
                echo "Генерация Allure отчёта..."
                sh 'mvn allure:report'
            }
        }

        stage('Publish Allure Report') {
                    steps {
                        allure results: [[path: 'target/allure-results']]
                        // или более безопасный вариант:
                        // allure([
                        //     results: [[path: 'target/allure-results']]
                        // ])
                    }
                }
    }

    post {
        always {
            echo "Сборка завершена ✅"
        }
        success {
            echo "Тесты прошли успешно 🎉"
        }
        failure {
            echo "Тесты упали ❌"
        }
    }
}