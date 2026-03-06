pipeline {
    agent {
        docker {
            image 'maven:3.9.6-eclipse-temurin-17'
        }
    }

    stages {
        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Service Test') {
            steps {
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
}