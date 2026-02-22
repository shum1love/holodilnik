pipeline {
    agent {
        docker {
            image 'maven:3.9.9-eclipse-temurin-17'
            args '-v /var/run/docker.sock:/var/run/docker.sock'
            reuseNode true
        }
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                sh 'ls -la'
                sh 'pwd'
            }
        }

        stage('Запуск Smoke') {
            steps {
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

    post {
        always {
            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
        }
    }
}