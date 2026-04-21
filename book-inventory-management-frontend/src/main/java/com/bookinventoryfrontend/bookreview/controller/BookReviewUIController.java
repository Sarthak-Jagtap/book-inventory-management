package com.bookinventoryfrontend.bookreview.controller;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.core.ParameterizedTypeReference;

import com.bookinventoryfrontend.bookreview.dto.BookReviewDTO;

@Controller
@RequestMapping("/ui")
public class BookReviewUIController {

    @Autowired
    private RestClient restClient;

    // ════════════════════════════════════════════════════
    // HELPER — extract clean "message" from JSON error body
    // ════════════════════════════════════════════════════

    /**
     * The backend wraps every error as:
     *   {"success":false,"statusCode":500,"message":"...","data":null}
     * This pulls just the message value so the toast stays readable.
     */
    private String extractMessage(RestClientException e) {
        String raw = e.getMessage();
        if (raw != null) {
            Matcher m = Pattern.compile("\"message\"\\s*:\\s*\"([^\"]+)\"").matcher(raw);
            if (m.find()) {
                return m.group(1);
            }
        }
        return "An unexpected server error occurred. Please try again.";
    }

    // ════════════════════════════════════════════════════
    // HELPER — check if a review already exists for the
    // given reviewerId + ISBN combination
    // Returns true if already exists, false otherwise
    // ════════════════════════════════════════════════════

    private boolean reviewExists(int reviewerId, String isbn) {
        try {
            List<BookReviewDTO> result = restClient.get()
                    .uri("/api/v1/reviews/{reviewerId}/book/{isbn}", reviewerId, isbn)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<BookReviewDTO>>() {});
            return result != null && !result.isEmpty();
        } catch (RestClientException e) {
            // If the backend returns 404 or any error, the review does not exist
            return false;
        }
    }

    // ════════════════════════════════════════════════════
    // REVIEWS — by ISBN
    // ════════════════════════════════════════════════════

    @GetMapping("/reviews/book/{isbn}")
    public String getByIsbn(@PathVariable String isbn, Model model) {
        try {
            List<BookReviewDTO> data = restClient.get()
                    .uri("/api/v1/reviews/book/{isbn}", isbn)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<BookReviewDTO>>() {});
            model.addAttribute("data", data);
        } catch (RestClientException e) {
            model.addAttribute("data", extractMessage(e));
        }
        model.addAttribute("pageTitle", "Reviews for ISBN: " + isbn);
        model.addAttribute("apiEndpoint", "GET /api/v1/reviews/book/" + isbn);
        return "/reviewui/reviews";
    }

    @GetMapping("/reviews/book/{isbn}/count")
    public String countByIsbn(@PathVariable String isbn, Model model) {
        try {
            List<BookReviewDTO> reviews = restClient.get()
                    .uri("/api/v1/reviews/book/{isbn}", isbn)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<BookReviewDTO>>() {});
            int count = (reviews != null) ? reviews.size() : 0;
            model.addAttribute("data", String.valueOf(count));
            model.addAttribute("isCount", true);
        } catch (RestClientException e) {
            model.addAttribute("data", extractMessage(e));
        }
        model.addAttribute("countLabel", "Reviews for ISBN " + isbn);
        model.addAttribute("pageTitle", "Review Count");
        model.addAttribute("apiEndpoint", "GET /api/v1/reviews/book/" + isbn + "/count");
        return "/reviewui/reviews";
    }

    // ════════════════════════════════════════════════════
    // REVIEWS — by Reviewer ID
    // ════════════════════════════════════════════════════

    @GetMapping("/reviews/reviewer/{id}")
    public String getByReviewer(@PathVariable int id, Model model) {
        try {
            List<BookReviewDTO> data = restClient.get()
                    .uri("/api/v1/reviews/reviewer/{reviewerId}", id)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<BookReviewDTO>>() {});
            model.addAttribute("data", data);
        } catch (RestClientException e) {
            model.addAttribute("data", extractMessage(e));
        }
        model.addAttribute("pageTitle", "Reviews by Reviewer ID: " + id);
        model.addAttribute("apiEndpoint", "GET /api/v1/reviews/reviewer/" + id);
        return "/reviewui/reviews";
    }

    @GetMapping("/reviews/reviewer/{id}/count")
    public String countByReviewer(@PathVariable int id, Model model) {
        try {
            List<BookReviewDTO> reviews = restClient.get()
                    .uri("/api/v1/reviews/reviewer/{reviewerId}", id)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<BookReviewDTO>>() {});
            int count = (reviews != null) ? reviews.size() : 0;
            model.addAttribute("data", String.valueOf(count));
            model.addAttribute("isCount", true);
        } catch (RestClientException e) {
            model.addAttribute("data", extractMessage(e));
        }
        model.addAttribute("countLabel", "Reviews by Reviewer ID " + id);
        model.addAttribute("pageTitle", "Review Count");
        model.addAttribute("apiEndpoint", "GET /api/v1/reviews/reviewer/" + id + "/count");
        return "/reviewui/reviews";
    }

    // ════════════════════════════════════════════════════
    // REVIEWS — specific (Reviewer ID + ISBN)
    // ════════════════════════════════════════════════════

    @GetMapping("/reviews/{reviewerId}/book/{isbn}")
    public String getSpecific(@PathVariable int reviewerId,
                               @PathVariable String isbn,
                               Model model) {
        try {
            List<BookReviewDTO> data = restClient.get()
                    .uri("/api/v1/reviews/{reviewerId}/book/{isbn}", reviewerId, isbn)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<BookReviewDTO>>() {});
            model.addAttribute("data", data);
        } catch (RestClientException e) {
            model.addAttribute("data", extractMessage(e));
        }
        model.addAttribute("pageTitle", "Review — Reviewer " + reviewerId + " / ISBN " + isbn);
        model.addAttribute("apiEndpoint", "GET /api/v1/reviews/" + reviewerId + "/book/" + isbn);
        return "/reviewui/reviews";
    }

    // ════════════════════════════════════════════════════
    // REVIEWS — all
    // ════════════════════════════════════════════════════

    @GetMapping("/reviews")
    public String getAll(Model model) {
        try {
            List<BookReviewDTO> data = restClient.get()
                    .uri("/api/v1/reviews/book")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<BookReviewDTO>>() {});
            model.addAttribute("data", data);
        } catch (RestClientException e) {
            model.addAttribute("data", extractMessage(e));
        }
        model.addAttribute("pageTitle", "All Book Reviews");
        model.addAttribute("apiEndpoint", "GET /api/v1/reviews/book");
        return "/reviewui/reviews";
    }

    // ════════════════════════════════════════════════════
    // CREATE
    // ════════════════════════════════════════════════════

    @GetMapping("/reviews/form")
    public String form(Model model) {
        model.addAttribute("review", new BookReviewDTO());
        return "/reviewui/review-form";
    }

    @PostMapping("/reviews/form")
    public String create(@ModelAttribute BookReviewDTO dto, Model model) {

        // ── Duplicate review check ──────────────────────────
        // A review is uniquely identified by reviewerId + ISBN.
        // Before hitting POST, verify this combination does not already exist.
        // This prevents the backend from silently updating instead of creating.
        if (reviewExists(dto.getReviewerId(), dto.getIsbn())) {
            model.addAttribute("review", dto);
            model.addAttribute("errorMsg",
                    "A review by Reviewer ID " + dto.getReviewerId()
                    + " for ISBN \"" + dto.getIsbn() + "\" already exists. "
                    + "Use the Update form to modify an existing review.");
            return "/reviewui/review-form";
        }

        try {
            restClient.post()
                    .uri("/api/v1/reviews/book")
                    .body(dto)
                    .retrieve()
                    .body(BookReviewDTO.class);

            model.addAttribute("review", new BookReviewDTO());
            model.addAttribute("successMsg",
                    "Review for ISBN \"" + dto.getIsbn() + "\" submitted successfully!");

        } catch (RestClientException e) {
            model.addAttribute("review", dto);
            model.addAttribute("errorMsg", extractMessage(e));
        }
        return "/reviewui/review-form";
    }

    // ════════════════════════════════════════════════════
    // UPDATE
    // ════════════════════════════════════════════════════

    @GetMapping("/reviews/update-form")
    public String updateForm(Model model) {
        model.addAttribute("review", new BookReviewDTO());
        return "/reviewui/review-update-form";
    }

    @PostMapping("/reviews/update")
    public String update(@ModelAttribute BookReviewDTO dto, Model model) {
        try {
            restClient.put()
                    .uri("/api/v1/reviews/book")
                    .body(dto)
                    .retrieve()
                    .toBodilessEntity();

            model.addAttribute("review", new BookReviewDTO());
            model.addAttribute("successMsg",
                    "Review for ISBN \"" + dto.getIsbn() + "\" updated successfully!");

        } catch (RestClientException e) {
            model.addAttribute("review", dto);
            model.addAttribute("errorMsg", extractMessage(e));
        }
        return "/reviewui/review-update-form";
    }

    // ════════════════════════════════════════════════════
    // DELETE
    // ════════════════════════════════════════════════════

    @GetMapping("/reviews/{reviewerId}/delete/{isbn}")
    public String delete(@PathVariable String isbn,
                         @PathVariable int reviewerId,
                         RedirectAttributes redirectAttributes) {
        try {
            restClient.delete()
                    .uri("/api/v1/reviews/{reviewerId}/book/{isbn}", reviewerId, isbn)
                    .retrieve()
                    .toBodilessEntity();

            redirectAttributes.addFlashAttribute("successMsg",
                    "Review for ISBN \"" + isbn + "\" by Reviewer ID " + reviewerId + " deleted successfully!");

        } catch (RestClientException e) {
            redirectAttributes.addFlashAttribute("errorMsg", extractMessage(e));
        }

        return "redirect:/ui/review-dashboard";
    }
}