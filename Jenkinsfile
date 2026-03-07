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
                git branch: 'main', url: 'https://github.com/shum1love/holodilnik.git'
            }
        }

        stage('Detect Selenoid') {
            steps {
                sh '''
                if curl -s http://host.docker.internal:4444/status > /dev/null
                then
                  echo "SELENOID=http://host.docker.internal:4444/wd/hub" > selenoid.env
                else
                  echo "SELENOID=http://localhost:4444/wd/hub" > selenoid.env
                fi
                '''
            }
        }

        stage('Run UI tests') {
            steps {
                sh '''
                . ./selenoid.env
                mvn -B clean test \
                  -Dgroups=UI \
                  -Dselenide.remote=$SELENOID
                '''
            }
        }

        stage('Generate Allure') {
            steps {
                sh '''
                $ALLURE_HOME/bin/allure generate \
                target/allure-results \
                -o allure-report \
                --clean
                '''
            }
        }

        stage('Publish Allure') {
            steps {
                publishHTML([
                        reportDir: 'allure-report',
                        reportFiles: 'index.html',
                        reportName: 'Allure Report',
                        keepAll: true,
                        alwaysLinkToLastBuild: true,
                        allowMissing: true
                ])
            }
        }
    }

    post {

        always {

            script {

                if (!fileExists('target/surefire-reports')) {
                    echo "No test results found"
                    return
                }

                def stats = sh(
                        script: """
                        awk '
                        /<testsuite /{
                        match(\$0,/tests="[0-9]+"/);t+=substr(\$0,RSTART+7,RLENGTH-8)
                        match(\$0,/failures="[0-9]+"/);f+=substr(\$0,RSTART+10,RLENGTH-11)
                        match(\$0,/errors="[0-9]+"/);e+=substr(\$0,RSTART+8,RLENGTH-9)
                        match(\$0,/skipped="[0-9]+"/);s+=substr(\$0,RSTART+9,RLENGTH-10)
                        }
                        END{print t,f,e,s}
                        ' target/surefire-reports/*.xml
                        """,
                        returnStdout: true
                ).trim().split(" ")

                def total = stats[0] as int
                def failures = stats[1] as int
                def errors = stats[2] as int
                def skipped = stats[3] as int

                def passed = total - failures - errors - skipped
                def rate = total > 0 ? (passed * 100 / total) : 0

                def statusEmoji = currentBuild.currentResult == "SUCCESS"
                        ? "УСПЕХ! ✅"
                        : "ПАДЕНИЕ! ❌"

                def message = """
🚀 ${env.JOB_NAME} #${env.BUILD_NUMBER} — ${statusEmoji}

📊 Результаты тестов

📈 Доля пройденных: ${rate}% (${passed}/${total})

🧪 Всего тестов: ${total}
🟢 Пройдено: ${passed}
❌ Провалено: ${failures}
⚠️ Ошибки: ${errors}
⏭️ Пропущено: ${skipped}

📎 Jenkins отчёт:
${env.BUILD_URL}
"""

                withCredentials([
                        string(credentialsId: 'telegram-bot-token', variable: 'TOKEN'),
                        string(credentialsId: 'telegram-chat-id', variable: 'CHAT')
                ]) {
                    sh """
                    curl -s -X POST https://api.telegram.org/bot\$TOKEN/sendMessage \
                    -d chat_id=\$CHAT \
                    --data-urlencode text="${message}"
                    """
                }
            }
        }
    }
}