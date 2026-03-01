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
        ALLURE_RESULTS = 'target/allure-results'
        ALLURE_REPORT = 'target/allure-report'
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
              if [ -d "${ALLURE_RESULTS}" ]; then
                cat > "${ALLURE_RESULTS}/environment.properties" <<EOT
Jenkins.Job=${JOB_NAME}
Jenkins.BuildNumber=${BUILD_NUMBER}
Jenkins.Branch=${GIT_BRANCH}
Jenkins.Commit=${GIT_COMMIT}
EOT

                cat > "${ALLURE_RESULTS}/executor.json" <<EOT
{
  "name": "Jenkins",
  "type": "jenkins",
  "buildName": "${JOB_NAME} #${BUILD_NUMBER}",
  "buildUrl": "${RUN_DISPLAY_URL:-${BUILD_URL}}",
  "reportUrl": "${RUN_DISPLAY_URL:-${BUILD_URL}}allure/"
}
EOT

                mvn -B allure:report \
                  -Dallure.results.directory=${ALLURE_RESULTS#target/} \
                  -Dallure.report.directory=${ALLURE_REPORT#target/}

                if [ -f "${ALLURE_REPORT}/widgets/summary.json" ]; then
                  python3 - <<'PY' > .allure-summary.env
import json
from pathlib import Path
s = json.loads(Path('target/allure-report/widgets/summary.json').read_text(encoding='utf-8')).get('statistic', {})
print(f"ALLURE_TOTAL={s.get('total', 0)}")
print(f"ALLURE_PASSED={s.get('passed', 0)}")
print(f"ALLURE_FAILED={s.get('failed', 0)}")
print(f"ALLURE_BROKEN={s.get('broken', 0)}")
print(f"ALLURE_SKIPPED={s.get('skipped', 0)}")
PY
                fi
              fi
            '''

            archiveArtifacts artifacts: 'target/allure-results/**,target/allure-report/**,.allure-summary.env', allowEmptyArchive: true

            script {
                if (fileExists('.allure-summary.env')) {
                    readFile('.allure-summary.env').trim().split('\n').each { line ->
                        def (k, v) = line.tokenize('=')
                        env[k] = v
                    }
                }
                if (fileExists('target/allure-results')) {
                    allure([results: [[path: 'target/allure-results']]])
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
                    MSG="<b>✅ ${JOB_NAME} #${BUILD_NUMBER}</b>%0A"
                    MSG="${MSG}passed: <b>${ALLURE_PASSED:-0}</b>, failed: <b>${ALLURE_FAILED:-0}</b>, broken: <b>${ALLURE_BROKEN:-0}</b>, skipped: <b>${ALLURE_SKIPPED:-0}</b>%0A"
                    MSG="${MSG}<a href=\"${BASE_URL}allure/\">Allure</a> | <a href=\"${BASE_URL}\">Build</a>"

                    curl -sS -X POST "https://api.telegram.org/bot${TOKEN}/sendMessage" \
                      -d chat_id="${CHAT}" \
                      -d parse_mode="HTML" \
                      -d disable_web_page_preview=true \
                      --data-urlencode "text=${MSG}" >/dev/null || true
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
                    MSG="<b>❌ ${JOB_NAME} #${BUILD_NUMBER}</b>%0A"
                    MSG="${MSG}passed: <b>${ALLURE_PASSED:-0}</b>, failed: <b>${ALLURE_FAILED:-0}</b>, broken: <b>${ALLURE_BROKEN:-0}</b>, skipped: <b>${ALLURE_SKIPPED:-0}</b>%0A"
                    MSG="${MSG}<a href=\"${BASE_URL}allure/\">Allure</a> | <a href=\"${BASE_URL}consoleFull\">Console</a>"

                    curl -sS -X POST "https://api.telegram.org/bot${TOKEN}/sendMessage" \
                      -d chat_id="${CHAT}" \
                      -d parse_mode="HTML" \
                      -d disable_web_page_preview=true \
                      --data-urlencode "text=${MSG}" >/dev/null || true
                '''
            }
        }
    }
}
