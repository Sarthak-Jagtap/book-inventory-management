package com.bookinventory.ui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/api-dashboard")
    public String apiDashboard() {
        return "api-dashboard";
    }
}