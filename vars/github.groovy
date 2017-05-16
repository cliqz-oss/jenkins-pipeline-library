#!/usr/bin/env groovy

import groovy.json.JsonSlurperClassic
import jenkins.scm.api.SCMRevision;
import jenkins.scm.api.SCMRevisionAction;
import jenkins.plugins.git.AbstractGitSCMSource;
import org.jenkinsci.plugins.github_branch_source.PullRequestSCMRevision;


def setCommitStatus(token, repo, commit, context, status, message) {
  def apiUrl = new URL("https://api.github.com/repos/${repo}/statuses/${commit}")
  echo "GITHUB API - url: ${apiUrl}"

  try {
    def HttpURLConnection connection = apiUrl.openConnection()
    connection.setRequestProperty('Authorization', "token ${token}")
    connection.setRequestMethod('POST')
    connection.setDoOutput(true)
    connection.connect()

    def body = """
    {
      "state": "${status}",
      "target_url": "${env.BUILD_URL}",
      "description": "${message}",
      "context": "${context}"
    }
    """
    echo "GITHUB API - request body ${body}\n"

    OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())
    writer.write(body);
    writer.flush();

    def rs = new groovy.json.JsonSlurperClassic().parse(new InputStreamReader(connection.getInputStream(), "UTF-8"))
    echo "GITHUB API - response: ${rs}"
    connection.disconnect()
  } catch(err) {
    echo "GITHUB API - error: ${err}"
    throw err
  }
}

def getCommitHash() {
  SCMRevisionAction revisionAction = currentBuild.rawBuild.getAction(SCMRevisionAction.class);
  SCMRevision revision = revisionAction.getRevision()

  if (revision instanceof AbstractGitSCMSource.SCMRevisionImpl) {
    return ((AbstractGitSCMSource.SCMRevisionImpl) revision).getHash();
  } else if (revision instanceof PullRequestSCMRevision) {
    return ((PullRequestSCMRevision) revision).getPullHash();
  } else {
    // TODO: throw some error
  }
}

return this
