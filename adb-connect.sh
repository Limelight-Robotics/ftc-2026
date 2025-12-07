#!/bin/bash

# Reset ADB and connect to FTC Robot Controller
# Device IP: 192.168.43.1:5555

DEVICE="192.168.43.1:5555"

echo "Disconnecting any existing connections..."
adb disconnect $DEVICE 2>/dev/null || true

echo "Killing ADB server..."
timeout 5 adb kill-server || killall adb 2>/dev/null || true

sleep 1

echo "Starting ADB server..."
adb start-server

sleep 1

echo "Connecting to robot at $DEVICE..."
adb connect $DEVICE
