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
                    script {
                        def testTag = params.TEST_TAG?.trim() ? params.TEST_TAG.trim() : 'Smoke'
                        def selenoidRemote = params.SELENOID_REMOTE?.trim() ? params.SELENOID_REMOTE.trim() : 'http://selenoid:4444/wd/hub'
                        def browserVersionArg = params.BROWSER_VERSION?.trim() ? "-Dselenide.browserVersion=${params.BROWSER_VERSION.trim()}" : ''

                        sh """
                            set -e
                            echo "Using TEST_TAG=${testTag}"
                            echo "Using SELENOID_REMOTE=${selenoidRemote}"
                            if [ -n "${browserVersionArg}" ]; then
                              echo "Using browser version from parameter"
                            else
                              echo "Using Selenoid default browser version"
                            fi

                            mvn clean test \
                                -Dtest=**/*Test \
                                -Djunit.jupiter.tags=${testTag} \
                                -Dselenide.remote=${selenoidRemote} \
                                -Dselenide.browser=chrome \
                                ${browserVersionArg} \
                                -Dselenide.timeout=15000 \
                                -Dselenide.headless=true \
                                -Dselenide.browserSize=1920x1080 \
                                -Dselenide.chromeoptions.args=--no-sandbox,--disable-dev-shm-usage,--disable-gpu,--disable-software-rasterizer
                        """
                    }
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
