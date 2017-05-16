#!/usr/bin/env groovy

def parse(reportPath) {
    def result = sh(returnStdout: true, script: "xmllint --xpath '//testsuite/@*' ${reportPath}").trim()
    def lstResult = result =~ /([^=]+)="([^"]+)"[\s]*/

    def summary = [:]
    if (lstResult.hasGroup()) {
      for (int i=0; i<lstResult.size(); i++) {
        summary[lstResult[i][1]] = lstResult[i][2]
      }
    }
    return summary
}

// source: https://issues.jenkins-ci.org/browse/JENKINS-27395?focusedCommentId=256459&page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel#comment-256459
def setJUnitPackageName(packageName, inFile, outFile) {
    // Prepend the testName as the package name so that we get sorted output in the
    // Jenkins test results page. The packageName is extracted from the classname
    // attribute of the testcase tag.
    // WARNING: the package attribute of the testcase tag is igonred by Jenkins
    sh "sed \"s/\\(classname=['\\\"]\\)/\\1${packageName}./g\" ${inFile} > ${outFile}"
}

return this
