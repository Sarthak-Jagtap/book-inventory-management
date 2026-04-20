package com.bookinventoryfrontend.dashboard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CartDashboardController {

    @GetMapping("/inventory-cart-dashboard")
    public String showDashboard() {
        return "dashboard/inventory-cart-dashboard";
    }
}