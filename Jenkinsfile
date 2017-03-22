def dockerImageName = 'jenkins-pipeline-library'

node('docker') {
    stage('checkout') {
        checkout scm
    }

    def dockerImage
    stage('build docker image') {
        def uid = sh(returnStdout: true, script: 'id -u').trim()
        def gid = sh(returnStdout: true, script: 'id -g').trim()

        dockerImage = docker.build(
            dockerImageName,
            "--build-arg UID=${uid} --build-arg GID=${gid} .")
    }

    stage('run tests') {
        docker.image(dockerImage.imageName()).inside() {
            sh('echo test')
        }
    }
}
