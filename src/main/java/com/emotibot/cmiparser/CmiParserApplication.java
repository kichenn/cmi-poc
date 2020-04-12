package com.emotibot.cmiparser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class CmiParserApplication {

    public static void main(String[] args) {
        SpringApplication.run(CmiParserApplication.class, args);
    }

}
