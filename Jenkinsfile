pipeline {

    agent any

    tools {
        jdk 'jdk17'
        maven 'maven3'
        allure 'allure'
    }

    parameters {
        string(name: 'TEST_CLASS', defaultValue: '', description: 'Тестовый класс(ы) для manual запуска (через запятую)')
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
                    if (env.TEST_SUITE == 'manual') {
                        echo "Selected classes: ${params.TEST_CLASS ?: 'не указан — упадёт ниже'}"
                    }
                }

            }
        }

        stage('Run tests') {
            steps {

                script {

                    if (!env.TEST_SUITE?.trim()) {
                        error('TEST_SUITE не задан. Допустимые: smoke, regression, manual')
                    }

                    if (env.TEST_SUITE == 'smoke') {

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

                    } else if (env.TEST_SUITE == 'regression') {

                        sh '''
                            mvn clean test \
                            -Dselenide.remote=http://selenoid:4444/wd/hub \
                            -Dselenide.browser=chrome \
                            -Dselenide.browserVersion=128.0 \
                            -Dselenide.headless=true \
                            -Dselenide.remoteConnectionTimeout=120000 \
                            -Dselenide.remoteReadTimeout=120000
                        '''

                    } else if (env.TEST_SUITE == 'manual') {

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