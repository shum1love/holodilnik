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
            }
        }

        stage('Run tests') {
            steps {
                script {
                    if (!env.TEST_SUITE?.trim()) {
                        error('TEST_SUITE не задан. Укажите TEST_SUITE в конфигурации Jenkins job: smoke, regression или crossbrowser.')
                    }

                    if (env.TEST_SUITE == 'smoke') {
                        sh '''
                            mvn clean test \
                            -Dgroups=smoke \
                            -Dselenide.remote=http://selenoid:4444/wd/hub \
                            -Dselenide.browser=chrome \
                            -Dselenide.browserVersion=128.0 \
                            -Dselenide.headless=true
                        '''
                    } else if (env.TEST_SUITE == 'regression') {
                        sh '''
                            mvn clean test \
                            -Dselenide.remote=http://selenoid:4444/wd/hub \
                            -Dselenide.browser=chrome \
                            -Dselenide.browserVersion=128.0 \
                            -Dselenide.headless=true
                        '''
                    } else if (env.TEST_SUITE == 'crossbrowser') {
                    sh 'mvn clean'
                        parallel(
                            chrome: {
                                sh '''
                                    mvn test \
                                    -Dallure.results.directory=target/allure-results-chrome \
                                    -Dselenide.remote=http://selenoid:4444/wd/hub \
                                    -Dselenide.browser=chrome \
                                    -Dselenide.browserVersion=128.0 \
                                    -Dselenide.headless=true
                                '''
                            },
                            firefox: {
                                sh '''
                                    mvn test \
                                    -Dallure.results.directory=target/allure-results-firefox \
                                    -Dselenide.remote=http://selenoid:4444/wd/hub \
                                    -Dselenide.browser=firefox \
                                    -Dselenide.headless=true
                                '''
                            }
                        )
                    } else {
                        error("Неизвестный TEST_SUITE='${env.TEST_SUITE}'. Допустимые значения: smoke, regression, crossbrowser.")
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
                results: [
                    [path: 'target/allure-results'],
                    [path: 'target/allure-results-chrome'],
                    [path: 'target/allure-results-firefox']
                ]
            )
        }
    }

}
