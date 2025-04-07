package com.enmasse.User_Service.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class HelloController {

    @GetMapping("/visitor")
    public String getVisitor(@RequestHeader String Authorization) {
        return "Hello visitor";
    }

    @GetMapping("/admin")
    public String getAdmin(@RequestHeader String Authorization) {
        return "Hello admin";
    }

    @GetMapping("/user")
    public String getUsers(@RequestHeader String Authorization) {
        return "Hello user";
    }

    @GetMapping("/random")
    public String getRandomUser() {
        return "Hello random user";
    }
}
