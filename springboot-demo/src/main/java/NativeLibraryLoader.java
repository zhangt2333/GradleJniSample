import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.stream.Stream;

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

            URL[] urls = new URL[2];
            ClassLoader classLoader = NativeLibraryLoader.class.getClassLoader();
            if (classLoader instanceof URLClassLoader) {
                for (URL url : ((URLClassLoader) classLoader).getURLs()) {
                    if (url.getPath().endsWith("classes/java/main/") || url.getPath().endsWith("classes/java/main")) {
                        urls[0] = Paths.get(url.getPath(), "/../../../natives").toUri().toURL();
                        urls[1] = Paths.get(url.getPath(), "/../../../").toUri().toURL();
                    }
                }
            }

            URLClassLoader urlClassLoader = new URLClassLoader(urls, classLoader);
            InputStream inputStream = urlClassLoader.getResourceAsStream(fileName);
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