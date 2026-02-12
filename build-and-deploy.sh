#!/bin/bash

# FTC Robot Controller - Build and Deploy Script
# This script builds the TeamCode APK and installs it to the connected robot

set -e  # Exit on error

# Ensure Java 21 is used (Gradle 8.9 doesn't support Java 25+)
if command -v /usr/libexec/java_home &> /dev/null; then
    JAVA21_HOME=$(/usr/libexec/java_home -v 21 2>/dev/null || true)
    if [ -n "$JAVA21_HOME" ]; then
        export JAVA_HOME="$JAVA21_HOME"
    fi
fi

# Simple argument parsing
CLEAN=false
BUILD_ONLY=false

while [[ $# -gt 0 ]]; do
    case "$1" in
        --clean|-c)
            CLEAN=true
            shift
            ;;
        --build-only|--build|-b)
            BUILD_ONLY=true
            shift
            ;;
        --help|-h)
            echo "Usage: $0 [--clean] [--build-only]"
            echo "  --clean, -c       : run './gradlew clean' before building"
            echo "  --build-only, -b  : build the APK but skip deploying to device"
            exit 0
            ;;
        *)
            echo "Unknown option: $1"
            echo "Use --help for usage."
            exit 1
            ;;
    esac
done

if [ "$CLEAN" = true ]; then
    echo "🧹 Cleaning build..."
    ./gradlew clean
    echo ""
fi

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
if [ "$BUILD_ONLY" = true ]; then
    echo "FTC Robot Controller - Build"
else
    echo "FTC Robot Controller - Build & Deploy"
fi
echo "======================================"
echo ""

# Build the APK
echo "📦 Building APK..."
./gradlew assembleDebug -x lint -x lintVitalAnalyzeRelease -x lintVitalReportRelease --parallel --build-cache --configuration-cache

if [ $? -ne 0 ]; then
    echo "❌ Build failed!"
    exit 1
fi

echo ""
echo "✅ Build successful!"
echo ""

if [ "$BUILD_ONLY" = true ]; then
    echo "🔧 Build-only flag set; skipping deploy."
    exit 0
fi

# Configuration
WIFI_DEVICE_IP="192.168.43.1"
WIFI_DEVICE_PORT="5555"
MAX_RETRIES=3
RETRY_DELAY=2

# Function to recover offline devices (tries fast reconnect first, then full restart)
restart_adb_server() {
    # Try fast reconnect for offline devices first
    echo "🔄 Attempting quick reconnect of offline devices..."
    adb reconnect offline 2>/dev/null
    sleep 1

    # Check if we have a device now
    if adb devices | grep -q "device$"; then
        return 0
    fi

    # Fall back to full server restart
    echo "🔄 Quick reconnect failed, restarting ADB server..."
    adb kill-server 2>/dev/null
    sleep 2
    adb start-server
    sleep 4
}

# Function to attempt WiFi reconnection
reconnect_wifi_device() {
    if [ -n "$WIFI_DEVICE_IP" ]; then
        echo "🔄 Attempting to reconnect to $WIFI_DEVICE_IP:$WIFI_DEVICE_PORT..."
        adb connect "$WIFI_DEVICE_IP:$WIFI_DEVICE_PORT" 2>&1 | head -n 1
        sleep 3
    fi
}

# Function to find USB device (serial number, no colon)
find_usb_device() {
    adb devices | grep -v "List of devices" | grep "device$" | awk '{print $1}' | grep -v ":" | head -n 1 | tr -d '\n\r'
}

# Function to find any device (USB or WiFi)
find_any_device() {
    adb devices | grep -v "List of devices" | grep "device$" | head -n 1 | awk '{print $1}' | tr -d '\n\r'
}

# Function to find connected device with retries (prefers USB over WiFi)
find_device() {
    local attempt=1
    local device=""

    while [ $attempt -le $MAX_RETRIES ]; do
        # First, prefer USB connection
        device=$(find_usb_device)
        if [ -n "$device" ]; then
            echo "$device"
            echo "📱 Using USB connection" >&2
            return 0
        fi

        # Check for existing WiFi connection
        device=$(find_any_device)
        if [ -n "$device" ]; then
            echo "$device"
            echo "📶 Using WiFi connection" >&2
            return 0
        fi

        echo "⚠️  No device found (attempt $attempt/$MAX_RETRIES)" >&2

        if [ $attempt -lt $MAX_RETRIES ]; then
            restart_adb_server >&2
            # Only try WiFi reconnect if no USB device found
            reconnect_wifi_device >&2
        fi

        attempt=$((attempt + 1))
    done

    return 1
}

# Function to install APK with retries
install_apk() {
    local device=$1
    local apk_path=$2
    local attempt=1

    while [ $attempt -le $MAX_RETRIES ]; do
        echo "🚀 Installing APK (attempt $attempt/$MAX_RETRIES)..."

        if adb -s "$device" install -r "$apk_path" 2>&1; then
            return 0
        fi

        echo "⚠️  Install failed on attempt $attempt"

        if [ $attempt -lt $MAX_RETRIES ]; then
            echo "🔄 Waiting ${RETRY_DELAY}s before retry..."
            sleep $RETRY_DELAY

            # Check if device is still connected
            if ! adb devices | grep -q "$device"; then
                echo "⚠️  Device disconnected, attempting recovery..."
                restart_adb_server
                reconnect_wifi_device
                sleep 2

                # Update device reference in case it changed
                device=$(adb devices | grep -v "List of devices" | grep "device$" | head -n 1 | awk '{print $1}' | tr -d '\n\r')
                # Validate device string format (should be IP:port or serial)
                if [ -z "$device" ] || [[ "$device" == *" "* ]] || [[ "$device" == *$'\n'* ]]; then
                    echo "❌ Could not reconnect to device"
                    return 1
                fi
            fi
        fi

        attempt=$((attempt + 1))
    done

    return 1
}

# Check for connected devices
echo "🔍 Checking for connected devices..."
DEVICE=$(find_device)

if [ -z "$DEVICE" ]; then
    echo "❌ No FTC robot connected via ADB!"
    echo ""
    echo "Troubleshooting tips:"
    echo "  1. For WiFi: Set FTC_ROBOT_IP=<robot-ip> and re-run"
    echo "  2. Run: adb connect <robot-ip>:5555"
    echo "  3. Check robot is on same network"
    echo "  4. Restart robot's ADB: adb tcpip 5555 (when USB connected)"
    exit 1
fi

echo "✅ Using device: $DEVICE"
echo ""

# Install the APK
APK_PATH="./TeamCode/build/outputs/apk/debug/TeamCode-debug.apk"

if [ ! -f "$APK_PATH" ]; then
    echo "❌ APK not found at $APK_PATH"
    exit 1
fi

if install_apk "$DEVICE" "$APK_PATH"; then
    echo ""
    echo "✅ Deployment successful!"
    echo "======================================"
    echo "Your robot is ready to use!"
    echo "======================================"
else
    echo ""
    echo "❌ Deployment failed after $MAX_RETRIES attempts!"
    echo ""
    echo "Manual recovery steps:"
    echo "  1. adb kill-server && adb start-server"
    echo "  2. adb connect <robot-ip>:5555"
    echo "  3. If still failing, USB connect and run: adb tcpip 5555"
    exit 1
fi
