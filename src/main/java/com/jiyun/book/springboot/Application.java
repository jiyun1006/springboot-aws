package com.jiyun.book.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication  // 스프링부트의 자동 설정, 스프링Bean 읽기와 생성 모두 자동으로 설정.
public class Application {
    public static void main(String[] args){
        // 내장 was로 별도의 외부 was를 두지않고, 애플리케이션을 실행할 때 내부에서 was를 실
        SpringApplication.run(Application.class, args);
    }
}
