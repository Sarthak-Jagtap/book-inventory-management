package com.bookinventory.ui.controller;

import com.bookinventory.inventory.dto.InventoryRequest;
import com.bookinventory.inventory.service.InventoryAdminService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/store-owner-page")
public class AdminPageController {

    private final InventoryAdminService inventoryAdminService;

    public AdminPageController(InventoryAdminService inventoryAdminService) {
        this.inventoryAdminService = inventoryAdminService;
    }

    @GetMapping("/inventory")
    public String showInventoryPage(Model model) {
        model.addAttribute("inventoryList", inventoryAdminService.getAllInventory());
        return "admin-inventory-page";
    }

    @GetMapping("/inventory/book/{isbn}")
    public String showInventorySummaryByBook(@PathVariable String isbn, Model model) {
        model.addAttribute("isbn", isbn);
        model.addAttribute("summaryList", inventoryAdminService.getInventorySummaryByIsbn(isbn));
        return "admin-inventory-summary-page";
    }

    @GetMapping("/inventory/add")
    public String showAddInventoryPage(Model model) {
        model.addAttribute("inventoryRequest", new InventoryRequest());
        return "add-inventory-page";
    }

    @PostMapping("/inventory/add")
    public String addInventory(@ModelAttribute InventoryRequest request) {
        inventoryAdminService.addInventory(request);
        return "redirect:/store-owner-page/inventory";
    }
}