package com.example.hks;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.example.hks.mapper")
@SpringBootApplication
public class HksApplication {

    public static void main(String[] args) {
        SpringApplication.run(HksApplication.class, args);
    }

}
