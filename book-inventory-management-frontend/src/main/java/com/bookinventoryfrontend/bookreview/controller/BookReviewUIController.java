package com.bookinventoryfrontend.bookreview.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.client.RestClient;

import com.bookinventoryfrontend.bookreview.dto.BookReviewDTO;

@Controller
@RequestMapping("/ui")
public class BookReviewUIController {

    @Autowired
    private RestClient restClient;

    // 🔹 Get Reviews by ISBN
    @GetMapping("/reviews/book/{isbn}")
    public String getByIsbn(@PathVariable String isbn, Model model) {

        List<BookReviewDTO> data = restClient.get()
                .uri("/api/v1/reviews/book/{isbn}", isbn)
                .retrieve()
                .body(new org.springframework.core.ParameterizedTypeReference<List<BookReviewDTO>>() {});

        model.addAttribute("data", data);
        return "/reviewui/reviews";
    }

    // 🔹 Count
    @GetMapping("/reviews/book/{isbn}/count")
    public String count(@PathVariable String isbn, Model model) {

        String data = restClient.get()
                .uri("/api/v1/reviews/book/{isbn}/count", isbn)
                .retrieve()
                .body(String.class);

        model.addAttribute("data", data);
        return "/reviewui/reviews";
    }

    // 🔹 Get All
    @GetMapping("/reviews")
    public String getAll(Model model) {

        List<BookReviewDTO> data = restClient.get()
                .uri("/api/v1/reviews/book")
                .retrieve()
                .body(new org.springframework.core.ParameterizedTypeReference<List<BookReviewDTO>>() {});

        model.addAttribute("data", data);
        return "/reviewui/reviews";
    }

    // 🔹 Form
    @GetMapping("/reviews/form")
    public String form(Model model) {
        model.addAttribute("review", new BookReviewDTO());
        return "/reviewui/review-form";
    }

    // 🔹 Create
    @PostMapping("/reviews/form")
    public String create(@ModelAttribute BookReviewDTO dto) {

        restClient.post()
                .uri("/api/v1/reviews/book")
                .body(dto)
                .retrieve()
                .body(BookReviewDTO.class);

        return "redirect:/ui/review-dashboard";
    }

    // 🔹 Update Form
    @GetMapping("/reviews/update-form")
    public String updateForm(Model model) {
        model.addAttribute("review", new BookReviewDTO());
        return "/reviewui/review-update-form";
    }

    // 🔹 Update
    @PostMapping("/reviews/update")
    public String update(@ModelAttribute BookReviewDTO dto) {

        restClient.put()
                .uri("/api/v1/reviews/book")
                .body(dto)
                .retrieve()
                .toBodilessEntity();

        return "redirect:/ui/review-dashboard";
    }

    // 🔹 Delete
    @GetMapping("/reviews/delete/{isbn}/{reviewerId}")
    public String delete(@PathVariable String isbn,
                         @PathVariable int reviewerId) {

        restClient.delete()
                .uri("/api/v1/reviews/book/{isbn}/{reviewerId}", isbn, reviewerId)
                .retrieve()
                .toBodilessEntity();

        return "redirect:/ui/review-dashboard";
    }
}