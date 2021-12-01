import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;

/**
 * Contains helper methods for loading native libraries, particularly JNI.
 *
 * @author gkubisa
 */
public class NativeLibraryLoader {
    /**
     * Utility classes should not have a public constructor.
     */
    private NativeLibraryLoader() {
    }

    /**
     * Loads a native shared library. It tries the standard System.loadLibrary method first and if it
     * fails, it looks for the library in the current class path. It will handle libraries packed
     * within jar files, too.
     *
     * @param libraryName - name of the library to load
     * @throws IOException if the library cannot be extracted from a jar file into a temporary file
     */
    public static void loadLibrary(String libraryName) throws IOException {
        try {
            System.loadLibrary(libraryName);
        } catch (UnsatisfiedLinkError e) {
            String fileName = System.mapLibraryName(libraryName);

            int dotPosition = fileName.lastIndexOf('.');
            File file =
                    File.createTempFile(fileName.substring(0, dotPosition), fileName.substring(dotPosition));
            file.deleteOnExit();

            byte[] buffer = new byte[4096];

            URL[] urls = null;

            ClassLoader classLoader = NativeLibraryLoader.class.getClassLoader();
            String path = NativeLibraryLoader.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            if (path.endsWith("classes/java/main/") || path.endsWith("classes/java/main")) {
                urls = new URL[2];
                urls[0] = Paths.get(path, "/../../../natives").toUri().toURL();
                urls[1] = Paths.get(path, "/../../../").toUri().toURL();
                classLoader = new URLClassLoader(urls, classLoader);
            }

            InputStream inputStream = classLoader.getResourceAsStream(fileName);
            OutputStream outputStream = new FileOutputStream(file);

            try {
                while (inputStream.available() > 0) {
                    int StreamLength = inputStream.read(buffer);
                    if (StreamLength >= 0) {
                        outputStream.write(buffer, 0, StreamLength);
                    }
                }
            } finally {
                outputStream.close();
                inputStream.close();
            }

            System.load(file.getAbsolutePath());
        }
    }
}