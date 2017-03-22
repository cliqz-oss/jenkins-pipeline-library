#!/usr/bin/env groovy

def parse(Map vars) {
    if (vars == null || !vars.reportPath) {
        error 'Parameter <reportPath> is required for parseJunitReport'
    }

    def result = sh(returnStdout: true, script: "xmllint --xpath '//testsuite/@*' ${vars.reportPath}").trim()
    def lstResult = result =~ /([^=]+)="([^"]+)"[\s]*/

    def summary = [:]
    if (lstResult.hasGroup()) {
      for (int i=0; i<lstResult.size(); i++) {
        summary[lstResult[i][1]] = lstResult[i][2]
      }
    }
    return summary
}

return this