package com.kang.core.blog.blog.demo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class demoController {
    @RequestMapping("/hello")
    public String hello(){
        return "hello word";
    }
}
