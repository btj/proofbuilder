#!/bin/bash

set -x -e

cd proofbuilder
jpackage -p target/proofbuilder-0.0.1-SNAPSHOT-jar-with-dependencies.jar -m proofbuilder/proofbuilder.ProofBuilderFrame --name ProofBuilder --type app-image
7z a ProofBuilder-windows.zip ProofBuilder
