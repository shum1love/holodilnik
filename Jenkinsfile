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
                    rm -rf target/site/allure-maven-plugin

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

                    mvn -B allure:report || true

                    # ────────────────────────────────────────────────
                    # Парсинг summary.json — jq если есть, иначе awk
                    # ────────────────────────────────────────────────
                    SUMMARY_FILE="target/site/allure-maven-plugin/widgets/summary.json"

                    if [ -f "$SUMMARY_FILE" ]; then
                        if command -v jq >/dev/null; then
                            {
                                echo "ALLURE_TOTAL=$(jq -r '((.statistic.total // .total // 0) | tonumber? // 0)' "$SUMMARY_FILE")"
                                echo "ALLURE_PASSED=$(jq -r '((.statistic.passed // .passed // 0) | tonumber? // 0)' "$SUMMARY_FILE")"
                                echo "ALLURE_FAILED=$(jq -r '((.statistic.failed // .failed // 0) | tonumber? // 0)' "$SUMMARY_FILE")"
                                echo "ALLURE_BROKEN=$(jq -r '((.statistic.broken // .broken // 0) | tonumber? // 0)' "$SUMMARY_FILE")"
                                echo "ALLURE_SKIPPED=$(jq -r '((.statistic.skipped // .skipped // 0) | tonumber? // 0)' "$SUMMARY_FILE")"
                            } > .allure-summary.env
                        else
                            # fallback awk (улучшенный, учитывает пробелы)
                            get_stat() {
                                local key="$1"
                                local file="$2"
                                awk -v k="$key" '
                                    $0 ~ "\"" k "\": *[0-9]+[ ,}]" {
                                        gsub(/[^0-9]/, "", $0)
                                        print $0
                                        exit
                                    }
                                ' "$file" || echo "0"
                            }

                            {
                                echo "ALLURE_TOTAL=$(get_stat total "$SUMMARY_FILE")"
                                echo "ALLURE_PASSED=$(get_stat passed "$SUMMARY_FILE")"
                                echo "ALLURE_FAILED=$(get_stat failed "$SUMMARY_FILE")"
                                echo "ALLURE_BROKEN=$(get_stat broken "$SUMMARY_FILE")"
                                echo "ALLURE_SKIPPED=$(get_stat skipped "$SUMMARY_FILE")"
                            } > .allure-summary.env
                        fi
                    fi
                fi
            '''

            archiveArtifacts artifacts: 'target/allure-results/**,target/site/allure-maven-plugin/**,.allure-summary.env', allowEmptyArchive: true

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
                    ALLURE_URL="${BASE_URL}allure/"

                    PASSED=${ALLURE_PASSED:-0}
                    TOTAL=${ALLURE_TOTAL:-0}
                    FAILED=${ALLURE_FAILED:-0}
                    BROKEN=${ALLURE_BROKEN:-0}
                    SKIPPED=${ALLURE_SKIPPED:-0}

                    to_num() {
                        case "$1" in
                            ''|*[!0-9]*) echo "0" ;;
                            *) echo "$1" ;;
                        esac
                    }

                    PASSED=$(to_num "$PASSED")
                    TOTAL=$(to_num "$TOTAL")
                    FAILED=$(to_num "$FAILED")
                    BROKEN=$(to_num "$BROKEN")
                    SKIPPED=$(to_num "$SKIPPED")

                    if [ "$TOTAL" -gt 0 ]; then
                        FILLED=$((PASSED * 10 / TOTAL))
                    else
                        FILLED=0
                    fi

                    BAR=""
                    i=0
                    while [ "$i" -lt "$FILLED" ]; do BAR="${BAR}█"; i=$((i + 1)); done
                    while [ "$i" -lt 10 ]; do BAR="${BAR}░"; i=$((i + 1)); done

                    MSG="🚀 *${JOB_NAME}* #${BUILD_NUMBER} — УСПЕХ! ✅%0A%0A"
                    MSG="${MSG}${BAR}  ${PASSED}/${TOTAL} тестов зелёные%0A"
                    MSG="${MSG}🔥 Провалено: *${FAILED}* | ⚠️ Сломано: *${BROKEN}* | ⏭ Пропущено: *${SKIPPED}*%0A%0A"
                    MSG="${MSG}📊 [Allure отчёт →](${ALLURE_URL})%0A"
                    MSG="${MSG}🖥️ [Консоль](${BASE_URL})"

                    curl -sS -X POST "https://api.telegram.org/bot${TOKEN}/sendMessage" \
                        -d chat_id="${CHAT}" \
                        -d parse_mode="Markdown" \
                        -d disable_web_page_preview=true \
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
                    set +x
                    BASE_URL="${RUN_DISPLAY_URL:-${BUILD_URL:-${JOB_URL}${BUILD_NUMBER}/}}"
                    ALLURE_URL="${BASE_URL}allure/"

                    PASSED=${ALLURE_PASSED:-0}
                    TOTAL=${ALLURE_TOTAL:-0}
                    FAILED=${ALLURE_FAILED:-0}
                    BROKEN=${ALLURE_BROKEN:-0}
                    SKIPPED=${ALLURE_SKIPPED:-0}

                    to_num() {
                        case "$1" in
                            ''|*[!0-9]*) echo "0" ;;
                            *) echo "$1" ;;
                        esac
                    }

                    PASSED=$(to_num "$PASSED")
                    TOTAL=$(to_num "$TOTAL")
                    FAILED=$(to_num "$FAILED")
                    BROKEN=$(to_num "$BROKEN")
                    SKIPPED=$(to_num "$SKIPPED")

                    if [ "$TOTAL" -gt 0 ]; then
                        FILLED=$((PASSED * 10 / TOTAL))
                    else
                        FILLED=0
                    fi

                    BAR=""
                    i=0
                    while [ "$i" -lt "$FILLED" ]; do BAR="${BAR}█"; i=$((i + 1)); done
                    while [ "$i" -lt 10 ]; do BAR="${BAR}░"; i=$((i + 1)); done

                    MSG="💥 *${JOB_NAME}* #${BUILD_NUMBER} — ПРОВАЛ! 🔥%0A%0A"
                    MSG="${MSG}${BAR}  ${PASSED}/${TOTAL} прошли%0A"
                    MSG="${MSG}❌ Провалено: *${FAILED}* | ⚠️ Сломано: *${BROKEN}* | ⏭ Пропущено: *${SKIPPED}*%0A%0A"
                    MSG="${MSG}📊 [Allure отчёт →](${ALLURE_URL})%0A"
                    MSG="${MSG}🖥️ [Консоль + лог](${BASE_URL}consoleFull)"

                    curl -sS -X POST "https://api.telegram.org/bot${TOKEN}/sendMessage" \
                        -d chat_id="${CHAT}" \
                        -d parse_mode="Markdown" \
                        -d disable_web_page_preview=true \
                        --data-urlencode "text=${MSG}" || echo "WARN: TG failed"
                '''
            }
        }
    }
}
