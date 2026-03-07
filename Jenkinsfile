pipeline {
    agent any

    tools {
        maven 'maven3'   // Имя Maven из Jenkins Global Tool Configuration
        jdk 'jdk17'      // Имя JDK 17 из Jenkins Global Tool Configuration
        allure 'allure'  // Имя Allure из Jenkins Global Tool Configuration
    }

    environment {
        SELENOID_URL = 'http://selenoid:4444/wd/hub' // URL твоего Selenoid
        BROWSER = 'chrome'
        BROWSER_VERSION = '128.0'
        HEADLESS = 'true' // headless режим
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
                allure includeProperties: false, jdk: 'jdk17', results: [[path: 'target/allure-results']]
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