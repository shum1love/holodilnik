pipeline {
    agent any

    tools {
        jdk 'jdk17'
        maven 'maven3'
        allure 'allure'
    }

    triggers {
        cron('H 2 * * *')
    }

    environment {
        ALLURE_RESULTS_DIR = 'target/allure-results'
        JENKINS_PUBLIC_URL = ''
    }

    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/shum1love/holodilnik.git', branch: 'main'
            }
        }

        stage('Check Selenoid') {
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

                    SELENOID_URL_EFFECTIVE="$(detect_selenoid_url)" || exit 1
                    echo "SELENOID_URL_EFFECTIVE=${SELENOID_URL_EFFECTIVE}" > .selenoid.env
                '''
            }
        }

        stage('Build & Run UI Tests') {
            steps {
                sh '''
                    . ./.selenoid.env
                    mvn -B clean test -Dgroups=UI -Dselenide.remote=${SELENOID_URL_EFFECTIVE}
                '''
            }
        }
    }

    post {
        always {
            // архивируем артефакты напрямую
            archiveArtifacts artifacts: 'target/allure-results/**,target/site/allure-maven-plugin/**', allowEmptyArchive: true

            // обрабатываем summary и Allure переменные
            script {
                def summaryFile = 'target/site/allure-maven-plugin/widgets/summary.json'

                if (fileExists(summaryFile)) {
                    def summary = readJSON file: summaryFile
                    env.ALLURE_TOTAL  = (summary.statistic.total ?: 0).toString()
                    env.ALLURE_PASSED = (summary.statistic.passed ?: 0).toString()
                    env.ALLURE_FAILED = (summary.statistic.failed ?: 0).toString()
                    env.ALLURE_BROKEN = (summary.statistic.broken ?: 0).toString()
                    env.ALLURE_SKIPPED= (summary.statistic.skipped ?: 0).toString()
                } else {
                    env.ALLURE_TOTAL  = '0'
                    env.ALLURE_PASSED = '0'
                    env.ALLURE_FAILED = '0'
                    env.ALLURE_BROKEN = '0'
                    env.ALLURE_SKIPPED= '0'
                }

                def total = env.ALLURE_TOTAL.toInteger()
                def passed = env.ALLURE_PASSED.toInteger()
                env.PASS_RATE = total > 0 ? ((passed * 100) / total).toInteger().toString() : '0'

                def internalBuildUrl = env.BUILD_URL ?: "${env.JOB_URL}${env.BUILD_NUMBER}/"
                env.BUILD_PUBLIC_URL = internalBuildUrl
                env.ALLURE_REPORT_URL = "${internalBuildUrl}allure"
                env.CONSOLE_URL = "${internalBuildUrl}console"
            }

            // Allure отчёт
            allure([
                includeProperties: false,
                jdk: '',
                properties: [],
                reportBuildPolicy: 'ALWAYS',
                results: [[path: env.ALLURE_RESULTS_DIR]]
            ])
        }

        success {
            withCredentials([
                string(credentialsId: 'telegram-bot-token', variable: 'TOKEN'),
                string(credentialsId: 'telegram-chat-id', variable: 'CHAT')
            ]) {
                sh '''
                    PASSED=${ALLURE_PASSED:-0}
                    TOTAL=${ALLURE_TOTAL:-0}
                    FAILED=${ALLURE_FAILED:-0}
                    BROKEN=${ALLURE_BROKEN:-0}
                    SKIPPED=${ALLURE_SKIPPED:-0}
                    PASS_RATE=${PASS_RATE:-0}

                    MSG=$(cat <<EOT
🚀 <b>${JOB_NAME}</b> #${BUILD_NUMBER} — УСПЕХ! ✅

📈 Доля пройденных: ${PASS_RATE}% (${PASSED}/${TOTAL})
✅ Всего тестов: <b>${TOTAL}</b>
🟢 Пройдено: <b>${PASSED}</b>
❌ Провалено: <b>${FAILED}</b>
⚠️ Сломано: <b>${BROKEN}</b>
⏭ Пропущено: <b>${SKIPPED}</b>

📊 <a href="${ALLURE_REPORT_URL}">Allure отчёт</a>   🖥️ <a href="${CONSOLE_URL}">Консоль</a>
EOT
)

                    curl -sS -X POST "https://api.telegram.org/bot${TOKEN}/sendMessage" \
                        -d chat_id="${CHAT}" \
                        -d parse_mode="HTML" \
                        -d disable_web_page_preview=true \
                        --data-urlencode "text=${MSG}"
                '''
            }
        }

        failure {
            withCredentials([
                string(credentialsId: 'telegram-bot-token', variable: 'TOKEN'),
                string(credentialsId: 'telegram-chat-id', variable: 'CHAT')
            ]) {
                sh '''
                    PASSED=${ALLURE_PASSED:-0}
                    TOTAL=${ALLURE_TOTAL:-0}
                    FAILED=${ALLURE_FAILED:-0}
                    BROKEN=${ALLURE_BROKEN:-0}
                    SKIPPED=${ALLURE_SKIPPED:-0}
                    PASS_RATE=${PASS_RATE:-0}

                    MSG=$(cat <<EOT
💥 <b>${JOB_NAME}</b> #${BUILD_NUMBER} — ПРОВАЛ! 🔥

📈 Доля пройденных: ${PASS_RATE}% (${PASSED}/${TOTAL})
✅ Всего тестов: <b>${TOTAL}</b>
🟢 Пройдено: <b>${PASSED}</b>
❌ Провалено: <b>${FAILED}</b>
⚠️ Сломано: <b>${BROKEN}</b>
⏭ Пропущено: <b>${SKIPPED}</b>

📊 <a href="${ALLURE_REPORT_URL}">Allure отчёт</a>   🖥️ <a href="${CONSOLE_URL}">Консоль + лог</a>
EOT
)

                    curl -sS -X POST "https://api.telegram.org/bot${TOKEN}/sendMessage" \
                        -d chat_id="${CHAT}" \
                        -d parse_mode="HTML" \
                        -d disable_web_page_preview=true \
                        --data-urlencode "text=${MSG}"
                '''
            }
        }
    }
}