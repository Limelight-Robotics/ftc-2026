#!/bin/bash

# FTC Robot Controller - Build and Deploy Script
# This script builds the TeamCode APK and installs it to the connected robot

set -e  # Exit on error

START_TIME=$(date +%s)

print_elapsed() {
    status=$?
    end_time=$(date +%s)
    elapsed=$((end_time - START_TIME))
    h=$((elapsed / 3600))
    m=$(((elapsed % 3600) / 60))
    s=$((elapsed % 60))
    printf "\nElapsed time: %02d:%02d:%02d\n" "$h" "$m" "$s"
    return $status
}
trap 'print_elapsed' EXIT

echo "======================================"
echo "FTC Robot Controller - Build & Deploy"
echo "======================================"
echo ""

# Set JAVA_HOME to Java 17 for compatibility with Android Gradle Plugin 8.7.3
# Only apply this configuration for Zander, as Omarchy Linux is finicky with this.
GIT_USERNAME=$(git config user.name)
GIT_EMAIL=$(git config user.email)
if [ "$GIT_USERNAME" = "Zander Lewis" ] && [ "$GIT_EMAIL" = "zander@zanderlewis.dev" ]; then
    export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
    echo "🔧 Using Java 17: $JAVA_HOME"
    echo ""
fi

# Build the APK
echo "📦 Building APK..."
./gradlew assembleDebug -x lint -x lintVitalAnalyzeRelease -x lintVitalReportRelease

if [ $? -ne 0 ]; then
    echo "❌ Build failed!"
    exit 1
fi

echo ""
echo "✅ Build successful!"
echo ""

# Check for connected devices
echo "🔍 Checking for connected devices..."
DEVICE=$(adb devices | grep -v "List of devices" | grep "device$" | head -n 1 | awk '{print $1}')

if [ -z "$DEVICE" ]; then
    echo "❌ No FTC robot connected via ADB!"
    echo "Please connect your robot and try again."
    exit 1
fi

echo "✅ Using device: $DEVICE"
echo ""

# Install the APK
echo "🚀 Installing APK to robot..."
APK_PATH="./TeamCode/build/outputs/apk/debug/TeamCode-debug.apk"

if [ ! -f "$APK_PATH" ]; then
    echo "❌ APK not found at $APK_PATH"
    exit 1
fi

adb -s "$DEVICE" install -r "$APK_PATH"

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Deployment successful!"
    echo "======================================"
    echo "Your robot is ready to use!"
    echo "======================================"
else
    echo "❌ Deployment failed!"
    exit 1
fi
