package com.shop.inventory;

import com.shop.inventory.entity.InhousePart;
import com.shop.inventory.entity.OutsourcedPart;

/**
 * Same checks as PartTest – run with Java only (no Maven):
 *   javac -d out src/main/java/com/shop/inventory/entity/Product.java src/main/java/com/shop/inventory/entity/Part.java src/main/java/com/shop/inventory/entity/InhousePart.java src/main/java/com/shop/inventory/entity/OutsourcedPart.java
 *   javac -cp out -d out manual-test/com/shop/inventory/RunPartChecks.java
 *   java -cp out com.shop.inventory.RunPartChecks
 */
public class RunPartChecks {

    public static void main(String[] args) {
        int passed = 0;
        int failed = 0;

        // Test 1: partMinAndMaxFieldsAreStoredAndRetrievedCorrectly
        try {
            InhousePart part = new InhousePart();
            part.setId(1L);
            part.setName("Test Part");
            part.setPrice(10.0);
            part.setInv(50);
            part.setMin(10);
            part.setMax(100);
            if (part.getMin() != 10 || part.getMax() != 100 || part.getInv() != 50) {
                throw new AssertionError("getMin/getMax/getInv mismatch");
            }
            System.out.println("PASS: partMinAndMaxFieldsAreStoredAndRetrievedCorrectly");
            passed++;
        } catch (Throwable t) {
            System.err.println("FAIL: partMinAndMaxFieldsAreStoredAndRetrievedCorrectly - " + t.getMessage());
            failed++;
        }

        // Test 2: partInventoryWithinMinMaxIsValid
        try {
            OutsourcedPart part = new OutsourcedPart();
            part.setMin(5);
            part.setMax(20);
            part.setInv(12);
            if (part.getMin() != 5 || part.getMax() != 20 || part.getInv() != 12) {
                throw new AssertionError("getMin/getMax/getInv mismatch");
            }
            if (part.getInv() < part.getMin() || part.getInv() > part.getMax()) {
                throw new AssertionError("inv not within min-max");
            }
            System.out.println("PASS: partInventoryWithinMinMaxIsValid");
            passed++;
        } catch (Throwable t) {
            System.err.println("FAIL: partInventoryWithinMinMaxIsValid - " + t.getMessage());
            failed++;
        }

        System.out.println("---");
        System.out.println("Result: " + passed + " passed, " + failed + " failed");
        if (failed != 0) {
            System.exit(1);
        }
    }
}
