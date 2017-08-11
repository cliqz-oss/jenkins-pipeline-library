#!/usr/bin/env groovy

def hasNewerQueuedJobs() {
  def queue = jenkins.model.Jenkins.getInstance().getQueue().getItems()
  for (int i=0; i < queue.length; i++) {
    if (queue[i].task.getName() == env.JOB_NAME ) {
      echo "Jobs in queue, aborting"
      return true
    }
  }
  return false
}

return this
