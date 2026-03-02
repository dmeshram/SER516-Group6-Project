pipeline {
    agent {
        docker {
            image 'maven:3.9.6-eclipse-temurin-17'
            args '-v $JENKINS_HOME/.m2:/root/.m2:z'
        }
    }

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
            steps {
                sh '''
                   mvn -B -ntp clean verify \
                       -Dmaven.test.failure.ignore=false
                       -Dmaven.repo.local=/root/.m2/repository
                '''
            }
        }

        stage('Metrics Computation') {
            steps {
                script {
                    echo "Running Fan-In / Fan-Out metrics computation..."
                    sh '''
                       mkdir -p metrics-output
                       mvn -B -ntp exec:java \
                           -Dexec.mainClass="edu.asu.ser516.metrics.FanOutComputerMain" \
                           -Dexec.args=". json metrics-output"
                    '''
                }
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
            echo "Pipeline failed."
        }
        unstable {
            echo "Pipeline is UNSTABLE — test failures detected."
        }
    }
}