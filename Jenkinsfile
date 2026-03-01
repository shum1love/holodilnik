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
                  echo "Using Selenoid URL: ${SELENOID_URL}"
                  SELENOID_STATUS_URL="${SELENOID_URL%/wd/hub}/status"
                  echo "Checking Selenoid status at: ${SELENOID_STATUS_URL}"
                  curl -fsS "${SELENOID_STATUS_URL}"
                '''
            }
        }

        stage('Run UI Tests') {
            steps {
                sh 'mvn clean test -Dgroups=UI -Dselenide.remote=${SELENOID_URL}'
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
