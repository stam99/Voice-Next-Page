#!/bin/bash

./gradlew assembleRelease || exit
cp ./app/build/outputs/apk/app-release-unsigned.apk ~/apk/
cp ./service/build/outputs/apk/service-release-unsigned.apk ~/apk/

rm ~/apk/app-release-unsigned-aligned.apk
rm ~/apk/service-release-unsigned-aligned.apk
/home/serena/Downloads/build-tools/25.0.2/zipalign 4 ~/apk/app-release-unsigned.apk ~/apk/app-release-unsigned-aligned.apk
/home/serena/Downloads/build-tools/25.0.2/zipalign 4 ~/apk/service-release-unsigned.apk ~/apk/service-release-unsigned-aligned.apk

/home/serena/Downloads/build-tools/25.0.2/apksigner sign --ks ~/Downloads/KEYS/release.keystore ~/apk/app-release-unsigned-aligned.apk
/home/serena/Downloads/build-tools/25.0.2/apksigner sign --ks ~/Downloads/KEYS/release.keystore ~/apk/service-release-unsigned-aligned.apk

adb uninstall io.voxhub.accessibility.servicecode
sleep 1
#adb install ./service/build/outputs/apk/service-debug.apk
#adb install ./service/build/outputs/apk/service-release-unsigned.apk
adb install ~/apk/service-release-unsigned-aligned.apk
sleep 1
adb uninstall io.voxhub.accessibility.app
sleep 1
#adb install ./app/build/outputs/apk/app-debug.apk
#adb install ./app/build/outputs/apk/app-release-unsigned.apk
adb install ~/apk/app-release-unsigned-aligned.apk
sleep 1
adb shell am start -n io.voxhub.accessibility.app/io.voxhub.accessibility.app.SimpleActivity

#adb shell am start -n io.voxhub.accessibility.servicecode/io.voxhub.accessibility.servicecode.MyAccessibilityService
