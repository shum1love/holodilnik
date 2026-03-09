pipeline {

    agent any

    tools {
        jdk 'jdk17'
        maven 'maven3'
        allure 'allure'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Run tests') {
            steps {
                sh '''
                    mvn clean test \
                    -Dselenide.remote=http://selenoid:4444/wd/hub \
                    -Dselenide.browser=chrome \
                    -Dselenide.browserVersion=128.0 \
                    -Dselenide.headless=true
                '''
            }
        }

    }

    post {
        always {
            allure(
                includeProperties: false,
                jdk: '',
                results: [[path: 'target/allure-results']]
            )
        }
    }

}