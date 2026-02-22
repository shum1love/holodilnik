pipeline {
    agent {
        docker {
            image 'selenium/standalone-chrome:131.0-20250222'  // свежий на февраль 2026, можно взять latest
            args '--shm-size=2g --user root'  // shm-size для Chrome, root чтобы избежать правовых проблем
            reuseNode true
        }
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                sh 'ls -la'
            }
        }

        stage('Smoke') {
            steps {
                sh '''
                    mvn clean test \
                        -Dtest=**/*Test \
                        -Djunit.jupiter.tags=Smoke \
                        -Dselenide.headless=true \
                        -Dselenide.browser=chrome \
                        -Dselenide.timeout=15000 \
                        -Dwebdriver.chrome.driver=/opt/selenium/chromedriver
                '''
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
        }
    }
}