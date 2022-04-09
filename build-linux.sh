#!/bin/bash

set -x -e

jpackage -p target/proofbuilder-0.0.1-SNAPSHOT-jar-with-dependencies.jar -m proofbuilder/proofbuilder.ProofBuilderFrame --name ProofBuilder --type app-image
gcc -o run-ProofBuilder -no-pie run-ProofBuilder.c
cp run-ProofBuilder ProofBuilder
tar cJf ProofBuilder-linux.txz ProofBuilder
