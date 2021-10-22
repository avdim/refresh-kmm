#!/bin/bash
./gradlew server:build && gradlew androidApp:assembleDebug && gradlew desktop:run
