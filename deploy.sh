#!/bin/bash

# Exit on any error
set -e

# Configuration
TOMCAT_HOME="/opt/homebrew/opt/tomcat@9/libexec"
APP_NAME="try-smart-lifecycle"
APP_URL="http://localhost:8080/$APP_NAME"
TIMEOUT=10

echo "Starting deployment process..."

# Function to check if Tomcat is running
is_tomcat_running() {
    if [ -f "$TOMCAT_HOME/bin/catalina.pid" ]; then
        return 0
    else
        return 1
    fi
}

# Function to wait for Tomcat to stop
wait_for_tomcat_stop() {
    local start_time=$(date +%s)
    while is_tomcat_running; do
        if [ $(($(date +%s) - start_time)) -gt $TIMEOUT ]; then
            echo "Error: Tomcat did not stop within $TIMEOUT seconds"
            exit 1
        fi
        sleep 1
    done
}

# Function to wait for Tomcat to start
wait_for_tomcat_start() {
    local start_time=$(date +%s)
    while ! curl -s "$APP_URL" > /dev/null; do
        if [ $(($(date +%s) - start_time)) -gt $TIMEOUT ]; then
            echo "Error: Tomcat did not start within $TIMEOUT seconds"
            exit 1
        fi
        sleep 1
    done
}

# Stop Tomcat if running
if is_tomcat_running; then
    echo "Stopping Tomcat..."
    "$TOMCAT_HOME/bin/shutdown.sh"
    wait_for_tomcat_stop
    echo "Tomcat stopped successfully"
fi

# Build the project
echo "Building project..."
if ! mvn clean package; then
    echo "Error: Build failed"
    exit 1
fi
echo "Build completed successfully"

# Deploy the WAR
echo "Deploying WAR file..."
cp "target/$APP_NAME-1.0-SNAPSHOT.war" "$TOMCAT_HOME/webapps/$APP_NAME.war"
echo "WAR file deployed"

# Start Tomcat
echo "Starting Tomcat..."
"$TOMCAT_HOME/bin/startup.sh"
wait_for_tomcat_start
echo "Tomcat started successfully"

echo "Deployment completed successfully!"
echo "Application is available at: $APP_URL" 