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
        if (body == null || body.isBlank()) {
            return "Something went wrong.";
        }

        try {
            int messageIndex = body.indexOf("\"message\"");
            if (messageIndex == -1) {
                return body;
            }

            int colonIndex = body.indexOf(':', messageIndex);
            int firstQuote = body.indexOf('"', colonIndex + 1);
            int secondQuote = body.indexOf('"', firstQuote + 1);

            if (firstQuote != -1 && secondQuote != -1) {
                return body.substring(firstQuote + 1, secondQuote);
            }
        } catch (Exception ignored) {
        }

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

    @PostMapping("/purchase/run")
    public String runPurchase(@RequestParam Integer inventoryId,
                              RedirectAttributes redirectAttributes) {
        if (inventoryId == null || inventoryId <= 0) {
            redirectAttributes.addFlashAttribute("apiError", "Inventory ID must be greater than 0.");
            return "redirect:/inventory-cart-dashboard";
        }

        try {
            restClient.patch()
                    .uri("/api/v1/store-owner/inventory/purchase/{inventoryId}", inventoryId)
                    .retrieve()
                    .toBodilessEntity();

            redirectAttributes.addFlashAttribute("successMessage", "Purchase status updated successfully.");
        } catch (HttpStatusCodeException ex) {
            redirectAttributes.addFlashAttribute("apiError", extractErrorMessage(ex));
        }

        return "redirect:/inventory-cart-dashboard";
    }

    @PostMapping("/delete/run")
    public String runDelete(@RequestParam Integer inventoryId,
                            RedirectAttributes redirectAttributes) {
        if (inventoryId == null || inventoryId <= 0) {
            redirectAttributes.addFlashAttribute("apiError", "Inventory ID must be greater than 0.");
            return "redirect:/inventory-cart-dashboard";
        }

        try {
            restClient.delete()
                    .uri("/api/v1/store-owner/inventory/{inventoryId}", inventoryId)
                    .retrieve()
                    .toBodilessEntity();

            redirectAttributes.addFlashAttribute("successMessage",
                    "Inventory record found and deleted successfully.");
        } catch (HttpStatusCodeException ex) {
            redirectAttributes.addFlashAttribute("apiError", extractErrorMessage(ex));
        }

        return "redirect:/inventory-cart-dashboard";
    }

    @GetMapping
    public String getAllInventory(
            @RequestParam(defaultValue = "inventoryId") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            Model model) {

        ApiResponse<List<InventoryResponse>> response = restClient.get()
                .uri("/api/v1/store-owner/inventory")
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponse<List<InventoryResponse>>>() {});

        List<InventoryResponse> inventoryList = response != null ? response.getData() : List.of();

        inventoryList = sortInventory(inventoryList, sortBy, direction);

        model.addAttribute("inventoryList", inventoryList);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        return "inventory/list";
    }

    @GetMapping("/{inventoryId}")
    public String getInventoryById(@PathVariable Integer inventoryId, Model model) {
        if (inventoryId == null || inventoryId <= 0) {
            model.addAttribute("apiError", "Inventory ID must be greater than 0.");
            model.addAttribute("inventory", null);
            return "inventory/details";
        }

        try {
            ApiResponse<InventoryResponse> response = restClient.get()
                    .uri("/api/v1/store-owner/inventory/{inventoryId}", inventoryId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<InventoryResponse>>() {});

            model.addAttribute("inventory", response != null ? response.getData() : null);
            return "inventory/details";
        } catch (HttpStatusCodeException ex) {
            model.addAttribute("apiError", extractErrorMessage(ex));
            model.addAttribute("inventory", null);
            return "inventory/details";
        }
    }

    @GetMapping("/book/{isbn}")
    public String getInventoryByIsbn(@PathVariable String isbn, Model model) {
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
            model.addAttribute("inventoryList", List.of());
            model.addAttribute("isbn", isbn);
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
            model.addAttribute("inventoryList", List.of());
            model.addAttribute("isbn", isbn);
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
            model.addAttribute("summaryList", List.of());
            model.addAttribute("isbn", isbn);
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
            model.addAttribute("inventoryList", List.of());
            model.addAttribute("rank", rank);
            return "inventory/list";
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

            return "redirect:/store-owner/inventory";
        } catch (HttpStatusCodeException ex) {
            model.addAttribute("apiError", extractErrorMessage(ex));
            return "inventory/add";
        }
    }

    @GetMapping("/edit/{inventoryId}")
    public String showEditForm(@PathVariable Integer inventoryId, Model model) {
        if (inventoryId == null || inventoryId <= 0) {
            model.addAttribute("apiError", "Inventory ID must be greater than 0.");
            model.addAttribute("inventoryId", inventoryId);
            model.addAttribute("updateInventoryRequest", new UpdateInventoryRequest());
            return "inventory/edit";
        }

        try {
            ApiResponse<InventoryResponse> response = restClient.get()
                    .uri("/api/v1/store-owner/inventory/{inventoryId}", inventoryId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<InventoryResponse>>() {});

            InventoryResponse inventory = response != null ? response.getData() : null;

            UpdateInventoryRequest updateRequest = new UpdateInventoryRequest();
            if (inventory != null) {
                updateRequest.setRank(inventory.getRank());
            }

            model.addAttribute("inventoryId", inventoryId);
            model.addAttribute("updateInventoryRequest", updateRequest);
            return "inventory/edit";
        } catch (HttpStatusCodeException ex) {
            model.addAttribute("apiError", extractErrorMessage(ex));
            model.addAttribute("inventoryId", inventoryId);
            model.addAttribute("updateInventoryRequest", new UpdateInventoryRequest());
            return "inventory/edit";
        }
    }

    @PostMapping("/edit/{inventoryId}")
    public String updateInventory(@PathVariable Integer inventoryId,
                                  @Valid @ModelAttribute("updateInventoryRequest") UpdateInventoryRequest request,
                                  BindingResult bindingResult,
                                  Model model) {
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

            return "redirect:/store-owner/inventory";
        } catch (HttpStatusCodeException ex) {
            model.addAttribute("inventoryId", inventoryId);
            model.addAttribute("apiError", extractErrorMessage(ex));
            return "inventory/edit";
        }
    }

    @PostMapping("/purchase/{inventoryId}")
    public String markAsPurchased(@PathVariable Integer inventoryId, Model model) {
        try {
            restClient.patch()
                    .uri("/api/v1/store-owner/inventory/purchase/{inventoryId}", inventoryId)
                    .retrieve()
                    .toBodilessEntity();

            return "redirect:/store-owner/inventory";
        } catch (HttpStatusCodeException ex) {
            model.addAttribute("apiError", extractErrorMessage(ex));

            ApiResponse<List<InventoryResponse>> response = restClient.get()
                    .uri("/api/v1/store-owner/inventory")
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<List<InventoryResponse>>>() {});

            model.addAttribute("inventoryList", response != null ? response.getData() : List.of());
            return "inventory/list";
        }
    }

    @PostMapping("/delete/{inventoryId}")
    public String deleteInventory(@PathVariable Integer inventoryId, Model model) {
        try {
            restClient.delete()
                    .uri("/api/v1/store-owner/inventory/{inventoryId}", inventoryId)
                    .retrieve()
                    .toBodilessEntity();

            return "redirect:/store-owner/inventory";
        } catch (HttpStatusCodeException ex) {
            model.addAttribute("apiError", extractErrorMessage(ex));

            ApiResponse<List<InventoryResponse>> response = restClient.get()
                    .uri("/api/v1/store-owner/inventory")
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<List<InventoryResponse>>>() {});

            model.addAttribute("inventoryList", response != null ? response.getData() : List.of());
            return "inventory/list";
        }
    }

    private List<InventoryResponse> sortInventory(List<InventoryResponse> list,
                                                  String sortBy,
                                                  String direction) {
        Comparator<InventoryResponse> comparator;

        switch (sortBy) {
            case "isbn":
                comparator = Comparator.comparing(
                        item -> item.getIsbn() == null ? "" : item.getIsbn(),
                        String.CASE_INSENSITIVE_ORDER
                );
                break;
            case "rank":
                comparator = Comparator.comparing(
                        item -> item.getRank() == null ? Integer.MIN_VALUE : item.getRank()
                );
                break;
            case "purchased":
                comparator = Comparator.comparing(
                        item -> item.getPurchased() != null && item.getPurchased()
                );
                break;
            case "inventoryId":
            default:
                comparator = Comparator.comparing(
                        item -> item.getInventoryId() == null ? Integer.MIN_VALUE : item.getInventoryId()
                );
                break;
        }

        if ("desc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }

        return list.stream().sorted(comparator).toList();
    }
}