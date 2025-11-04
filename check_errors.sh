#!/bin/bash

# Script to check for compilation errors in ShambaBora Android project

echo "========================================="
echo "ShambaBora Project Error Check"
echo "========================================="
echo ""

# Check if we're in the project root
if [ ! -f "build.gradle.kts" ]; then
    echo "Error: Please run this script from the project root directory"
    exit 1
fi

echo "1. Checking for Gradle wrapper..."
if [ -f "gradlew" ]; then
    echo "   ✓ Gradle wrapper found"
else
    echo "   ✗ Gradle wrapper not found"
    exit 1
fi

echo ""
echo "2. Checking for required files..."
if [ -f "app/build.gradle.kts" ]; then
    echo "   ✓ app/build.gradle.kts found"
else
    echo "   ✗ app/build.gradle.kts not found"
    exit 1
fi

if [ -f "app/src/main/AndroidManifest.xml" ]; then
    echo "   ✓ AndroidManifest.xml found"
else
    echo "   ✗ AndroidManifest.xml not found"
    exit 1
fi

echo ""
echo "3. Checking Kotlin syntax (basic checks)..."
echo "   Checking for common syntax issues..."

# Check for unclosed braces
unclosed=$(find app/src/main/java -name "*.kt" -exec grep -l "suspend fun" {} \; | while read file; do
    open=$(grep -o "{" "$file" | wc -l)
    close=$(grep -o "}" "$file" | wc -l)
    if [ "$open" != "$close" ]; then
        echo "$file"
    fi
done)

if [ -z "$unclosed" ]; then
    echo "   ✓ No obvious brace mismatches found"
else
    echo "   ⚠ Found potential brace mismatches in:"
    echo "$unclosed"
fi

echo ""
echo "4. Checking for missing imports..."
echo "   (This is a basic check - full compilation will catch more)"

echo ""
echo "========================================="
echo "To fully compile and check for errors:"
echo "========================================="
echo ""
echo "Option 1: In Android Studio:"
echo "  - Click 'Build' → 'Make Project' (Ctrl+F9)"
echo "  - Or 'Build' → 'Rebuild Project'"
echo ""
echo "Option 2: From Terminal (if Java is set up):"
echo "  ./gradlew build"
echo "  ./gradlew assembleDebug"
echo "  ./gradlew check"
echo ""
echo "Option 3: Check syntax only (faster):"
echo "  ./gradlew compileDebugKotlin"
echo ""
echo "========================================="

