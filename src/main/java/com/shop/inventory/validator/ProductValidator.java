package com.shop.inventory.validator;

import com.shop.inventory.entity.Part;
import com.shop.inventory.entity.Product;
import com.shop.inventory.service.InventoryService;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Validates product and ensures updating product inventory does not lower part inventory below minimum (requirement H).
 */
@Component
public class ProductValidator implements Validator {

    private final InventoryService inventoryService;

    public ProductValidator(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Product.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Product product = (Product) target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "name.required");
        if (product.getPrice() < 0) {
            errors.rejectValue("price", "price.negative");
        }
        if (product.getInv() < 0) {
            errors.rejectValue("inv", "inv.negative");
        }
        if (product.getParts() != null) {
            double partsTotal = product.getParts().stream().mapToDouble(Part::getPrice).sum();
            if (partsTotal > product.getPrice()) {
                errors.rejectValue("price", "price.lessThanParts", "Product price must be greater than or equal to the sum of part prices.");
            }
        }
    }

    /**
     * Validates that increasing product inventory would not lower any associated part below its minimum.
     */
    public void validateProductInventoryUpdate(Product product, int previousInv, Errors errors) {
        if (product.getParts() == null) return;
        int delta = product.getInv() - previousInv;
        if (delta <= 0) return;
        for (Part part : product.getParts()) {
            Part current = inventoryService.findPartById(part.getId());
            if (current != null) {
                int partInvAfter = current.getInv() - delta;
                if (partInvAfter < current.getMin()) {
                    errors.reject("inv", "Part '" + current.getName() + "' inventory would fall below minimum (" + current.getMin() + ") after this update.");
                    break;
                }
            }
        }
    }
}
