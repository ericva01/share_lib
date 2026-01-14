@Library('share_library@master') _

pipeline {
    agent any

    environment {
        IMAGE_NAME = "jenkins-app-devop10"
        REPO_NAME  = "share-library"
        TAG        = "v1.0.${BUILD_NUMBER}"
    }

    stages {

        stage('Clone Code') {
            steps {
                git "https://github.com/sexymanalive/reactjs-devop10-template"
                sh "ls -lrt"
            }
        }

        stage('Prepare Dockerfile (Auto Detect)') {
            steps {
                script {
                    def dockerfileResource = ""

                    if (fileExists('package.json')) {
                        echo "✅ ReactJS project detected"
                        dockerfileResource = "reactjs/dev.Dockerfile"

                    } else if (fileExists('pom.xml')) {
                        echo "✅ Spring Boot project detected"
                        dockerfileResource = "spring/dev.Dockerfile"

                    } else {
                        error "❌ Cannot detect project type"
                    }

                    writeFile(
                        file: "Dockerfile",
                        text: libraryResource(dockerfileResource)
                    )
                }
            }
        }

        stage('Build Image') {
            steps {
                sh """
                docker build -t ${REPO_NAME}/${IMAGE_NAME}:${TAG} .
                """
            }
        }

        stage('Push to DockerHub') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'DOCKERHUB-CRED',
                        usernameVariable: 'DOCKERHUB_USER',
                        passwordVariable: 'DOCKERHUB_PASS'
                    )
                ]) {
                    sh '''
                        echo "$DOCKERHUB_PASS" | docker login -u "$DOCKERHUB_USER" --password-stdin
                        docker push share-library/jenkins-app-devop10:v1.0.10
                    '''
                }
            }
        }


        stage('Run Service') {
            steps {
                sh """
                docker stop app-cont || true
                docker rm app-cont || true

                docker run -dp 3000:8080 \
                --name app-cont \
                ${REPO_NAME}/${IMAGE_NAME}:${TAG}
                """
            }
        }
    }
}
