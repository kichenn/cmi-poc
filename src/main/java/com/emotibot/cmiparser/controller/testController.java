package com.emotibot.cmiparser.controller;

import com.emotibot.cmiparser.common.BaseResult;
import com.emotibot.cmiparser.entity.dto.SlotResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: zujikang
 * @Date: 2020-04-08 18:43
 */
@RestController
public class testController {

    @GetMapping("/_health_check")
    public Object haha() {
        return "haha";
    }

}
