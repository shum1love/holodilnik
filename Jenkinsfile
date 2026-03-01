pipeline {
    agent any

    tools {
        jdk 'jdk17'
        maven 'maven3'
        allure 'allure'
    }

    stages {
        stage('Init Job Properties') {
            steps {
                script {
                    // Ensure legacy job parameters are removed so Jenkins no longer asks for SELENOID_URL.
                    properties([
                        pipelineTriggers([cron('H 2 * * *')])
                    ])
                }
            }
        }

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
                    if [ -n "${SELENOID_URL_OVERRIDE}" ]; then
                      echo "${SELENOID_URL_OVERRIDE}"
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
                    echo "Try setting SELENOID_URL_OVERRIDE environment variable manually, e.g. http://host.docker.internal:4444/wd/hub"
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
            script {
                allure([
                    results: [[path: 'allure-results']]
                ])
            }
        }
    }
}
