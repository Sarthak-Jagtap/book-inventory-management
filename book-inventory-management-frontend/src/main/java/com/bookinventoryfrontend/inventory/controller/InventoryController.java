package com.bookinventoryfrontend.inventory.controller;

import com.bookinventoryfrontend.common.dto.ApiResponse;
import com.bookinventoryfrontend.inventory.dto.InventoryRequest;
import com.bookinventoryfrontend.inventory.dto.InventoryResponse;
import com.bookinventoryfrontend.inventory.dto.InventorySummaryResponse;
import com.bookinventoryfrontend.inventory.dto.UpdateInventoryRequest;
import jakarta.validation.Valid;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/store-owner/inventory")
public class InventoryController {

    private final RestClient restClient;

    public InventoryController(RestClient restClient) {
        this.restClient = restClient;
    }

    private String extractErrorMessage(HttpStatusCodeException ex) {
        String body = ex.getResponseBodyAsString();
        if (body == null || body.isBlank()) return "Something went wrong.";
        try {
            int messageIndex = body.indexOf("\"message\"");
            if (messageIndex == -1) return body;
            int colonIndex = body.indexOf(':', messageIndex);
            int firstQuote = body.indexOf('"', colonIndex + 1);
            int secondQuote = body.indexOf('"', firstQuote + 1);
            if (firstQuote != -1 && secondQuote != -1) return body.substring(firstQuote + 1, secondQuote);
        } catch (Exception ignored) {}
        return body;
    }

    @GetMapping("/find-by-id")
    public String findInventoryById(@RequestParam Integer inventoryId) {
        return "redirect:/store-owner/inventory/" + inventoryId;
    }

    @GetMapping("/find-by-isbn")
    public String findInventoryByIsbn(@RequestParam String isbn) {
        return "redirect:/store-owner/inventory/book/" + isbn;
    }

    @GetMapping("/find-available")
    public String findAvailableInventoryByIsbn(@RequestParam String isbn) {
        return "redirect:/store-owner/inventory/available/" + isbn;
    }

    @GetMapping("/find-summary")
    public String findInventorySummaryByIsbn(@RequestParam String isbn) {
        return "redirect:/store-owner/inventory/summary/" + isbn;
    }

    @GetMapping("/find-by-rank")
    public String findInventoryByRank(@RequestParam Integer rank) {
        return "redirect:/store-owner/inventory/rank/" + rank;
    }

    @GetMapping("/edit")
    public String openEditPage(@RequestParam Integer inventoryId) {
        return "redirect:/store-owner/inventory/edit/" + inventoryId;
    }



    @GetMapping
    public String getAllInventory(
            @RequestParam(defaultValue = "inventoryId") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            Model model) {

        try {
            ApiResponse<List<InventoryResponse>> response = restClient.get()
                    .uri("/api/v1/store-owner/inventory")
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<List<InventoryResponse>>>() {});

            List<InventoryResponse> inventoryList = response != null ? response.getData() : List.of();
            inventoryList = sortInventory(inventoryList, sortBy, direction);

            model.addAttribute("inventoryList", inventoryList);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("direction", direction);
        } catch (HttpStatusCodeException ex) {
            model.addAttribute("apiError", extractErrorMessage(ex));
        }
        return "inventory/list";
    }

    @GetMapping("/{inventoryId}")
    public String getInventoryById(@PathVariable Integer inventoryId, Model model) {
        if (inventoryId == null || inventoryId < 1000000) {
            model.addAttribute("apiError", "Invalid Inventory ID. Must be >= 1000000.");
            return "inventory/details";
        }

        try {
            ApiResponse<InventoryResponse> response = restClient.get()
                    .uri("/api/v1/store-owner/inventory/{inventoryId}", inventoryId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<InventoryResponse>>() {});

            InventoryResponse data = response != null ? response.getData() : null;
            if (data == null) {
                model.addAttribute("apiError", "Item doesn't exist");
                return "inventory/details";
            }
            model.addAttribute("inventory", data);
            return "inventory/details";
        } catch (HttpStatusCodeException ex) {
            if (ex.getStatusCode().value() == 404) {
                model.addAttribute("apiError", "Item doesn't exist");
            } else {
                model.addAttribute("apiError", extractErrorMessage(ex));
            }
            return "inventory/details";
        }
    }

    @GetMapping("/book/{isbn}")
    public String getInventoryByIsbn(@PathVariable String isbn, Model model) {
        if (isbn == null || isbn.isBlank()) {
            model.addAttribute("apiError", "ISBN is required.");
            return "inventory/list";
        }
        try {
            ApiResponse<List<InventoryResponse>> response = restClient.get()
                    .uri("/api/v1/store-owner/inventory/book/{isbn}", isbn)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<List<InventoryResponse>>>() {});

            model.addAttribute("inventoryList", response != null ? response.getData() : List.of());
            model.addAttribute("isbn", isbn);
            return "inventory/list";
        } catch (HttpStatusCodeException ex) {
            model.addAttribute("apiError", extractErrorMessage(ex));
            return "inventory/list";
        }
    }

    @GetMapping("/available/{isbn}")
    public String getAvailableInventoryByIsbn(@PathVariable String isbn, Model model) {
        try {
            ApiResponse<List<InventoryResponse>> response = restClient.get()
                    .uri("/api/v1/store-owner/inventory/available/{isbn}", isbn)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<List<InventoryResponse>>>() {});

            model.addAttribute("inventoryList", response != null ? response.getData() : List.of());
            model.addAttribute("isbn", isbn);
            return "inventory/available-list";
        } catch (HttpStatusCodeException ex) {
            model.addAttribute("apiError", extractErrorMessage(ex));
            return "inventory/available-list";
        }
    }

    @GetMapping("/summary/{isbn}")
    public String getInventorySummaryByIsbn(@PathVariable String isbn, Model model) {
        try {
            ApiResponse<List<InventorySummaryResponse>> response = restClient.get()
                    .uri("/api/v1/store-owner/inventory/summary/{isbn}", isbn)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<List<InventorySummaryResponse>>>() {});

            model.addAttribute("summaryList", response != null ? response.getData() : List.of());
            model.addAttribute("isbn", isbn);
            return "inventory/summary";
        } catch (HttpStatusCodeException ex) {
            model.addAttribute("apiError", extractErrorMessage(ex));
            return "inventory/summary";
        }
    }

    @GetMapping("/rank/{rank}")
    public String getInventoryByRank(@PathVariable Integer rank, Model model) {
        try {
            ApiResponse<List<InventoryResponse>> response = restClient.get()
                    .uri("/api/v1/store-owner/inventory/rank/{rank}", rank)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<List<InventoryResponse>>>() {});

            model.addAttribute("inventoryList", response != null ? response.getData() : List.of());
            model.addAttribute("rank", rank);
            return "inventory/list";
        } catch (HttpStatusCodeException ex) {
            model.addAttribute("apiError", extractErrorMessage(ex));
            return "inventory/list";
        }
    }

    @GetMapping("/book-conditions")
    public String getAllBookConditions(Model model) {
        try {
            ApiResponse<List<com.bookinventoryfrontend.inventory.dto.BookConditionResponse>> response = restClient.get()
                    .uri("/api/v1/store-owner/book-conditions")
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<List<com.bookinventoryfrontend.inventory.dto.BookConditionResponse>>>() {});

            model.addAttribute("conditions", response != null ? response.getData() : List.of());
            return "inventory/condition-list";
        } catch (HttpStatusCodeException ex) {
            model.addAttribute("apiError", extractErrorMessage(ex));
            return "inventory/condition-list";
        }
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        if (!model.containsAttribute("inventoryRequest")) {
            model.addAttribute("inventoryRequest", new InventoryRequest());
        }
        return "inventory/add";
    }

    @PostMapping("/add")
    public String addInventory(@Valid @ModelAttribute("inventoryRequest") InventoryRequest request,
                               BindingResult bindingResult,
                               Model model) {
        if (bindingResult.hasErrors()) {
            return "inventory/add";
        }

        try {
            restClient.post()
                    .uri("/api/v1/store-owner/inventory")
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();

            model.addAttribute("successMessage", "Inventory item added successfully!");
            model.addAttribute("inventoryRequest", new InventoryRequest());
            return "inventory/add";
        } catch (HttpStatusCodeException ex) {
            model.addAttribute("apiError", extractErrorMessage(ex));
            return "inventory/add";
        }
    }

    @GetMapping("/edit/{inventoryId}")
    public String showEditForm(@PathVariable Integer inventoryId, Model model, RedirectAttributes redirectAttributes) {
        if (inventoryId == null || inventoryId < 1000000) {
            redirectAttributes.addFlashAttribute("apiError", "Invalid Inventory ID. Must be >= 1000000.");
            return "redirect:/store-owner/inventory";
        }

        try {
            ApiResponse<InventoryResponse> response = restClient.get()
                    .uri("/api/v1/store-owner/inventory/{inventoryId}", inventoryId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<InventoryResponse>>() {});

            InventoryResponse inventory = response != null ? response.getData() : null;
            if (inventory == null) {
                redirectAttributes.addFlashAttribute("apiError", "Item doesn't exist");
                return "redirect:/store-owner/inventory";
            }

            UpdateInventoryRequest updateRequest = new UpdateInventoryRequest();
            updateRequest.setRank(inventory.getRank());

            model.addAttribute("inventoryId", inventoryId);
            model.addAttribute("updateInventoryRequest", updateRequest);
            return "inventory/edit";
        } catch (HttpStatusCodeException ex) {
            if (ex.getStatusCode().value() == 404) {
                redirectAttributes.addFlashAttribute("apiError", "Item doesn't exist");
            } else {
                redirectAttributes.addFlashAttribute("apiError", extractErrorMessage(ex));
            }
            return "redirect:/store-owner/inventory";
        }
    }

    @PostMapping("/edit/{inventoryId}")
    public String updateInventory(@PathVariable Integer inventoryId,
                                  @Valid @ModelAttribute("updateInventoryRequest") UpdateInventoryRequest request,
                                  BindingResult bindingResult,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("inventoryId", inventoryId);
            return "inventory/edit";
        }

        try {
            restClient.put()
                    .uri("/api/v1/store-owner/inventory/{inventoryId}", inventoryId)
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();

            redirectAttributes.addFlashAttribute("successMessage", "Item updated successfully!");
            return "redirect:/team/inventory-module";
        } catch (HttpStatusCodeException ex) {
            model.addAttribute("inventoryId", inventoryId);
            model.addAttribute("apiError", extractErrorMessage(ex));
            return "inventory/edit";
        }
    }

    @PostMapping("/purchase/run")
    public String togglePurchaseStatus(@RequestParam Integer inventoryId, RedirectAttributes redirectAttributes) {
        try {
            // 1. Fetch current status
            ApiResponse<InventoryResponse> getResponse = restClient.get()
                    .uri("/api/v1/store-owner/inventory/{inventoryId}", inventoryId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<InventoryResponse>>() {});

            InventoryResponse inventory = getResponse != null ? getResponse.getData() : null;
            if (inventory == null) {
                redirectAttributes.addFlashAttribute("apiError", "Item doesn't exist");
                return "redirect:/team/inventory-module";
            }

            // 2. Toggle status
            boolean newStatus = !Boolean.TRUE.equals(inventory.getPurchased());

            UpdateInventoryRequest patchRequest = new UpdateInventoryRequest();
            patchRequest.setPurchased(newStatus);
            // Rank is technically not required by backend for toggle, but DTO might have @NotNull
            // Check UpdateInventoryRequest.java - backend doesn't have @NotNull on rank.
            
            // 3. Send PATCH
            restClient.patch()
                    .uri("/api/v1/store-owner/inventory/purchase/{inventoryId}", inventoryId)
                    .body(patchRequest)
                    .retrieve()
                    .toBodilessEntity();

            redirectAttributes.addFlashAttribute("successMessage", "Status toggled to: " + (newStatus ? "Purchased" : "Available"));
        } catch (HttpStatusCodeException ex) {
            redirectAttributes.addFlashAttribute("apiError", extractErrorMessage(ex));
        }
        return "redirect:/team/inventory-module";
    }

    @PostMapping("/delete/run")
    public String deleteInventory(@RequestParam Integer inventoryId, RedirectAttributes redirectAttributes) {
        try {
            // Check if exists first for better feedback
            restClient.get()
                    .uri("/api/v1/store-owner/inventory/{inventoryId}", inventoryId)
                    .retrieve()
                    .toBodilessEntity();

            restClient.delete()
                    .uri("/api/v1/store-owner/inventory/{inventoryId}", inventoryId)
                    .retrieve()
                    .toBodilessEntity();

            redirectAttributes.addFlashAttribute("successMessage", "Item deleted successfully.");
        } catch (HttpStatusCodeException ex) {
            if (ex.getStatusCode().value() == 404) {
                redirectAttributes.addFlashAttribute("apiError", "Item doesn't exist.");
            } else {
                redirectAttributes.addFlashAttribute("apiError", extractErrorMessage(ex));
            }
        }
        return "redirect:/team/inventory-module";
    }

    private List<InventoryResponse> sortInventory(List<InventoryResponse> list, String sortBy, String direction) {
        Comparator<InventoryResponse> comp;
        switch (sortBy) {
            case "isbn": comp = Comparator.comparing(i -> i.getIsbn() != null ? i.getIsbn() : ""); break;
            case "rank": comp = Comparator.comparing(i -> i.getRank() != null ? i.getRank() : 0); break;
            case "purchased": comp = Comparator.comparing(i -> i.getPurchased() != null && i.getPurchased()); break;
            default: comp = Comparator.comparing(i -> i.getInventoryId() != null ? i.getInventoryId() : 0); break;
        }
        if ("desc".equalsIgnoreCase(direction)) comp = comp.reversed();
        return list.stream().sorted(comp).toList();
    }
}