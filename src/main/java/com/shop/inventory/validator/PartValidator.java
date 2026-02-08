package com.shop.inventory.validator;

import com.shop.inventory.entity.Part;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Validates part inventory is between min and max (requirements G, H).
 */
@Component
public class PartValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Part.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Part part = (Part) target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "name.required");
        if (part.getPrice() < 0) {
            errors.rejectValue("price", "price.negative");
        }
        if (part.getInv() < 0) {
            errors.rejectValue("inv", "inv.negative");
        }
        if (part.getMin() < 0) {
            errors.rejectValue("min", "min.negative");
        }
        if (part.getMax() < 0) {
            errors.rejectValue("max", "max.negative");
        }
        if (part.getMin() > part.getMax()) {
            errors.rejectValue("min", "min.greaterThanMax");
        }
        // Only enforce inv between min and max when min/max are set (avoid rejecting when both 0 for new parts)
        if (part.getMax() >= part.getMin() && (part.getMin() > 0 || part.getMax() > 0)) {
            if (part.getInv() < part.getMin()) {
                errors.rejectValue("inv", "inv.belowMin", "Inventory is less than the minimum number of parts.");
            }
            if (part.getInv() > part.getMax()) {
                errors.rejectValue("inv", "inv.aboveMax", "Inventory is greater than the maximum.");
            }
        }
    }
}
