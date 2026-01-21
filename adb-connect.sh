#!/bin/bash

# Reset ADB and connect to FTC Robot Controller

ROBOT_IP="192.168.43.1"
ROBOT_PORT="5555"
DEVICE="$ROBOT_IP:$ROBOT_PORT"
MAX_RETRIES=3

echo "======================================"
echo "FTC Robot ADB Connect"
echo "======================================"
echo ""

echo "🔌 Disconnecting any existing connections..."
adb disconnect "$DEVICE" 2>/dev/null || true

echo "🔄 Restarting ADB server..."
adb kill-server 2>/dev/null || true
sleep 1
adb start-server
sleep 1

# Try connecting with retries
attempt=1
connected=false

while [ $attempt -le $MAX_RETRIES ]; do
    echo "📶 Connecting to robot at $DEVICE (attempt $attempt/$MAX_RETRIES)..."

    if adb connect "$DEVICE" 2>&1 | grep -q "connected"; then
        connected=true
        break
    fi

    if [ $attempt -lt $MAX_RETRIES ]; then
        echo "⚠️  Connection failed, retrying..."
        sleep 2
    fi

    attempt=$((attempt + 1))
done

echo ""

if $connected; then
    echo "✅ Connected successfully!"
    echo ""
    echo "Connected devices:"
    adb devices
else
    echo "❌ Failed to connect after $MAX_RETRIES attempts"
    echo ""
    echo "Troubleshooting:"
    echo "  1. Verify robot is powered on"
    echo "  2. Check you're on the same network (robot WiFi: 192.168.43.x)"
    echo "  3. Try: ping $ROBOT_IP"
    echo "  4. USB connect and run: adb tcpip 5555"
    exit 1
fi
