#!/bin/bash

set -x -e

cd jlatexmath
mvn install -DskipTests
cd ..
cd proofbuilder
mvn compile assembly:single
cd packaging
javac module-info.java
jar uf ../target/proofbuilder-0.0.1-SNAPSHOT-jar-with-dependencies.jar module-info.class
cd ..
