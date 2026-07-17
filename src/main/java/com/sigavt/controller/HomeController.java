package com.sigavt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/login.html";
    }

    @GetMapping("/index.html")
    public String index() {
        return "redirect:/login.html";
    }

    @GetMapping("/login")
    public String login() {
        return "redirect:/login.html";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "redirect:/sigavt.html";
    }
}
