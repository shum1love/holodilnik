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
        // ALLURE_RESULTS_MVN и ALLURE_REPORT больше не нужны
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
                    # Удаляем старый отчёт, если был
                    rm -rf target/site/allure-maven-plugin

                    # Пишем environment и executor — это важно
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

                    # Генерируем отчёт БЕЗ -D параметров
                    mvn -B allure:report || true

                    # Парсим summary.json из дефолтного места
                    if [ -f "target/site/allure-maven-plugin/widgets/summary.json" ]; then
                        get_stat() {
                            local key="$1"
                            local file="$2"
                            awk -v k="$key" '
                                $0 ~ "\"" k "\"[[:space:]]+:" {
                                    match($0, /[0-9]+/)
                                    if (RSTART > 0) print substr($0, RSTART, RLENGTH)
                                    exit
                                }
                            ' "$file" || echo "0"
                        }

                        SUMMARY_FILE="target/site/allure-maven-plugin/widgets/summary.json"

                        {
                            echo "ALLURE_TOTAL=$(get_stat total "$SUMMARY_FILE")"
                            echo "ALLURE_PASSED=$(get_stat passed "$SUMMARY_FILE")"
                            echo "ALLURE_FAILED=$(get_stat failed "$SUMMARY_FILE")"
                            echo "ALLURE_BROKEN=$(get_stat broken "$SUMMARY_FILE")"
                            echo "ALLURE_SKIPPED=$(get_stat skipped "$SUMMARY_FILE")"
                        } > .allure-summary.env
                    fi
                fi
            '''

            // Архивим результаты + новый отчёт
            archiveArtifacts artifacts: 'target/allure-results/**,target/site/allure-maven-plugin/**,.allure-summary.env', allowEmptyArchive: true

            script {
                if (fileExists('.allure-summary.env')) {
                    readFile('.allure-summary.env').trim().split('\n').each { line ->
                        def (k, v) = line.tokenize('=')
                        env[k] = v
                    }
                }
                // Путь к результатам не изменился
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