pipeline {
    agent any

    environment {
        TEST_TAG = 'Smoke'
        SELENOID_REMOTE = 'http://selenoid:4444/wd/hub'
        BROWSER_VERSION = '126.0'
    }

    stages {
        stage('Smoke') {
            steps {
                withMaven(jdk: 'jdk17', maven: 'maven3') {
                    sh '''
                        set -e
                        echo "Using TEST_TAG=${TEST_TAG}"
                        echo "Using SELENOID_REMOTE=${SELENOID_REMOTE}"
                        echo "Using BROWSER_VERSION=${BROWSER_VERSION}"

                        mvn -B clean test \
                          -Dtest=**/*Test \
                          -Djunit.jupiter.tags=${TEST_TAG} \
                          -Dselenide.remote=${SELENOID_REMOTE} \
                          -Dselenide.browser=chrome \
                          -Dselenide.browserVersion=${BROWSER_VERSION} \
                          -Dselenide.timeout=15000 \
                          -Dselenide.headless=true \
                          -Dselenide.browserSize=1920x1080 \
                          -Dselenide.chromeoptions.args=--no-sandbox,--disable-dev-shm-usage
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
