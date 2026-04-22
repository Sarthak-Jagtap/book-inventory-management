package com.bookinventoryfrontend.controller;

import com.bookinventoryfrontend.dto.TeamMember;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Arrays;

@Controller
public class HomeController {

    @GetMapping("/")
    public String root() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String getHomePage(Model model) {

        model.addAttribute("activePage", "home");

        // ✅ MERGED VERSION (photos + extra info)
        List<TeamMember> members = Arrays.asList(
                new TeamMember("Krishna Varma", "User Module", "/images/team/krishna.jpg"),
                new TeamMember("Sarthak Jagtap", "Book Module", "/images/team/sarthak.jpeg"),
                new TeamMember("Yashomati", "Inventory Module", "/images/team/yashomati.jpeg"),
                new TeamMember("Prajwal", "Review Module", "/images/team/prajwal.jpeg"),
                new TeamMember("Sudhanshu", "Cart Module", "/images/team/sudhanshu.jpeg")
        );

        model.addAttribute("members", members);
        model.addAttribute("teamMembers", members);

        return "home";
    }

    @GetMapping("/access-denied")
    public String accessDenied(Model model) {
        model.addAttribute("activePage", "access-denied");
        return "access-denied";
    }

    @GetMapping("/team/user-module")
    public String userModule(Model model) {
        model.addAttribute("activePage", "user-module");
        model.addAttribute("memberName", "Krishna Varma");
        model.addAttribute("memberPhoto", "/images/team/krishna.jpg");
        model.addAttribute("endpointCount", 24);
        model.addAttribute("groupCount", 5);
        return "team/user-module";
    }

    @GetMapping("/team/book-module")
    public String bookModule(Model model) {
        model.addAttribute("activePage", "book-module");
        model.addAttribute("memberName", "Sarthak Jagtap");
        model.addAttribute("endpointCount", 20);
        model.addAttribute("groupCount", 4);
        return "team/book-module";
    }

    @GetMapping("/team/inventory-module")
    public String inventoryModule(Model model) {
        model.addAttribute("activePage", "inventory-module");
        model.addAttribute("memberName", "Yashomati");
        model.addAttribute("endpointCount", 16);
        model.addAttribute("groupCount", 3);
        return "team/inventory-module";
    }

    @GetMapping("/team/review-module")
    public String reviewModule(Model model) {
        model.addAttribute("activePage", "review-module");
        model.addAttribute("memberName", "Prajwal");
        model.addAttribute("endpointCount", 15);
        model.addAttribute("groupCount", 3);
        return "team/review-module";
    }

    @GetMapping("/team/author-module")
    public String authorModule() {
        // Redirect to the Author PageController which builds the full model
        return "redirect:/api-author-dashboard";
    }
}