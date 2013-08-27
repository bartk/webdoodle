#!/bin/bash

MAVEN_OPTS="-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=9009,server=y,suspend=n -Xmx128m -XX:MaxPermSize=100m" mvn embedded-glassfish:run
