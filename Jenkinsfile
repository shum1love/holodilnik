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

                    if [ -f .allure-summary.env ]; then
                        . ./.allure-summary.env
                    fi

                    ALLURE_TOTAL=${ALLURE_TOTAL:-0}
                    if [ "$ALLURE_TOTAL" -eq 0 ] && ls target/surefire-reports/TEST-*.xml >/dev/null 2>&1; then
                        read SUREFIRE_TOTAL SUREFIRE_FAILED SUREFIRE_BROKEN SUREFIRE_SKIPPED <<EOF
$(awk '
    /<testsuite / {
        if (match($0, /tests="[0-9]+"/)) {
            tests += substr($0, RSTART + 7, RLENGTH - 8) + 0
        }
        if (match($0, /failures="[0-9]+"/)) {
            failures += substr($0, RSTART + 10, RLENGTH - 11) + 0
        }
        if (match($0, /errors="[0-9]+"/)) {
            errors += substr($0, RSTART + 8, RLENGTH - 9) + 0
        }
        if (match($0, /skipped="[0-9]+"/)) {
            skipped += substr($0, RSTART + 9, RLENGTH - 10) + 0
        }
    }
    END {
        print tests + 0, failures + 0, errors + 0, skipped + 0
    }
' target/surefire-reports/TEST-*.xml)
EOF

                        SUREFIRE_TOTAL=${SUREFIRE_TOTAL:-0}
                        SUREFIRE_FAILED=${SUREFIRE_FAILED:-0}
                        SUREFIRE_BROKEN=${SUREFIRE_BROKEN:-0}
                        SUREFIRE_SKIPPED=${SUREFIRE_SKIPPED:-0}
                        SUREFIRE_PASSED=$((SUREFIRE_TOTAL - SUREFIRE_FAILED - SUREFIRE_BROKEN - SUREFIRE_SKIPPED))
                        if [ "$SUREFIRE_PASSED" -lt 0 ]; then SUREFIRE_PASSED=0; fi

                        {
                            echo "ALLURE_TOTAL=${SUREFIRE_TOTAL}"
                            echo "ALLURE_PASSED=${SUREFIRE_PASSED}"
                            echo "ALLURE_FAILED=${SUREFIRE_FAILED}"
                            echo "ALLURE_BROKEN=${SUREFIRE_BROKEN}"
                            echo "ALLURE_SKIPPED=${SUREFIRE_SKIPPED}"
                        } > .allure-summary.env
                    fi
                fi
            '''

            archiveArtifacts artifacts: 'target/allure-results/**,target/site/allure-maven-plugin/**,.allure-summary.env', allowEmptyArchive: true

            script {
                if (fileExists('.allure-summary.env')) {
                    readFile('.allure-summary.env').trim().split('\n').each { line ->
                        def parts = line.split('=', 2)
                        if (parts.length == 2) {
                            env[parts[0]] = parts[1]
                        }
                    }
                }

                def total = (env.ALLURE_TOTAL ?: '0') as Integer
                def passed = (env.ALLURE_PASSED ?: '0') as Integer
                def failed = (env.ALLURE_FAILED ?: '0') as Integer
                def broken = (env.ALLURE_BROKEN ?: '0') as Integer
                def skipped = (env.ALLURE_SKIPPED ?: '0') as Integer

                def baseUrl = env.BUILD_URL ?: env.RUN_DISPLAY_URL ?: "${env.JOB_URL}${env.BUILD_NUMBER}/"
                if ((env.JENKINS_PUBLIC_URL ?: '').trim() && (env.BUILD_URL ?: '').trim()) {
                    def buildPath = env.BUILD_URL.replaceFirst(/^https?:\\/\\/[^\\/]+/, '')
                    if (buildPath?.trim()) {
                        baseUrl = "${env.JENKINS_PUBLIC_URL.replaceAll('/+$', '')}${buildPath}"
                    }
                }

                if (baseUrl.contains('unconfigured-jenkins-location')) {
                    baseUrl = baseUrl.replaceFirst(/^https?:\/\/[^\/]+/, '')
                }

                if (!baseUrl.endsWith('/')) {
                    baseUrl = "${baseUrl}/"
                }

                def allureUrl = "${baseUrl}allure/"
                def consoleUrl = currentBuild.currentResult == 'FAILURE' ? "${baseUrl}consoleFull" : baseUrl
                def barFilled = total > 0 ? (int) ((passed * 10) / total) : 0
                def bar = ('█' * barFilled) + ('░' * (10 - barFilled))

                env.BUILD_PUBLIC_URL = baseUrl
                env.ALLURE_REPORT_URL = allureUrl
                env.CONSOLE_URL = consoleUrl
                env.PROGRESS_BAR = bar

                def statusLine = currentBuild.currentResult == 'FAILURE' ? '💥 ПРОВАЛ! 🔥' : '🚀 УСПЕХ! ✅'
                currentBuild.description = """${statusLine}<br>
📈 Прогресс: ${bar}<br>
✅ Всего тестов: ${total}<br>
🟢 Пройдено: ${passed}<br>
❌ Провалено: ${failed}<br>
⚠️ Сломано: ${broken}<br>
⏭️ Пропущено: ${skipped}<br>
📊 <a href='${allureUrl}'>Allure отчёт</a><br>
🖥️ <a href='${consoleUrl}'>Консоль</a>"""

                echo "Allure summary => total=${total}, passed=${passed}, failed=${failed}, broken=${broken}, skipped=${skipped}"
                echo "Build links => base=${baseUrl}, allure=${allureUrl}, console=${consoleUrl}"

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

                    MSG=$(cat <<EOT
🚀 <b>${JOB_NAME}</b> #${BUILD_NUMBER} — УСПЕХ! ✅

📈 Прогресс: ${BAR}
✅ Всего тестов: <b>${TOTAL}</b>
🟢 Пройдено: <b>${PASSED}</b>
❌ Провалено: <b>${FAILED}</b>
⚠️ Сломано: <b>${BROKEN}</b>
⏭ Пропущено: <b>${SKIPPED}</b>

📊 <a href="${ALLURE_REPORT_URL}">Allure отчёт</a>
🖥️ <a href="${CONSOLE_URL}">Консоль</a>
EOT
)

                    curl -sS -X POST "https://api.telegram.org/bot${TOKEN}/sendMessage" \
                        -d chat_id="${CHAT}" \
                        -d parse_mode="HTML" \
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

                    MSG=$(cat <<EOT
💥 <b>${JOB_NAME}</b> #${BUILD_NUMBER} — ПРОВАЛ! 🔥

📈 Прогресс: ${BAR}
✅ Всего тестов: <b>${TOTAL}</b>
🟢 Пройдено: <b>${PASSED}</b>
❌ Провалено: <b>${FAILED}</b>
⚠️ Сломано: <b>${BROKEN}</b>
⏭ Пропущено: <b>${SKIPPED}</b>

📊 <a href="${ALLURE_REPORT_URL}">Allure отчёт</a>
🖥️ <a href="${CONSOLE_URL}">Консоль + лог</a>
EOT
)

                    curl -sS -X POST "https://api.telegram.org/bot${TOKEN}/sendMessage" \
                        -d chat_id="${CHAT}" \
                        -d parse_mode="HTML" \
                        -d disable_web_page_preview=true \
                        --data-urlencode "text=${MSG}" || echo "WARN: TG failed"
                '''
            }
        }
    }
}
