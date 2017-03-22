/**
 *  Pipeline to test jenkins-pipeline-library
 */

properties([
    buildDiscarder(
    logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '10', daysToKeepStr: '', numToKeepStr: '10')),
    disableConcurrentBuilds(),
    [$class: 'GithubProjectProperty',  displayName: '',
        projectUrlStr: 'https://github.com/cliqz-oss/jenkins-pipeline-library'],
    [$class: 'JobRestrictionProperty'],
    parameters([
        string(defaultValue: 'ubuntu && docker',
            description: 'The default jenkins agent to build on', name: 'BUILD_AGENT')]),
    pipelineTriggers([])
])

def imgName = 'jenkins-pipeline-library'
def img = null

node(params.BUILD_AGENT) {
    timestamps {
        stage('Checkout') {
            checkout scm
        }

        stage('Build') {
            def uid = sh(returnStdout: true, script: 'id -u').trim()
            def gid = sh(returnStdout: true, script: 'id -g').trim()

            img = docker.build(
                imgName,
                "--build-arg UID=${uid} --build-arg GID=${gid} .")
        }

        stage('Test') {
            img.inside() {
                summary = [errors:'0', failures:'1', name:'pytest', skips:'0', tests:'1', time:'0.838']
                xunit = load('vars/xunit.groovy')
                result = xunit.parse(reportPath: 'resources/xunit.xml')
                assert result.equals(summary)
            }
        }
    }
}
