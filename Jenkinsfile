pipeline {
    agent any

    tools {
        gradle "gradle"
    }

    stages {
        stage("Checkout") {
            steps {
                checkout scm
            }
        }
        stage("Compile") {
            steps {
                sh 'gradle classes --info'
            }
        }
        stage("Package") {
            steps {
                sh 'gradle build copyRuntimeLibs --info'
            }
        }
        stage("Compile test") {
            steps {
                sh 'gradle testClasses --info'
            }
        }
        stage("Unit test") {
            steps {
                sh 'gradle test --info'
            }
        }
        stage("Build docker image") {
            steps {
                sh "docker build -t gfriendmedianews ."
            }
        }
        stage("Update prod environment") {
            steps {
                sh "docker stop gfriendmedianews-prod-app && docker rm gfriendmedianews-prod-app || true"
                sh "docker run -d --restart unless-stopped --name gfriendmedianews-prod-app  --volume /data/gfmn/prod:/opt/gfriendmedianews/var --net gfriendmedianews-prod-net gfriendmedianews"
            }
        }
    }
}
