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
            sh '''
                if [ -d "${ALLURE_RESULTS_DIR}" ]; then
                    cat > "${ALLURE_RESULTS_DIR}/environment.properties" <<EOT
Jenkins.Job=${JOB_NAME}
Jenkins.BuildNumber=${BUILD_NUMBER}
Jenkins.Branch=${GIT_BRANCH}
Jenkins.Commit=${GIT_COMMIT}
EOT

                    cat > "${ALLURE_RESULTS_DIR}/executor.json" <<EOT
{
  "name": "Jenkins",
  "type": "jenkins",
  "buildName": "${JOB_NAME} #${BUILD_NUMBER}",
  "buildUrl": "${BUILD_URL}",
  "reportUrl": "${BUILD_URL}allure"
}
EOT
                fi

                archiveArtifacts artifacts: 'target/allure-results/**,target/site/allure-maven-plugin/**', allowEmptyArchive: true
            '''

            script {
                def total = 0, passed = 0, failed = 0, broken = 0, skipped = 0
                if (fileExists('target/site/allure-maven-plugin/widgets/summary.json')) {
                    def summary = readJSON file: 'target/site/allure-maven-plugin/widgets/summary.json'
                    total = summary.statistic.total ?: 0
                    passed = summary.statistic.passed ?: 0
                    failed = summary.statistic.failed ?: 0
                    broken = summary.statistic.broken ?: 0
                    skipped = summary.statistic.skipped ?: 0
                }

                env.ALLURE_TOTAL = total.toString()
                env.ALLURE_PASSED = passed.toString()
                env.ALLURE_FAILED = failed.toString()
                env.ALLURE_BROKEN = broken.toString()
                env.ALLURE_SKIPPED = skipped.toString()
                env.PASS_RATE = total > 0 ? ((passed * 100 / total).toInteger()).toString() : "0"
            }
        }

        success {
            withCredentials([
                string(credentialsId: 'telegram-bot-token', variable: 'TOKEN'),
                string(credentialsId: 'telegram-chat-id', variable: 'CHAT')
            ]) {
                sh '''
                    MSG=$(cat <<EOT
🚀 <b>${JOB_NAME}</b> #${BUILD_NUMBER} — УСПЕХ! ✅

📈 Доля пройденных: ${PASS_RATE}% (${ALLURE_PASSED}/${ALLURE_TOTAL})
✅ Всего тестов: <b>${ALLURE_TOTAL}</b>
🟢 Пройдено: <b>${ALLURE_PASSED}</b>
❌ Провалено: <b>${ALLURE_FAILED}</b>
⚠️ Сломано: <b>${ALLURE_BROKEN}</b>
⏭ Пропущено: <b>${ALLURE_SKIPPED}</b>
EOT
                    )

                    curl -sS -X POST "https://api.telegram.org/bot${TOKEN}/sendMessage" \
                        -d chat_id="${CHAT}" \
                        -d parse_mode="HTML" \
                        --data-urlencode "text=${MSG}" || echo "WARN: TG failed"
                '''
            }
        }

        failure {
            withCredentials([
                string(credentialsId: 'telegram-bot-token', variable: 'TOKEN'),
                string(credentialsId: 'telegram-chat-id', variable: 'CHAT')
            ]) {
                sh '''
                    MSG=$(cat <<EOT
💥 <b>${JOB_NAME}</b> #${BUILD_NUMBER} — ПРОВАЛ! 🔥

📈 Доля пройденных: ${PASS_RATE}% (${ALLURE_PASSED}/${ALLURE_TOTAL})
✅ Всего тестов: <b>${ALLURE_TOTAL}</b>
🟢 Пройдено: <b>${ALLURE_PASSED}</b>
❌ Провалено: <b>${ALLURE_FAILED}</b>
⚠️ Сломано: <b>${ALLURE_BROKEN}</b>
⏭ Пропущено: <b>${ALLURE_SKIPPED}</b>
EOT
                    )

                    curl -sS -X POST "https://api.telegram.org/bot${TOKEN}/sendMessage" \
                        -d chat_id="${CHAT}" \
                        -d parse_mode="HTML" \
                        --data-urlencode "text=${MSG}" || echo "WARN: TG failed"
                '''
            }
        }
    }
}