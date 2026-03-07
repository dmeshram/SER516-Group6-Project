pipeline {
    agent any

    options {
        skipDefaultCheckout(true)
        timestamps()
    }

    stages {

        stage('Checkout Source') {
            steps {
                checkout scm
            }
        }

        stage('Build & Verify') {
            agent {
                docker {
                    image 'maven:3.9.6-eclipse-temurin-17'
                    args '-v $JENKINS_HOME/.m2:/root/.m2:z'
                }
            }
            steps {
                sh '''
                   mvn -B -ntp clean verify \
                       -Dmaven.test.failure.ignore=false \
                       -Dmaven.repo.local=/root/.m2/repository
                '''
            }
        }

        stage('Metrics Computation') {
            agent {
                docker {
                    image 'maven:3.9.6-eclipse-temurin-17'
                    args '-v $JENKINS_HOME/.m2:/root/.m2:z'
                }
            }
            steps {
                echo "Running Fan-In / Fan-Out metrics computation..."
                sh '''
                   mkdir -p metrics-output
                   mvn -B -ntp exec:java \
                       -Dexec.args=". metrics-output" \
                       -Dmaven.repo.local=/root/.m2/repository
                '''
            }
        }

        stage('Archive Metrics Artifacts') {
            steps {
                script {
                    if (fileExists('metrics-output')) {
                        echo "Archiving metrics artifacts..."
                        archiveArtifacts artifacts: 'metrics-output/**/*', fingerprint: true
                    } else {
                        error("Metrics output directory not found!")
                    }
                }
            }
        }

        stage('Service Test') {
            steps {
                sh 'docker --version'
                sh 'docker compose version'
                sh 'docker compose up -d --build'
                sh 'sleep 60'
                sh 'bash scripts/service-test.sh'
            }
            post {
                always {
                    sh 'docker compose down || true'
                }
            }
        }
    }

    post {
        always {
            junit testResults: '**/surefire-reports/*.xml',
                  allowEmptyResults: false
            echo "Build result: ${currentBuild.currentResult}"
        }
        success {
            echo "Pipeline completed successfully with metrics generated."
        }
        failure {
            echo "Pipeline FAILED. Check console output above for details."
        }
        unstable {
            echo "Pipeline is UNSTABLE — test failures detected."
        }
    }
}