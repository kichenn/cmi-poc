package com.emotibot.cmiparser.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/_health_check")
    public Object haha() {
        return "haha";
    }


    public static void main(String[] args) {

    }

}
