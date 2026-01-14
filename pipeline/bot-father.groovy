@Library('share_library@master') _

pipeline {
    agent any

    environment {
        CHAT_TOKEN = '8512003176:AAFfd1SjIQK0GQcXEW4S-aczsL1ngJzD59A'
        CHAT_ID = '1714755240'
    }

    stages {
        stage('Send Message') {
            script {
                def message = """
                *Greeting From Jenkins*
your alert channel is successfully configured\\. 
                """
                sendTelegramMessage("${message}","${CHAT_TOKEN}","${CHAT_ID}")
            }
        }
    }
}