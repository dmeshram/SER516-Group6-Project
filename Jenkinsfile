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
                   mvn -B -ntp clean verify \
                       -Dmaven.test.failure.ignore=false
                '''
            }
        }

        stage('Metrics Computation') {
            steps {
                script {
                    echo "Running Fan-In / Fan-Out metrics computation..."

                    sh '''
                       mkdir -p metrics-output

                       java -cp target/*.jar \
                       edu.asu.ser516.metrics.FanOutComputerMain \
                       . json metrics-output
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

    post {
        always {
            junit 'target/surefire-reports/*.xml'
        }
        success {
            echo "Pipeline completed successfully with metrics generated."
        }
        failure {
            echo "Pipeline failed."
        }
    }
}