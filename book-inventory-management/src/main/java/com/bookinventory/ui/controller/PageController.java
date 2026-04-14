package com.bookinventory.ui.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
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
    
    @GetMapping("/api-result")
    public String viewApi(@RequestParam String endpoint, Model model) {

        RestTemplate restTemplate = new RestTemplate();

        String url = "http://localhost:8080" + endpoint;

        Map response = restTemplate.getForObject(url, Map.class);

        model.addAttribute("response", response);

        return "api-result";
    }
}