package com.userauth.usermanagement.infrastructure.adapter.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "forward:/index.html";
    }

    @GetMapping("/landing")
    public String landing() {
        return "forward:/index.html";
    }
}
