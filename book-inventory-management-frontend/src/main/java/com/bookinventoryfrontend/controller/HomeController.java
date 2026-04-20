package com.bookinventoryfrontend.controller;

import com.bookinventoryfrontend.dto.TeamMember;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @GetMapping("/")
    public String root() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("activePage", "home");

        List<TeamMember> members = List.of(
                new TeamMember(
                        "Krishna",
                        "User Module",
                        "user-module",
                        "Auth · Users · Roles · Purchases"
                ),
                new TeamMember(
                        "Member 2",
                        "Book Module",
                        "book-module",
                        "Books · Categories · Publishers · States"
                ),
                new TeamMember(
                        "Yashomati",
                        "Inventory & Cart Module",
                        "inventory-module",
                        "Inventory · Cart · Book Conditions"
                ),
                new TeamMember(
                        "Member 4",
                        "Review Module",
                        "review-module",
                        "Reviews · Reviewer UI"
                ),
                new TeamMember(
                        "Member 5",
                        "Author Module",
                        "author-module",
                        "Authors · Book Authors"
                )
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
        model.addAttribute("memberName", "Krishna");
        model.addAttribute("endpointCount", 24);
        model.addAttribute("groupCount", 5);
        return "team/user-module";
    }

    @GetMapping("/team/book-module")
    public String bookModule(Model model) {
        model.addAttribute("activePage", "book-module");
        model.addAttribute("memberName", "Member 2");
        model.addAttribute("endpointCount", 20);
        model.addAttribute("groupCount", 4);
        return "team/book-module";
    }

    @GetMapping("/team/inventory-module")
    public String inventoryModule(Model model) {
        model.addAttribute("activePage", "inventory-module");
        model.addAttribute("memberName", "Yashomati");
        model.addAttribute("endpointCount", 17);
        model.addAttribute("groupCount", 3);
        return "team/inventory-module";
    }

    @GetMapping("/team/review-module")
    public String reviewModule(Model model) {
        model.addAttribute("activePage", "review-module");
        model.addAttribute("memberName", "Member 4");
        model.addAttribute("endpointCount", 15);
        model.addAttribute("groupCount", 3);
        return "team/review-module";
    }

    @GetMapping("/team/author-module")
    public String authorModule(Model model) {
        model.addAttribute("activePage", "author-module");
        model.addAttribute("memberName", "Member 5");
        model.addAttribute("endpointCount", 15);
        model.addAttribute("groupCount", 3);
        return "author/api-author-dashboard";
    }
}