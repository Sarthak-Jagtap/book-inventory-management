package com.bookinventoryfrontend.reviewer.controller;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bookinventoryfrontend.reviewer.dto.ReviewerDTO;

@Controller
@RequestMapping("/ui")
public class ReviewerUIController {

    @Autowired
    private RestClient restClient;

    // ════════════════════════════════════════════════════
    // HELPER — extract clean "message" from JSON error body
    // ════════════════════════════════════════════════════

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
    // HELPER — check if a reviewer ID already exists
    // Returns true if the ID is already taken, false otherwise
    // ════════════════════════════════════════════════════

    private boolean reviewerExists(int id) {
        try {
            List<ReviewerDTO> result = restClient.get()
                    .uri("/api/v1/reviewer/{id}", id)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<ReviewerDTO>>() {});
            return result != null && !result.isEmpty();
        } catch (RestClientException e) {
            // If the backend returns 404 or any error, the reviewer does not exist
            return false;
        }
    }

    // ════════════════════════════════════════════════════
    // DASHBOARD
    // ════════════════════════════════════════════════════

    @GetMapping("/review-dashboard")
    public String dashboard() {
        return "/reviewui/review-dashboard";
    }

    // ════════════════════════════════════════════════════
    // READ OPERATIONS
    // ════════════════════════════════════════════════════

    @GetMapping("/reviewer/{id}")
    public String getReviewer(@PathVariable int id, Model model) {
        try {
            List<ReviewerDTO> data = restClient.get()
                    .uri("/api/v1/reviewer/{id}", id)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<ReviewerDTO>>() {});
            model.addAttribute("data", data);
        } catch (RestClientException e) {
            model.addAttribute("data", extractMessage(e));
        }
        return "/reviewui/reviewer";
    }

    @GetMapping(value = "/reviewer", params = "name")
    public String getByName(@RequestParam String name, Model model) {
        try {
            List<ReviewerDTO> data = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/reviewer")
                            .queryParam("name", name)
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<ReviewerDTO>>() {});
            model.addAttribute("data", data);
        } catch (RestClientException e) {
            model.addAttribute("data", extractMessage(e));
        }
        return "/reviewui/reviewer";
    }

    @GetMapping(value = "/reviewer", params = "employedBy")
    public String getByCompany(@RequestParam String employedBy, Model model) {
        try {
            List<ReviewerDTO> data = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/reviewer")
                            .queryParam("employedBy", employedBy)
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<ReviewerDTO>>() {});
            model.addAttribute("data", data);
        } catch (RestClientException e) {
            model.addAttribute("data", extractMessage(e));
        }
        return "/reviewui/reviewer";
    }

    @GetMapping("/reviewers")
    public String getAll(Model model) {
        try {
            List<ReviewerDTO> data = restClient.get()
                    .uri("/api/v1/reviewer")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<ReviewerDTO>>() {});
            model.addAttribute("data", data);
        } catch (RestClientException e) {
            model.addAttribute("data", extractMessage(e));
        }
        return "/reviewui/reviewer";
    }

    // ════════════════════════════════════════════════════
    // CREATE
    // ════════════════════════════════════════════════════

    @GetMapping("/reviewer/form")
    public String form(Model model) {
        model.addAttribute("reviewer", new ReviewerDTO());
        return "/reviewui/reviewer-form";
    }

    @PostMapping("/reviewer/form")
    public String create(@ModelAttribute ReviewerDTO dto, Model model) {

        // ── Duplicate ID check ──────────────────────────────
        // Before hitting POST, verify no reviewer with this ID exists.
        // This prevents the backend from silently updating instead of creating.
        if (reviewerExists(dto.getReviewerID())) {
            model.addAttribute("reviewer", dto);
            model.addAttribute("errorMsg",
                    "Reviewer with ID " + dto.getReviewerID() + " already exists. "
                    + "Use the Update form to modify an existing reviewer.");
            return "/reviewui/reviewer-form";
        }

        try {
            restClient.post()
                    .uri("/api/v1/reviewer")
                    .body(dto)
                    .retrieve()
                    .body(ReviewerDTO.class);

            model.addAttribute("reviewer", new ReviewerDTO());
            model.addAttribute("successMsg", "Reviewer \"" + dto.getName() + "\" created successfully!");

        } catch (RestClientException e) {
            model.addAttribute("reviewer", dto);
            model.addAttribute("errorMsg", extractMessage(e));
        }
        return "/reviewui/reviewer-form";
    }

    // ════════════════════════════════════════════════════
    // UPDATE
    // ════════════════════════════════════════════════════

    @GetMapping("/reviewer/update-form")
    public String updateForm(Model model) {
        model.addAttribute("reviewer", new ReviewerDTO());
        return "/reviewui/reviewer-update-form";
    }

    @PostMapping("/reviewer/update-form")
    public String update(@ModelAttribute ReviewerDTO dto, Model model) {
        try {
            restClient.put()
                    .uri("/api/v1/reviewer")
                    .body(dto)
                    .retrieve()
                    .toBodilessEntity();

            model.addAttribute("reviewer", new ReviewerDTO());
            model.addAttribute("successMsg", "Reviewer ID " + dto.getReviewerID() + " updated successfully!");

        } catch (RestClientException e) {
            model.addAttribute("reviewer", dto);
            model.addAttribute("errorMsg", extractMessage(e));
        }
        return "/reviewui/reviewer-update-form";
    }

    // ════════════════════════════════════════════════════
    // DELETE
    // ════════════════════════════════════════════════════

    @GetMapping("/reviewer/delete/{id}")
    public String delete(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            restClient.delete()
                    .uri("/api/v1/reviewer/{id}", id)
                    .retrieve()
                    .toBodilessEntity();

            redirectAttributes.addFlashAttribute("successMsg",
                    "Reviewer ID " + id + " deleted successfully!");

        } catch (RestClientException e) {
            redirectAttributes.addFlashAttribute("errorMsg", extractMessage(e));
        }

        return "redirect:/ui/review-dashboard";
    }
}