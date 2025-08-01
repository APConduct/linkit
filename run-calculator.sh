#!/bin/bash

# Advanced Calculator Runner Script
# This script compiles and runs the calculator with proper interactive input handling

echo "🔨 Building calculator..."

# Compile the Kotlin source files
kotlinc -cp "$(./gradlew -q printClasspath 2>/dev/null || echo '')" \
    app/src/main/kotlin/org/linkit/*.kt \
    -include-runtime \
    -d linkit.jar

if [ $? -eq 0 ]; then
    echo "✅ Build successful!"
    echo "🧮 Starting Advanced Calculator..."
    echo ""

    # Run the calculator with proper input handling
    java -jar linkit.jar

    # Clean up
    rm -f calculator.jar
else
    echo "❌ Build failed!"
    echo "Trying alternative approach..."

    # Fallback: use gradle but with better input handling
    echo "Starting linkit via Gradle..."
    ./gradlew run --console=plain --quiet
fi
