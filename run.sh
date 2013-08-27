#!/bin/bash

MAVEN_OPTS="-Xmx128m -XX:MaxPermSize=100m" mvn embedded-glassfish:run
