package com.bookinventoryfrontend.reviewer.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.client.RestClient;

import com.bookinventoryfrontend.reviewer.dto.ReviewerDTO;

@Controller
@RequestMapping("/ui")
public class ReviewerUIController {

    @Autowired
    private RestClient restClient;

    // 🔹 Dashboard
    @GetMapping("/review-dashboard")
    public String dashboard() {
        return "/reviewui/review-dashboard";
    }

    // 🔹 Get Reviewer by ID
    @GetMapping("/reviewer/{id}")
    public String getReviewer(@PathVariable int id, Model model) {

        List<ReviewerDTO> data = restClient.get()
                .uri("/api/v1/reviewer/{id}", id)
                .retrieve()
                .body(new org.springframework.core.ParameterizedTypeReference<List<ReviewerDTO>>() {});

        model.addAttribute("data", data);
        return "/reviewui/reviewer";
    }

    // 🔹 Get by Name
    @GetMapping(value = "/reviewer", params = "name")
    public String getByName(@RequestParam String name, Model model) {

        List<ReviewerDTO> data = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/reviewer")
                        .queryParam("name", name)
                        .build())
                .retrieve()
                .body(new org.springframework.core.ParameterizedTypeReference<List<ReviewerDTO>>() {});

        model.addAttribute("data", data);
        return "/reviewui/reviewer";
    }

    // 🔹 Get by Company
    @GetMapping(value = "/reviewer", params = "employedBy")
    public String getByCompany(@RequestParam String employedBy, Model model) {

        List<ReviewerDTO> data = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/reviewer")
                        .queryParam("employedBy", employedBy)
                        .build())
                .retrieve()
                .body(new org.springframework.core.ParameterizedTypeReference<List<ReviewerDTO>>() {});

        model.addAttribute("data", data);
        return "/reviewui/reviewer";
    }

    // 🔹 Get All
    @GetMapping("/reviewers")
    public String getAll(Model model) {

        List<ReviewerDTO> data = restClient.get()
                .uri("/api/v1/reviewer")
                .retrieve()
                .body(new org.springframework.core.ParameterizedTypeReference<List<ReviewerDTO>>() {});

        model.addAttribute("data", data);
        return "/reviewui/reviewer";
    }

    // 🔹 Form
    @GetMapping("/reviewer/form")
    public String form(Model model) {
        model.addAttribute("reviewer", new ReviewerDTO());
        return "/reviewui/reviewer-form";
    }

    // 🔹 Create
    @PostMapping("/reviewer/form")
    public String create(@ModelAttribute ReviewerDTO dto) {

        restClient.post()
                .uri("/api/v1/reviewer")
                .body(dto)
                .retrieve()
                .body(ReviewerDTO.class);

        return "redirect:/ui/review-dashboard";
    }

    // 🔹 Update Form
    @GetMapping("/reviewer/update-form")
    public String updateForm(Model model) {
        model.addAttribute("reviewer", new ReviewerDTO());
        return "/reviewui/reviewer-update-form";
    }

    // 🔹 Update
    @PostMapping("/reviewer/update-form")
    public String update(@ModelAttribute ReviewerDTO dto) {

        restClient.put()
                .uri("/api/v1/reviewer")
                .body(dto)
                .retrieve()
                .toBodilessEntity();

        return "redirect:/ui/review-dashboard";
    }

    // 🔹 Delete
    @GetMapping("/reviewer/delete/{id}")
    public String delete(@PathVariable int id) {

        restClient.delete()
                .uri("/api/v1/reviewer/{id}", id)
                .retrieve()
                .toBodilessEntity();

        return "redirect:/ui/review-dashboard";
    }
}