package com.example.couplebackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.example.couplebackend.mapper")
public class CoupleBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoupleBackendApplication.class, args);
    }

}
