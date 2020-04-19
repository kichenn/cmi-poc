package com.emotibot.cmiparser;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
class CmiParserApplicationTests {
    @Autowired
    private RestTemplate restTemplate;

    @Test
    void test01() {
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


        System.out.println("string cort : "+(start1-end1));
        System.out.println("stringbuffer cort : "+(start1-end1));



    }


}
