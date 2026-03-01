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

    environment {
        SELENOID_URL = 'http://selenoid:4444/wd/hub'
    }

    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/shum1love/holodilnik.git',
                    branch: 'main'
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
