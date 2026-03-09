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
                sh 'mvn clean test'
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