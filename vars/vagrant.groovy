#!/usr/bin/env groovy

import jenkins.model.*
import hudson.model.*
import hudson.slaves.*

@NonCPS
def _createNode(nodeId, jenkinsFolderPath) {
    def launcher = new JNLPLauncher()
    def node = new DumbSlave(
        nodeId,
        jenkinsFolderPath,
        launcher
    )
    Jenkins.instance.addNode(node)
}

@NonCPS
def _removeNode(nodeId) {
    def allNodes = Jenkins.getInstance().getNodes()
    for (int i =0; i < allNodes.size(); i++) {
        Slave node = allNodes[i]

        if (node.name.toString() == nodeId) {
            Jenkins.getInstance().removeNode(node)
            return
        }
    }
}

@NonCPS
def _getNodeSecret(nodeId) {
    return jenkins.slaves.JnlpSlaveAgentProtocol.SLAVE_SECRET.mac(nodeId)
}

def inside(String vagrantFilePath, String jenkinsFolderPath, Integer cpu, Integer memory, Integer vnc_port, Boolean rebuild, Boolean debug=false, Closure body) {
    def nodeId = "${env.BUILD_TAG}"
    _createNode(nodeId, jenkinsFolderPath)
    def error

    try {
        def nodeSecret = _getNodeSecret(nodeId)

        withEnv([
            "VAGRANT_VAGRANTFILE=${vagrantFilePath}",
            "NODE_CPU_COUNT=${cpu}",
            "NODE_MEMORY=${memory}",
            "NODE_VNC_PORT=${vnc_port}",
            "NODE_SECRET=${nodeSecret}",
            "NODE_ID=${nodeId}",
        ]) {

            sh 'vagrant halt --force'
            if (rebuild) {
                try {
                    sh 'vagrant destroy --force'
                } catch (e) {
                    echo 'ignoring error on destroy'
                    echo e.getMessage()
                }
            }
            if (debug) {
                sh 'vagrant up --debug --provision'
            } else {
                sh  'vagrant up'
            }
        }

        body(nodeId)
    } catch (e) {
        error = e
    } finally {
        if (!debug) {
            _removeNode(nodeId)
            withEnv(["VAGRANT_VAGRANTFILE=${vagrantFilePath}"]) {
                sh 'vagrant halt --force'
            }
        }
        if (error) {
            throw error
        }
    }
}

return this
