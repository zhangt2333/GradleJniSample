import java.io.IOException;

public class HelloWorld {
    public native void print();

    static {
        try {
            NativeLibraryLoader.loadLibrary("native-hello");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new HelloWorld().print();
    }
}