package com.shop.inventory.controller;

import com.shop.inventory.service.InventoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {

    private final InventoryService inventoryService;

    public MainController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/mainscreen")
    public String mainScreen(Model model,
                             @RequestParam(required = false) String partFilter,
                             @RequestParam(required = false) String productFilter) {
        inventoryService.addSampleInventoryIfEmpty();
        model.addAttribute("parts", inventoryService.searchParts(partFilter));
        model.addAttribute("products", inventoryService.searchProducts(productFilter));
        model.addAttribute("partFilter", partFilter != null ? partFilter : "");
        model.addAttribute("productFilter", productFilter != null ? productFilter : "");
        return "mainscreen";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }
}
