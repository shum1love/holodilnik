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

    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/shum1love/holodilnik.git',
                    branch: 'main'
            }
        }

        stage('Check Selenoid') {
            steps {
                sh '''
                  detect_selenoid_url() {
                    if [ -n "${SELENOID_URL}" ]; then
                      echo "${SELENOID_URL}"
                      return 0
                    fi

                    for candidate in \
                      http://host.docker.internal:4444/wd/hub \
                      http://selenoid:4444/wd/hub \
                      http://localhost:4444/wd/hub
                    do
                      status_url="${candidate%/wd/hub}/status"
                      if curl -fsS --max-time 3 "${status_url}" >/dev/null; then
                        echo "${candidate}"
                        return 0
                      fi
                    done

                    return 1
                  }

                  SELENOID_URL_EFFECTIVE="$(detect_selenoid_url)" || {
                    echo "Cannot detect reachable Selenoid endpoint."
                    echo "Try setting SELENOID_URL environment variable manually, e.g. http://host.docker.internal:4444/wd/hub"
                    exit 1
                  }

                  echo "Using Selenoid URL: ${SELENOID_URL_EFFECTIVE}"
                  SELENOID_STATUS_URL="${SELENOID_URL_EFFECTIVE%/wd/hub}/status"
                  echo "Checking Selenoid status at: ${SELENOID_STATUS_URL}"
                  curl -fsS "${SELENOID_STATUS_URL}"

                  echo "SELENOID_URL_EFFECTIVE=${SELENOID_URL_EFFECTIVE}" > .selenoid.env
                '''
            }
        }

        stage('Run UI Tests') {
            steps {
                sh '''
                  . ./.selenoid.env
                  mvn clean test -Dgroups=UI -Dselenide.remote=${SELENOID_URL_EFFECTIVE}
                '''
            }
        }
    }


    post {
        always {
            sh '''
              if [ -d allure-results ]; then
                mvn -B allure:report
              else
                echo "allure-results directory not found, skipping static report generation"
              fi
            '''

            archiveArtifacts artifacts: 'allure-results/**,allure-report/**', allowEmptyArchive: true

            script {
                if (fileExists('allure-results')) {
                    allure([
                        results: [[path: 'allure-results']]
                    ])
                }

                if (fileExists('allure-report/index.html')) {
                    echo "Allure HTML report: ${env.BUILD_URL}artifact/allure-report/index.html"
                }
            }
        }

        success {
            sh '''
              if [ -n "${TELEGRAM_BOT_TOKEN}" ] && [ -n "${TELEGRAM_CHAT_ID}" ]; then
                curl -sS -X POST "https://api.telegram.org/bot${TELEGRAM_BOT_TOKEN}/sendMessage" \
                  -d chat_id="${TELEGRAM_CHAT_ID}" \
                  --data-urlencode text="✅ ${JOB_NAME} #${BUILD_NUMBER} passed\n${BUILD_URL}" >/dev/null
              else
                echo "Telegram vars are not set. Skipping success notification."
              fi
            '''
        }

        failure {
            sh '''
              if [ -n "${TELEGRAM_BOT_TOKEN}" ] && [ -n "${TELEGRAM_CHAT_ID}" ]; then
                curl -sS -X POST "https://api.telegram.org/bot${TELEGRAM_BOT_TOKEN}/sendMessage" \
                  -d chat_id="${TELEGRAM_CHAT_ID}" \
                  --data-urlencode text="❌ ${JOB_NAME} #${BUILD_NUMBER} failed\n${BUILD_URL}" >/dev/null
              else
                echo "Telegram vars are not set. Skipping failure notification."
              fi
            '''
        }
    }
}
