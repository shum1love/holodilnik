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
        ALLURE_RESULTS_MVN = 'allure-results'
        ALLURE_REPORT      = 'allure-report'
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

        stage('Run UI Tests') {
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
                    rm -rf "${ALLURE_REPORT}"

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
  "buildUrl": "${RUN_DISPLAY_URL:-${BUILD_URL}}",
  "reportUrl": "${RUN_DISPLAY_URL:-${BUILD_URL}}allure/"
}
EOT

                    if ! mvn -B allure:report \
                        -Dallure.results.directory=${ALLURE_RESULTS_MVN} \
                        -Dallure.report.directory=${ALLURE_REPORT}
                    then
                        echo "WARN: Allure report generation failed, keeping pipeline status from test stages"
                    fi

                    if [ -f "${ALLURE_REPORT}/widgets/summary.json" ]; then
                        summary_get() {
                            key="$1"
                            file="$2"
                            value=$(sed -n "s/.*\"${key}\"[[:space:]]*:[[:space:]]*\([0-9][0-9]*\).*/\1/p" "$file" | head -n1)
                            [ -n "$value" ] || value=0
                            printf '%s' "$value"
                        }

                        SUMMARY_FILE="${ALLURE_REPORT}/widgets/summary.json"
                        {
                            echo "ALLURE_TOTAL=$(summary_get total \"$SUMMARY_FILE\")"
                            echo "ALLURE_PASSED=$(summary_get passed \"$SUMMARY_FILE\")"
                            echo "ALLURE_FAILED=$(summary_get failed \"$SUMMARY_FILE\")"
                            echo "ALLURE_BROKEN=$(summary_get broken \"$SUMMARY_FILE\")"
                            echo "ALLURE_SKIPPED=$(summary_get skipped \"$SUMMARY_FILE\")"
                        } > .allure-summary.env
                    fi
                fi
            '''

            archiveArtifacts artifacts: 'target/allure-results/**,allure-report/**,.allure-summary.env', allowEmptyArchive: true

            script {
                if (fileExists('.allure-summary.env')) {
                    readFile('.allure-summary.env').trim().split('\n').each { line ->
                        def (k, v) = line.tokenize('=')
                        env[k] = v
                    }
                }
                if (fileExists(env.ALLURE_RESULTS_DIR)) {
                    allure([results: [[path: env.ALLURE_RESULTS_DIR]]])
                }
            }
        }

        success {
            withCredentials([
                string(credentialsId: 'telegram-bot-token', variable: 'TOKEN'),
                string(credentialsId: 'telegram-chat-id', variable: 'CHAT')
            ]) {
                sh '''
                    set +x
                    BASE_URL="${RUN_DISPLAY_URL:-${BUILD_URL:-${JOB_URL}${BUILD_NUMBER}/}}"
                    MSG="✅ ${JOB_NAME} #${BUILD_NUMBER}%0A"
                    MSG="${MSG}Алюр отчёт: ${BASE_URL}allure/%0A"
                    MSG="${MSG}passed: ${ALLURE_PASSED:-0}, failed: ${ALLURE_FAILED:-0}, broken: ${ALLURE_BROKEN:-0}, skipped: ${ALLURE_SKIPPED:-0}%0A"
                    MSG="${MSG}Build: ${BASE_URL}"

                    HTTP_CODE=$(curl -sS -o /tmp/tg-response.txt -w "%{http_code}" -X POST "https://api.telegram.org/bot${TOKEN}/sendMessage" \
                        -d chat_id="${CHAT}" \
                        -d disable_web_page_preview=true \
                        --data-urlencode "text=${MSG}" || true)

                    [ "${HTTP_CODE}" = "200" ] || echo "WARN: Telegram notify failed, HTTP=${HTTP_CODE}, response=$(cat /tmp/tg-response.txt)"
                '''
            }
        }

        failure {
            withCredentials([
                string(credentialsId: 'telegram-bot-token', variable: 'TOKEN'),
                string(credentialsId: 'telegram-chat-id', variable: 'CHAT')
            ]) {
                sh '''
                    set +x
                    BASE_URL="${RUN_DISPLAY_URL:-${BUILD_URL:-${JOB_URL}${BUILD_NUMBER}/}}"
                    MSG="❌ ${JOB_NAME} #${BUILD_NUMBER}%0A"
                    MSG="${MSG}Алюр отчёт: ${BASE_URL}allure/%0A"
                    MSG="${MSG}passed: ${ALLURE_PASSED:-0}, failed: ${ALLURE_FAILED:-0}, broken: ${ALLURE_BROKEN:-0}, skipped: ${ALLURE_SKIPPED:-0}%0A"
                    MSG="${MSG}Console: ${BASE_URL}consoleFull"

                    HTTP_CODE=$(curl -sS -o /tmp/tg-response.txt -w "%{http_code}" -X POST "https://api.telegram.org/bot${TOKEN}/sendMessage" \
                        -d chat_id="${CHAT}" \
                        -d disable_web_page_preview=true \
                        --data-urlencode "text=${MSG}" || true)

                    [ "${HTTP_CODE}" = "200" ] || echo "WARN: Telegram notify failed, HTTP=${HTTP_CODE}, response=$(cat /tmp/tg-response.txt)"
                '''
            }
        }
    }
}
