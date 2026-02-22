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
                            -Dselenide.remote=http://selenoid:4444/wd/hub \
                            -Dselenide.browser=chrome \
                            -Dselenide.timeout=15000 \
                            -Dselenide.headless=true \
                            -Dselenide.browserSize=1920x1080 \
                            -Dselenide.chromeoptions.args=--no-sandbox,--disable-dev-shm-usage,--disable-gpu,--disable-software-rasterizer
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