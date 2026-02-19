pipeline {
    agent {
        docker {
            image 'maven:3.9.6-eclipse-temurin-17'
        }
    }

    stages {
        stage('Build') {
            steps {
                dir('SER516-Group6-Project') {
                    sh 'mvn clean compile'
                }
            }
        }

        stage('Test') {
            steps {
                dir('SER516-Group6-Project') {
                    sh 'mvn test'
                }
            }
        }
    }
}