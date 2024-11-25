package de.dhbw.visualizer.object;

import com.google.common.base.Preconditions;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Diese Implementierung ist ein Workaround. Besser sollte es wie folgt gehen:
 * <p>
 * If the resources are packed in another data file (e.g. a .jar file) and you
 * don't want to extract them to disk, you must use aiImportFileEx. The last
 * argument is an AIFileIO structure that can be used to define a virtual
 * filesystem. The callbacks you set up there will be used by Assimp while
 * parsing a scene. I've successfully used it in the past, and you can nicely
 * implement extraction/decompression to memory buffers, avoiding expensive IO.
 *
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class ResourceManager {

    // Stores paths to files with the global jarFilePath as the key
    private static final Map<String, String> FILE_CACHE = new HashMap<>();

    /**
     * Extract the specified resource from inside the jar to the local file system.
     *
     * @param jarFilePath absolute path to the resource
     * @return full file system path if file successfully extracted, else null on error
     */
    public static String extract(String jarFilePath) {
        Preconditions.checkNotNull(jarFilePath);

        return FILE_CACHE.computeIfAbsent(jarFilePath, ResourceManager::extractComputation);
    }

    private static String extractComputation(String key) {
        // Read the file we're looking for
        try (InputStream fileStream = ResourceManager.class.getResourceAsStream(key)) {
            // Was the resource found?
            if (fileStream == null) {
                throw new IllegalArgumentException(String.format("jarFilePath \"%s\" not found!", key));
            }

            // Grab the file name
            String[] chopped = key.split("/");
            String fileName = chopped[chopped.length - 1];

            // Create our temp file (first param is just random bits)
            File tempFile = File.createTempFile("asdf", fileName);

            // Set this file to be deleted on VM exit
            tempFile.deleteOnExit();

            // Create an output stream to barf to the temp file
            try (OutputStream out = new FileOutputStream(tempFile)) {
                // Write the file to the temp file
                byte[] buffer = new byte[1024];
                int len = fileStream.read(buffer);
                while (len != -1) {
                    out.write(buffer, 0, len);
                    len = fileStream.read(buffer);
                }
            }

            // Return the path of this sweet new file
            return tempFile.getAbsolutePath();
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    private ResourceManager() {
    }
}