pipeline {
    agent any

    tools {
        maven 'maven3'   // Имя Maven из Jenkins Global Tool Configuration
        jdk 'jdk17'          // Имя JDK 17 из Jenkins Global Tool Configuration
        allure 'allure'
    }

    stages {
        stage('Run Tests') {
            steps {
                echo "Запуск тестов..."
                sh 'mvn clean test'
            }
        }

        stage('Generate Allure Report') {
            steps {
                echo "Генерация Allure отчёта..."
                sh 'mvn allure:report'
            }
        }

        stage('Show Allure Report') {
            steps {
                echo """
🏆 Allure отчёт готов!
Открой его локально командой:
mvn allure:serve
Или найдёшь готовый HTML здесь: target/site/allure-maven/index.html
"""
            }
        }
    }

    post {
        always {
            echo "Сборка завершена ✅"
        }
    }
}