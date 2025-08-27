# Native Library Setup Guide

## Problem Description
The error `CuCustomWndAPIJWrapException Error:ERR_LIBRARY_NOT_LOADED -> Load library error` occurs when Java cannot find or load the native DLL library `CuCustomWndAPI.dll`.

## Root Cause
This issue typically happens because:
1. The native DLL is not in the Java library path
2. The DLL architecture doesn't match the JVM architecture (32-bit vs 64-bit)
3. Missing dependencies
4. Incorrect file permissions

## Solution

### Method 1: Use the Provided Startup Scripts (Recommended)

#### Windows Batch File
```cmd
start-tvm.bat
```

#### PowerShell Script
```powershell
.\start-tvm.ps1
```

These scripts automatically:
- Set the correct `java.library.path`
- Verify the DLL exists
- Start the application with proper library configuration

### Method 2: Manual Setup

#### Step 1: Verify DLL Location
Ensure `CuCustomWndAPI.dll` is in the `lib/Printer/` directory relative to your project root.

#### Step 2: Set Library Path
Set the `java.library.path` system property before running the application:

```cmd
java -Djava.library.path="lib\Printer" -jar target\TVM-1.1.0.jar
```

#### Step 3: Environment Variable (Alternative)
Set the `JAVA_LIBRARY_PATH` environment variable:

```cmd
set JAVA_LIBRARY_PATH=%cd%\lib\Printer
java -jar target\TVM-1.1.0.jar
```

### Method 3: Code-Level Solution

The application now includes a `NativeLibraryLoader` utility that automatically:
- Searches multiple locations for the DLL
- Sets up the library path
- Provides detailed error messages

## Troubleshooting

### 1. DLL Not Found
**Error**: `java.lang.UnsatisfiedLinkError: no CuCustomWndAPI in java.library.path`

**Solution**: 
- Verify the DLL exists in `lib/Printer/CuCustomWndAPI.dll`
- Check file permissions
- Ensure the path is correctly set

### 2. Architecture Mismatch
**Error**: `java.lang.UnsatisfiedLinkError: Can't load library`

**Solution**:
- Ensure you're using the correct DLL architecture (32-bit vs 64-bit)
- Match your JVM architecture with the DLL architecture
- Download the correct version from the vendor

### 3. Missing Dependencies
**Error**: Various runtime errors after DLL loads

**Solution**:
- Check if the DLL requires other DLLs (use Dependency Walker)
- Ensure Visual C++ Redistributable is installed
- Verify all required system libraries are present

### 4. File Permissions
**Error**: Access denied or permission errors

**Solution**:
- Run as Administrator if needed
- Check file permissions on the DLL
- Ensure the DLL is not locked by another process

## File Structure
```
AgraCashTVM/
├── lib/
│   └── Printer/
│       ├── CuCustomWndAPI.dll          # Native library
│       └── CuCustomWndAPIJWrap.jar     # Java wrapper
├── start-tvm.bat                       # Windows batch launcher
├── start-tvm.ps1                       # PowerShell launcher
└── src/main/java/com/amay/utils/
    └── NativeLibraryLoader.java        # Library loading utility
```

## Verification Steps

1. **Check DLL Existence**:
   ```cmd
   dir lib\Printer\CuCustomWndAPI.dll
   ```

2. **Verify Architecture**:
   - Use `file` command (Linux/Mac) or check file properties (Windows)
   - Ensure it matches your JVM architecture

3. **Test Library Loading**:
   ```cmd
   java -Djava.library.path="lib\Printer" -cp "lib\Printer\CuCustomWndAPIJWrap.jar" com.custom.wndapijwrap.CuCustomWndAPIJWrap
   ```

4. **Check System Requirements**:
   - Windows 10/11 (64-bit recommended)
   - Java 17+ (as specified in pom.xml)
   - Visual C++ Redistributable (if required)

## Common Issues and Solutions

### Issue: DLL loads but functions fail
- Check if the DLL requires initialization
- Verify the DLL version compatibility
- Check for missing system dependencies

### Issue: Works in development but fails in production
- Ensure the DLL is included in the deployment package
- Check if the production environment has the required system libraries
- Verify the working directory and relative paths

### Issue: Intermittent failures
- Check for DLL conflicts with other applications
- Verify system resources and memory
- Check for antivirus software interference

## Support

If the issue persists:
1. Check the application logs for detailed error messages
2. Verify the DLL vendor's documentation
3. Ensure all system requirements are met
4. Test with a minimal example to isolate the issue

## Additional Resources

- [Java Native Interface (JNI) Documentation](https://docs.oracle.com/javase/8/docs/technotes/guides/jni/)
- [System.loadLibrary() Documentation](https://docs.oracle.com/javase/8/docs/api/java/lang/System.html#loadLibrary-java.lang.String-)
- [Java Library Path Configuration](https://docs.oracle.com/javase/8/docs/technotes/guides/extensions/specification.html#bundled)

