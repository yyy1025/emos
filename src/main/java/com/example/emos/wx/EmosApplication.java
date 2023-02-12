package com.example.emos.wx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
//添加上面这个注解，filter才会生效
@SpringBootApplication
public class EmosApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmosApplication.class, args);
    }

}
