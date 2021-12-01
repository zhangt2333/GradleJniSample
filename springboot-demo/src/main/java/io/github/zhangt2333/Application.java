package io.github.zhangt2333;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) throws Exception {
        Class.forName("HelloWorld");
        SpringApplication.run(Application.class, args);
    }
}
