#!/bin/bash

set -x -e

jpackage -p target/proofbuilder-0.0.1-SNAPSHOT-jar-with-dependencies.jar -m proofbuilder/proofbuilder.ProofBuilderFrame --name ProofBuilder --type app-image --mac-sign
zip -ry ProofBuilder-macos-unstapled.zip ProofBuilder.app
xcrun notarytool submit ProofBuilder-macos-unstapled.zip --keychain-profile AC_PASSWORD --wait
xcrun stapler staple ProofBuilder.app
zip -ry ProofBuilder-macos.zip ProofBuilder.app
