#!/usr/bin/env groovy

@NonCPS
def getChangeString() {
    MAX_MSG_LEN = 100
    def changeString = ""
    echo "Gathering SCM changes"
    def changeLogSets = currentBuild.changeSets
    for (int i = 0; i < changeLogSets.size(); i++) {
        def entries = changeLogSets[i].items
        for (int j = 0; j < entries.length; j++) {
            def entry = entries[j]
            truncated_msg = entry.msg.take(MAX_MSG_LEN)
            changeString += " - ${truncated_msg} [${entry.author}]\n"
        }
    }
    if (!changeString) {
        changeString = " - No new changes"
    }
    return changeString
}

@NonCPS
def getIssueList() {
    def list = []
    def changes = getChangeString()
    def re = /(AB|DB|EX|ICE|IB)-([0-9])*/
    def y = changes =~ re
    while (y) {
        list.add(y.group().toString())
    }
    echo 'Detected JIRA tickets'
    echo list
    return list
}
