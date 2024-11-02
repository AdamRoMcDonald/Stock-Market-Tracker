package org.example;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class ImageLoader {
    public BufferedImage loadImage(String path) throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream(path);
        if (is == null) {
            throw new IOException("Resource not found: " + path);
        }
        return ImageIO.read(is);
    }
    public File loadFile(String path) throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream(path);
        if (is == null) {
            throw new IOException("Resource not found: " + path);
        }
        // Create a temporary file to hold the resource data
        File tempFile = File.createTempFile("tempfile", ".tmp");
        tempFile.deleteOnExit(); // Ensure the file is deleted when the JVM exits

        // Write the resource data to the temporary file
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }

        return tempFile;
    }
}
