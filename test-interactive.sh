#!/bin/bash

# Interactive Calculator Test Script
# This script demonstrates the calculator working with various inputs

echo "🧮 Testing Advanced Calculator Interactively"
echo "============================================="
echo ""

# Compile the calculator
echo "🔨 Building calculator..."
kotlinc app/src/main/kotlin/org/linkit/*.kt -include-runtime -d calculator.jar 2>/dev/null

if [ $? -ne 0 ]; then
    echo "❌ Build failed!"
    exit 1
fi

echo "✅ Build successful!"
echo ""

# Test expressions to demonstrate functionality
test_expressions=(
    "2 + 3"
    "10 - 4"
    "3 * 4"
    "15 / 3"
    "2^8"
    "17 % 5"
    "2 + 3 * 4"
    "(2 + 3) * 4"
    "sqrt(16)"
    "abs(-42)"
    "sin(PI/2)"
    "cos(0)"
    "ln(E)"
    "log10(100)"
    "sqrt(16) + sin(PI/2)"
    "2 * PI * 5"
    "-5 + 10"
    "help"
    "quit"
)

echo "🚀 Running calculator with test expressions:"
echo "============================================"

# Create input file with test expressions
input_file=$(mktemp)
for expr in "${test_expressions[@]}"; do
    echo "$expr" >> "$input_file"
done

# Run the calculator with the input file
echo "📝 Input expressions:"
for expr in "${test_expressions[@]}"; do
    if [ "$expr" != "quit" ]; then
        echo "  calc> $expr"
    fi
done

echo ""
echo "📤 Calculator output:"
echo "===================="

# Run the calculator
java -jar calculator.jar < "$input_file"

# Cleanup
rm -f "$input_file" calculator.jar

echo ""
echo "✨ To run the calculator interactively yourself:"
echo "   1. Compile: kotlinc app/src/main/kotlin/org/linkit/*.kt -include-runtime -d calculator.jar"
echo "   2. Run: java -jar calculator.jar"
echo "   3. Enter expressions like: sqrt(16) + sin(PI/2)"
echo "   4. Type 'quit' to exit"
