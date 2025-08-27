package com.amay.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

/**
 * Utility class for loading native libraries with proper error handling
 */
public class NativeLibraryLoader {
    
    private static final Logger logger = Logger.getLogger(NativeLibraryLoader.class.getName());
    
    /**
     * Loads a native library with proper path resolution
     * @param libraryName The name of the library (without extension)
     * @param searchPaths Additional search paths for the library
     * @return true if library was loaded successfully
     */
    public static boolean loadNativeLibrary(String libraryName, String... searchPaths) {
        try {
            // First try to load using System.loadLibrary (searches java.library.path)
            try {
                System.loadLibrary(libraryName);
                logger.info("Successfully loaded library: " + libraryName + " from java.library.path");
                return true;
            } catch (UnsatisfiedLinkError e) {
                logger.warning("Failed to load library from java.library.path: " + e.getMessage());
            }
            
            // Try to load from current directory
            try {
                System.load(libraryName);
                logger.info("Successfully loaded library: " + libraryName + " from current directory");
                return true;
            } catch (UnsatisfiedLinkError e) {
                logger.warning("Failed to load library from current directory: " + e.getMessage());
            }
            
            // Try to load with .dll extension from current directory
            try {
                System.load(libraryName + ".dll");
                logger.info("Successfully loaded library: " + libraryName + ".dll from current directory");
                return true;
            } catch (UnsatisfiedLinkError e) {
                logger.warning("Failed to load library with .dll extension: " + e.getMessage());
            }
            
            // Try to load from lib directory
            String libPath = "lib/Printer/" + libraryName + ".dll";
            try {
                System.load(libPath);
                logger.info("Successfully loaded library: " + libPath);
                return true;
            } catch (UnsatisfiedLinkError e) {
                logger.warning("Failed to load library from lib path: " + e.getMessage());
            }
            
            // Try to load from absolute paths
            for (String searchPath : searchPaths) {
                try {
                    String fullPath = searchPath + File.separator + libraryName + ".dll";
                    if (Files.exists(Paths.get(fullPath))) {
                        System.load(fullPath);
                        logger.info("Successfully loaded library: " + fullPath);
                        return true;
                    }
                } catch (UnsatisfiedLinkError e) {
                    logger.warning("Failed to load library from search path " + searchPath + ": " + e.getMessage());
                }
            }
            
            // Try to load from project root
            try {
                String projectRoot = System.getProperty("user.dir");
                String fullPath = projectRoot + File.separator + "lib" + File.separator + "Printer" + File.separator + libraryName + ".dll";
                if (Files.exists(Paths.get(fullPath))) {
                    System.load(fullPath);
                    logger.info("Successfully loaded library: " + fullPath);
                    return true;
                }
            } catch (UnsatisfiedLinkError e) {
                logger.warning("Failed to load library from project root: " + e.getMessage());
            }
            
            // Try to load from jar location
            try {
                String jarLocation = getJarLocation();
                if (jarLocation != null) {
                    String fullPath = jarLocation + File.separator + "lib" + File.separator + "Printer" + File.separator + libraryName + ".dll";
                    if (Files.exists(Paths.get(fullPath))) {
                        System.load(fullPath);
                        logger.info("Successfully loaded library: " + fullPath);
                        return true;
                    }
                }
            } catch (UnsatisfiedLinkError e) {
                logger.warning("Failed to load library from jar location: " + e.getMessage());
            }
            
            logger.severe("Failed to load native library: " + libraryName + " from all attempted locations");
            return false;
            
        } catch (Exception e) {
            logger.severe("Error loading native library " + libraryName + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Gets the directory where the JAR file is located
     * @return The JAR directory path or null if not found
     */
    private static String getJarLocation() {
        try {
            String className = NativeLibraryLoader.class.getName().replace('.', '/') + ".class";
            String classPath = NativeLibraryLoader.class.getClassLoader().getResource(className).getPath();
            
            if (classPath.startsWith("file:")) {
                classPath = classPath.substring(5);
            }
            
            if (classPath.contains("!")) {
                // Running from JAR
                String jarPath = classPath.substring(0, classPath.indexOf("!"));
                return new File(jarPath).getParent();
            } else {
                // Running from class files
                return new File(classPath).getParent();
            }
        } catch (Exception e) {
            logger.warning("Could not determine JAR location: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Sets the java.library.path system property to include the lib directory
     */
    public static void setupLibraryPath() {
        try {
            String currentLibraryPath = System.getProperty("java.library.path", "");
            String projectRoot = System.getProperty("user.dir");
            String libPath = projectRoot + File.separator + "lib" + File.separator + "Printer";
            
            if (!currentLibraryPath.contains(libPath)) {
                String newLibraryPath = currentLibraryPath.isEmpty() ? libPath : currentLibraryPath + File.pathSeparator + libPath;
                System.setProperty("java.library.path", newLibraryPath);
                logger.info("Updated java.library.path to include: " + libPath);
            }
        } catch (Exception e) {
            logger.warning("Failed to setup library path: " + e.getMessage());
        }
    }
    
    /**
     * Checks if the native library file exists
     * @param libraryName The name of the library
     * @return true if the library file exists
     */
    public static boolean libraryExists(String libraryName) {
        String[] searchPaths = {
            ".",
            "lib/Printer",
            System.getProperty("user.dir") + "/lib/Printer"
        };
        
        for (String path : searchPaths) {
            if (Files.exists(Paths.get(path, libraryName + ".dll"))) {
                return true;
            }
        }
        return false;
    }
}

