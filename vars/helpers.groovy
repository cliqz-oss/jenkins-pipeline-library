#!/usr/bin/env groovy

@NonCPS
def entries(m) {m.collect {k, v -> [k, v]}}

def getFreePort(Map m = [:]) {
    def lower = ''
    def upper = ''

    if (m.lower) {
      lower = m.lower
    }

    if (m.upper) {
      upper = m.upper
    }

    sh(
        returnStdout: true,
        script: """#!/bin/bash
            read LOWERPORT UPPERPORT < /proc/sys/net/ipv4/ip_local_port_range

            if [[ ! -z "${lower}" ]]; then
              LOWERPORT=${lower}
            fi

            if [[ ! -z "${upper}" ]]; then
              UPPERPORT=${upper}
            fi

            while :
            do
                    PORT="`shuf -i \$LOWERPORT-\$UPPERPORT -n 1`"
                    ss -lpn | grep -q ":\$PORT " || break
            done
            echo \$PORT
        """
     ).trim()
}

def getIp() {
    sh(
        returnStdout: true,
        script: '''#!/bin/bash
            /sbin/ifconfig eth0 | grep "inet addr:" | cut -d: -f2 | awk \'{ print $1}\'
        '''
     ).trim()
}

return this
