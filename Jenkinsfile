pipeline {

    agent any

    tools {
        jdk 'jdk17'
        maven 'maven3'
        allure 'allure'
    }

    environment {
        ALLURE_RESULTS = 'target/allure-results'
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/shum1love/holodilnik.git'
            }
        }

        stage('Detect Selenoid') {
            steps {
                sh '''
                for url in \
                    http://host.docker.internal:4444/wd/hub \
                    http://selenoid:4444/wd/hub \
                    http://localhost:4444/wd/hub
                do
                    if curl -s ${url%/wd/hub}/status >/dev/null ; then
                        echo "SELENOID=$url" > selenoid.env
                        exit 0
                    fi
                done

                echo "Selenoid not found"
                exit 1
                '''
            }
        }

        stage('Run UI tests') {
            steps {
                sh '''
                . ./selenoid.env
                mvn -B clean test -Dgroups=UI -Dselenide.remote=$SELENOID
                '''
            }
        }

        stage('Generate Allure') {
            steps {
                sh '''
                if command -v allure >/dev/null 2>&1; then
                    allure generate target/allure-results -o allure-report --clean
                else
                    echo "Allure CLI not installed, skipping report"
                fi
                '''
            }
        }

        stage('Publish Allure') {
            when {
                expression { fileExists('allure-report/index.html') }
            }
            steps {
                publishHTML([
                    allowMissing: true,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'allure-report',
                    reportFiles: 'index.html',
                    reportName: 'Allure'
                ])
            }
        }
    }

    post {

        always {

            script {

                def total = 0
                def failed = 0
                def skipped = 0
                def passed = 0

                if (fileExists('target/surefire-reports')) {

                    def stats = sh(
                        script: """
                        awk '
                        /<testsuite /{
                        match(\$0,/tests="[0-9]+"/);t+=substr(\$0,RSTART+7,RLENGTH-8)
                        match(\$0,/failures="[0-9]+"/);f+=substr(\$0,RSTART+10,RLENGTH-11)
                        match(\$0,/errors="[0-9]+"/);e+=substr(\$0,RSTART+8,RLENGTH-9)
                        match(\$0,/skipped="[0-9]+"/);s+=substr(\$0,RSTART+9,RLENGTH-10)
                        }
                        END{print t,f+e,s}
                        ' target/surefire-reports/TEST-*.xml
                        """,
                        returnStdout: true
                    ).trim()

                    def p = stats.split(" ")

                    total = p[0] as Integer
                    failed = p[1] as Integer
                    skipped = p[2] as Integer
                    passed = total - failed - skipped
                }

                def passRate = 0
                if (total > 0) {
                    passRate = (passed * 100) / total
                }

                env.TEST_TOTAL = total.toString()
                env.TEST_FAILED = failed.toString()
                env.TEST_PASSED = passed.toString()
                env.TEST_SKIPPED = skipped.toString()
                env.PASS_RATE = passRate.toString()

                currentBuild.description = """
Pass rate: ${passRate}%
Passed: ${passed}
Failed: ${failed}
Skipped: ${skipped}
"""
            }
        }

        success {
            telegramNotify("SUCCESS")
        }

        failure {
            telegramNotify("FAIL")
        }
    }
}

def telegramNotify(status) {

    withCredentials([
        string(credentialsId: 'telegram-bot-token', variable: 'TOKEN'),
        string(credentialsId: 'telegram-chat-id', variable: 'CHAT')
    ]) {

        sh """
        MSG="${status}

${JOB_NAME} #${BUILD_NUMBER}

Pass rate: ${PASS_RATE}% (${TEST_PASSED}/${TEST_TOTAL})

Failed: ${TEST_FAILED}
Skipped: ${TEST_SKIPPED}

${env.BUILD_URL}
"

        curl -s -X POST https://api.telegram.org/bot\$TOKEN/sendMessage \
        -d chat_id=\$CHAT \
        --data-urlencode text="\$MSG"
        """
    }
}