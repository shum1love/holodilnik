pipeline {
    agent any

    parameters {
        string(name: 'SELENOID_URL', defaultValue: 'http://host.docker.internal:4444/wd/hub', description: 'Remote WebDriver URL (Selenoid endpoint)')
    }

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
                  SELENOID_URL_EFFECTIVE="${SELENOID_URL:-http://host.docker.internal:4444/wd/hub}"
                  echo "Using Selenoid URL: ${SELENOID_URL_EFFECTIVE}"
                  SELENOID_STATUS_URL="${SELENOID_URL_EFFECTIVE%/wd/hub}/status"
                  echo "Checking Selenoid status at: ${SELENOID_STATUS_URL}"
                  curl -fsS "${SELENOID_STATUS_URL}"
                '''
            }
        }

        stage('Run UI Tests') {
            steps {
                sh '''
                  SELENOID_URL_EFFECTIVE="${SELENOID_URL:-http://host.docker.internal:4444/wd/hub}"
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
