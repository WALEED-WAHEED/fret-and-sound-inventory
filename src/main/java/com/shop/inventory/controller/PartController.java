package com.shop.inventory.controller;

import com.shop.inventory.entity.InhousePart;
import com.shop.inventory.entity.OutsourcedPart;
import com.shop.inventory.entity.Part;
import com.shop.inventory.service.InventoryService;
import com.shop.inventory.validator.PartValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/parts")
public class PartController {

    private final InventoryService inventoryService;
    private final PartValidator partValidator;

    public PartController(InventoryService inventoryService, PartValidator partValidator) {
        this.inventoryService = inventoryService;
        this.partValidator = partValidator;
    }

    @GetMapping("/add-inhouse")
    public String addInhouseForm(Model model) {
        model.addAttribute("part", new InhousePart());
        return "inhousepartform";
    }

    @GetMapping("/add-outsourced")
    public String addOutsourcedForm(Model model) {
        model.addAttribute("part", new OutsourcedPart());
        return "outsourcedpartform";
    }

    @PostMapping("/add-inhouse")
    public String addInhouse(@ModelAttribute("part") InhousePart part, BindingResult result, Model model) {
        partValidator.validate(part, result);
        if (result.hasErrors()) {
            return "inhousepartform";
        }
        setDefaultMinMaxIfZero(part);
        inventoryService.savePart(part);
        return "redirect:/mainscreen";
    }

    @PostMapping("/add-outsourced")
    public String addOutsourced(@ModelAttribute("part") OutsourcedPart part, BindingResult result, Model model) {
        partValidator.validate(part, result);
        if (result.hasErrors()) {
            return "outsourcedpartform";
        }
        setDefaultMinMaxIfZero(part);
        inventoryService.savePart(part);
        return "redirect:/mainscreen";
    }

    @GetMapping("/update/{id}")
    public String updatePartForm(@PathVariable long id, Model model) {
        Part part = inventoryService.findPartById(id);
        if (part == null) return "redirect:/mainscreen";
        model.addAttribute("part", part);
        if (part instanceof InhousePart) {
            return "inhousepartform";
        } else {
            return "outsourcedpartform";
        }
    }

    @PostMapping("/update-inhouse")
    public String updateInhousePart(@ModelAttribute("part") InhousePart part, BindingResult result, Model model) {
        partValidator.validate(part, result);
        if (result.hasErrors()) {
            model.addAttribute("part", part);
            return "inhousepartform";
        }
        inventoryService.savePart(part);
        return "redirect:/mainscreen";
    }

    @PostMapping("/update-outsourced")
    public String updateOutsourcedPart(@ModelAttribute("part") OutsourcedPart part, BindingResult result, Model model) {
        partValidator.validate(part, result);
        if (result.hasErrors()) {
            model.addAttribute("part", part);
            return "outsourcedpartform";
        }
        inventoryService.savePart(part);
        return "redirect:/mainscreen";
    }

    @PostMapping("/delete/{id}")
    public String deletePart(@PathVariable long id) {
        Part part = inventoryService.findPartById(id);
        if (part != null && !inventoryService.isPartUsedInProduct(part)) {
            inventoryService.deletePart(part);
        }
        return "redirect:/mainscreen";
    }

    private void setDefaultMinMaxIfZero(Part part) {
        if (part.getMax() <= 0) part.setMax(Math.max(part.getInv(), 100));
        if (part.getMin() < 0) part.setMin(0);
        if (part.getInv() < part.getMin()) part.setInv(part.getMin());
        if (part.getInv() > part.getMax()) part.setInv(part.getMax());
    }
}
