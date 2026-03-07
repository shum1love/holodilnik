pipeline {
    agent any

    environment {
        TELEGRAM_TOKEN = credentials('telegram-bot-token')
        TELEGRAM_CHAT_ID = credentials('telegram-chat-id')
    }

    stages {

        stage('Checkout') {
            steps {
                git url: 'https://github.com/shum1love/holodilnik.git', branch: 'main'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Run UI tests') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Publish JUnit') {
            steps {
                junit 'target/surefire-reports/*.xml'
            }
        }

        stage('Generate Allure Report') {
            steps {
                script {
                    allure([
                        results: [[path: 'target/allure-results']]
                    ])
                }
            }
        }

        stage('Publish Allure HTML') {
            steps {
                publishHTML([
                    allowMissing: true,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'target/site/allure-maven-plugin',
                    reportFiles: 'index.html',
                    reportName: 'Allure'
                ])
            }
        }

        stage('Parse Test Results') {
            steps {
                script {

                    def report = junit testResults: 'target/surefire-reports/*.xml'

                    def total = report.totalCount
                    def failed = report.failCount
                    def skipped = report.skipCount
                    def passed = total - failed - skipped
                    def passRate = total > 0 ? (passed * 100 / total).toInteger() : 0

                    env.TEST_TOTAL = total.toString()
                    env.TEST_PASSED = passed.toString()
                    env.TEST_FAILED = failed.toString()
                    env.TEST_SKIPPED = skipped.toString()
                    env.TEST_PASSRATE = passRate.toString()
                }
            }
        }
    }

    post {

        success {
            script {

                def message = """
🚀 ${env.JOB_NAME} #${env.BUILD_NUMBER} — УСПЕХ! ✅

📊 Результаты тестов

📈 Доля пройденных: ${env.TEST_PASSRATE}% (${env.TEST_PASSED}/${env.TEST_TOTAL})

🧪 Всего тестов: ${env.TEST_TOTAL}
🟢 Пройдено: ${env.TEST_PASSED}
❌ Провалено: ${env.TEST_FAILED}
⏭️ Пропущено: ${env.TEST_SKIPPED}

📎 Jenkins отчёт
${env.RUN_DISPLAY_URL}
"""

                sh """
                curl -s -X POST https://api.telegram.org/bot${TELEGRAM_TOKEN}/sendMessage \
                -d chat_id=${TELEGRAM_CHAT_ID} \
                -d text="${message}" \
                -d parse_mode="HTML"
                """
            }
        }

        failure {
            script {

                def message = """
🔥 ${env.JOB_NAME} #${env.BUILD_NUMBER} — ПАДЕНИЕ ❌

🧪 Всего тестов: ${env.TEST_TOTAL}
❌ Провалено: ${env.TEST_FAILED}

📎 Jenkins отчёт
${env.RUN_DISPLAY_URL}
"""

                sh """
                curl -s -X POST https://api.telegram.org/bot${TELEGRAM_TOKEN}/sendMessage \
                -d chat_id=${TELEGRAM_CHAT_ID} \
                -d text="${message}"
                """
            }
        }
    }
}