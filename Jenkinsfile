pipeline {
    agent any

    tools {
        jdk 'jdk17'
        maven 'maven3'
    }

    triggers {
        cron('H 2 * * *')
    }

    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/твой_юзер/твой_репо.git',
                    branch: 'main'
            }
        }
        stage('Run UI Tests') {
            steps {
                sh 'mvn clean test -Dgroups=ui'
            }
        }
    }

    post {
        always {
            allure([
                includeProperties: false,
                jdk: '',
                results: [[path: 'allure-results']]
            ])
        }
    }
}