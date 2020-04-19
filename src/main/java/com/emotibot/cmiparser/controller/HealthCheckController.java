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
        String a1 = "a";
        StringBuffer a2 = new StringBuffer("a");
        long start1 = System.currentTimeMillis();

        for (int i = 0; i < 10000; i++) {
            a1 = a1 + "b";
        }
        long end1 = System.currentTimeMillis();
        long start2 = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            a2.append("b"   );
        }

        long end2 = System.currentTimeMillis();


        System.out.println("string cort : "+(end1-start1));
        System.out.println("stringbuffer cort : "+(end2-start2));
    }

}
