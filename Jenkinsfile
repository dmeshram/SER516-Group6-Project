pipeline {
    agent {
        docker {
            image 'maven:3.9.6-eclipse-temurin-17'
            args '-v $HOME/.m2:/root/.m2'
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
                   mvn -B -ntp \
                       clean verify \
                       -Dmaven.test.failure.ignore=false
                '''
            }
        }
    }

    post {
        always {
            junit 'target/surefire-reports/*.xml'
        }
        success {
            echo "Build and verification completed successfully."
        }
        failure {
            echo "Build failed. Please check logs."
        }
    }
}