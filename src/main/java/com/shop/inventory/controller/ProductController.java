package com.shop.inventory.controller;

import com.shop.inventory.entity.Part;
import com.shop.inventory.entity.Product;
import com.shop.inventory.service.InventoryService;
import com.shop.inventory.validator.ProductValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final InventoryService inventoryService;
    private final ProductValidator productValidator;

    public ProductController(InventoryService inventoryService, ProductValidator productValidator) {
        this.inventoryService = inventoryService;
        this.productValidator = productValidator;
    }

    @GetMapping("/add")
    public String addProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("allParts", inventoryService.findAllParts());
        return "productform";
    }

    @PostMapping("/add")
    public String addProduct(@ModelAttribute("product") Product product, BindingResult result,
                            @RequestParam(required = false) List<Long> partIds, Model model) {
        if (product.getParts() == null) product.setParts(new HashSet<>());
        if (partIds != null) {
            Set<Part> parts = new HashSet<>();
            for (Long pid : partIds) {
                Part p = inventoryService.findPartById(pid);
                if (p != null) parts.add(p);
            }
            product.setParts(parts);
        }
        productValidator.validate(product, result);
        if (result.hasErrors()) {
            model.addAttribute("allParts", inventoryService.findAllParts());
            model.addAttribute("globalErrors", result.getGlobalErrors());
            return "productform";
        }
        product.setInv(0);
        inventoryService.saveProduct(product);
        return "redirect:/mainscreen";
    }

    @GetMapping("/update/{id}")
    public String updateProductForm(@PathVariable long id, Model model) {
        Product product = inventoryService.findProductById(id);
        if (product == null) return "redirect:/mainscreen";
        model.addAttribute("product", product);
        model.addAttribute("allParts", inventoryService.findAllParts());
        return "productform";
    }

    @PostMapping("/update")
    public String updateProduct(@ModelAttribute("product") Product product, BindingResult result,
                                @RequestParam(required = false) List<Long> partIds,
                                Model model, RedirectAttributes redirectAttributes) {
        Product existing = inventoryService.findProductById(product.getId());
        if (existing == null) return "redirect:/mainscreen";
        int previousInv = existing.getInv();
        Set<Part> parts = new HashSet<>();
        if (partIds != null) {
            for (Long pid : partIds) {
                Part p = inventoryService.findPartById(pid);
                if (p != null) parts.add(p);
            }
        }
        product.setParts(parts);
        productValidator.validate(product, result);
        productValidator.validateProductInventoryUpdate(product, previousInv, result);
        if (result.hasErrors()) {
            model.addAttribute("product", product);
            model.addAttribute("allParts", inventoryService.findAllParts());
            model.addAttribute("globalErrors", result.getGlobalErrors());
            return "productform";
        }
        int delta = product.getInv() - previousInv;
        if (delta > 0 && product.getParts() != null) {
            for (Part part : product.getParts()) {
                Part p = inventoryService.findPartById(part.getId());
                if (p != null) {
                    p.setInv(p.getInv() - delta);
                    inventoryService.savePart(p);
                }
            }
        }
        inventoryService.saveProduct(product);
        return "redirect:/mainscreen";
    }

    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable long id) {
        Product product = inventoryService.findProductById(id);
        if (product != null) {
            inventoryService.deleteProduct(product);
        }
        return "redirect:/mainscreen";
    }

    @PostMapping("/buy/{id}")
    public String buyNow(@PathVariable long id, RedirectAttributes redirectAttributes) {
        boolean success = inventoryService.buyNow(id);
        if (success) {
            redirectAttributes.addFlashAttribute("buyMessage", "Purchase successful. Product inventory decremented by one.");
        } else {
            redirectAttributes.addFlashAttribute("buyMessage", "Purchase failed. Product may be out of stock or not found.");
        }
        return "redirect:/mainscreen";
    }
}
