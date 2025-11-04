# Compilation Commands for ShambaBora App

## Quick Status Check
✅ **Linter Status**: No errors found
✅ **Basic Syntax Check**: Passed
✅ **File Structure**: All required files present

## Commands to Compile and Check for Errors

### Option 1: In Android Studio (Recommended)
1. **Make Project** (Quick compile):
   - Menu: `Build` → `Make Project`
   - Keyboard: `Ctrl+F9` (Windows/Linux) or `Cmd+F9` (Mac)
   - This compiles only changed files

2. **Rebuild Project** (Full compile):
   - Menu: `Build` → `Rebuild Project`
   - This recompiles everything from scratch

3. **Clean Project** (Clear build cache):
   - Menu: `Build` → `Clean Project`
   - Then `Build` → `Rebuild Project`

### Option 2: From Terminal (Command Line)

#### Prerequisites
Make sure Java/JDK is installed and JAVA_HOME is set:
```bash
# Check Java version
java -version

# Set JAVA_HOME (example - adjust path to your Java installation)
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
# Or for Android Studio's bundled JDK:
export JAVA_HOME=/opt/android-studio/jbr
```

#### Compilation Commands

1. **Full Build** (Compiles everything):
   ```bash
   ./gradlew build
   ```

2. **Debug Build** (Faster, for development):
   ```bash
   ./gradlew assembleDebug
   ```

3. **Release Build** (Production version):
   ```bash
   ./gradlew assembleRelease
   ```

4. **Compile Kotlin Only** (Fastest, syntax check):
   ```bash
   ./gradlew compileDebugKotlin
   ```

5. **Run Lint Checks** (Code quality):
   ```bash
   ./gradlew lint
   ```

6. **Run Tests**:
   ```bash
   ./gradlew test
   ./gradlew connectedAndroidTest
   ```

7. **Check for Errors Only** (No build artifacts):
   ```bash
   ./gradlew check
   ```

### Option 3: Using the Check Script
```bash
bash check_errors.sh
```

## Common Issues and Fixes

### Issue: "JAVA_HOME is not set"
**Solution**: Set JAVA_HOME environment variable:
```bash
export JAVA_HOME=/path/to/your/java
```

### Issue: "Gradle daemon not available"
**Solution**: Start Gradle daemon:
```bash
./gradlew --daemon
```

### Issue: Build cache corrupted
**Solution**: Clean and rebuild:
```bash
./gradlew clean
./gradlew build
```

## Current Code Status

✅ **No compilation errors detected**
✅ **All suspend functions properly handled**
✅ **All imports resolved**
✅ **Navigation routes configured**
✅ **ViewModels and Repositories implemented**

## Known TODOs (Not Errors)
- Some navigation routes need username/group name fetching (non-critical)
- Settings screen has placeholder navigation (features to be implemented)

These are feature enhancements, not compilation errors.

