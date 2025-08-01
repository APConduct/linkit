#!/bin/bash

# LinkIt Calculator Dark Mode Launcher
# This script launches the calculator with optimized dark mode settings for macOS

echo "🌙 Launching LinkIt Calculator in Dark Mode..."

# Set macOS-specific JVM arguments for better dark mode integration
export JAVA_OPTS="-Dapple.awt.application.appearance=NSAppearanceNameDarkAqua \
-Dapple.laf.useScreenMenuBar=true \
-Dcom.apple.mrj.application.apple.menu.about.name='LinkIt Calculator' \
-Dapple.awt.application.name='LinkIt Calculator' \
-Dcom.apple.macos.use-file-dialog-packages=true \
-Dcom.apple.macos.useScreenMenuBar=true"

# Launch the calculator
./gradlew gui:run

echo "Calculator closed. Goodbye! 👋"
