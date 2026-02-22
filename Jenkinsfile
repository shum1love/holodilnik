pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Smoke') {
            steps {
                withMaven(jdk: 'jdk17', maven: 'maven3') {
                    sh '''
                        mvn clean test \
                            -Dtest=**/*Test \
                            -Djunit.jupiter.tags=Smoke \
                            -Dselenide.headless=true \
                            -Dselenide.browser=chrome \
                            -Dselenide.timeout=15000
                    '''
                }
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
        }
    }
}