#!/bin/bash

./gradlew assembleDebug
adb uninstall jp.naist.ahclab.kaldigstreamerclient
sleep 1
adb install ./app/build/outputs/apk/app-debug.apk
sleep 1
adb shell am start -n jp.naist.ahclab.kaldigstreamerclient/jp.naist.ahclab.kaldigstreamerclient.SimpleActivity
