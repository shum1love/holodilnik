pipeline {
    agent any

    parameters {
        string(name: 'TEST_TAG', defaultValue: 'Smoke', description: 'JUnit5 tag to run: Smoke / Cart / UI')
        string(name: 'SELENOID_REMOTE', defaultValue: 'http://selenoid:4444/wd/hub', description: 'Remote WebDriver URL')
        string(name: 'BROWSER_VERSION', defaultValue: '', description: 'Optional browser version (e.g. 126.0). Leave empty to use Selenoid default.')
    }

    stages {
        stage('Smoke') {
            steps {
                withMaven(jdk: 'jdk17', maven: 'maven3') {
                    sh '''
                        set -e

                        MVN_BROWSER_VERSION_ARG=""
                        if [ -n "${BROWSER_VERSION}" ]; then
                          MVN_BROWSER_VERSION_ARG="-Dselenide.browserVersion=${BROWSER_VERSION}"
                        fi

                        mvn clean test \
                            -Dtest=**/*Test \
                            -Djunit.jupiter.tags=${TEST_TAG} \
                            -Dselenide.remote=${SELENOID_REMOTE} \
                            -Dselenide.browser=chrome \
                            ${MVN_BROWSER_VERSION_ARG} \
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
