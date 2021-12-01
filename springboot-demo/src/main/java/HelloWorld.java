import java.io.IOException;

public class HelloWorld {
    public native void print();

    static {
        try {
            NativeLibraryLoader.loadLibrary("native-hello");
            new HelloWorld().print();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}