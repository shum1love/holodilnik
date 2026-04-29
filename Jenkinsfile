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

        stage('Show test suite') {
            steps {

                echo "Running TEST_SUITE=${env.TEST_SUITE}"

                script {
                    def testSuite = env.TEST_SUITE?.trim()?.toLowerCase()
                    if (testSuite == 'manual') {
                        echo "Selected classes: ${params.TEST_CLASS ?: 'не указан — упадёт ниже'}"
                    }
                }

            }
        }

        stage('Run tests') {
            steps {

                script {
                    def testSuite = env.TEST_SUITE?.trim()?.toLowerCase()

                    if (!testSuite) {
                        error('TEST_SUITE не задан. Допустимые: smoke, regression, manual')
                    }

                    if (testSuite == 'smoke') {

                        sh '''
                            mvn clean test \
                            -Dgroups=smoke \
                            -Dselenide.remote=http://selenoid:4444/wd/hub \
                            -Dselenide.browser=chrome \
                            -Dselenide.browserVersion=128.0 \
                            -Dselenide.headless=true \
                            -Dselenide.remoteConnectionTimeout=120000 \
                            -Dselenide.remoteReadTimeout=120000
                        '''

                    } else if (testSuite == 'regression') {

                        sh '''
                            mvn clean test \
                            -Dselenide.remote=http://selenoid:4444/wd/hub \
                            -Dselenide.browser=chrome \
                            -Dselenide.browserVersion=128.0 \
                            -Dselenide.headless=true \
                            -Dselenide.remoteConnectionTimeout=120000 \
                            -Dselenide.remoteReadTimeout=120000
                        '''

                    } else if (testSuite == 'manual') {

                        if (!params.TEST_CLASS?.trim()) {
                            error('Для manual режима ОБЯЗАТЕЛЬНО укажи параметр TEST_CLASS!')
                        }

                        def classes = params.TEST_CLASS.trim().replaceAll(/\s+/, '')

                        sh """
                            mvn clean test \
                            -Dtest=${classes} \
                            -Dselenide.remote=http://selenoid:4444/wd/hub \
                            -Dselenide.browser=chrome \
                            -Dselenide.browserVersion=128.0 \
                            -Dselenide.headless=true \
                            -Dselenide.remoteConnectionTimeout=120000 \
                            -Dselenide.remoteReadTimeout=120000
                        """

                    } else {

                        error("Неизвестный TEST_SUITE='${env.TEST_SUITE}'")

                    }

                }

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
