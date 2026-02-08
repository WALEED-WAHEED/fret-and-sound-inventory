# Shop Inventory Management

Spring Boot inventory application for **Fret & Sound Guitar Shop** — manage parts (strings, tuners, pickups, necks, bridges) and products (guitars, packs). Built for WGU assessment (Java, Spring Framework, HTML/Thymeleaf).

---

## Tech Stack

- **Java 17+** · **Spring Boot 2.7** · **Thymeleaf** · **Maven**
- In-memory storage with file persistence (`guitar_shop_inventory.dat`)

---

## Quick Start

**Prerequisites:** JDK 17+ and Maven 3.6+ installed, with `java` and `mvn` on your PATH.

```bash
# Clone the repository (or open the project folder)
cd shop-inventory-management

# Run the application
mvn spring-boot:run
```

When you see **Started InventoryApplication**, open in your browser:

**http://localhost:8080/mainscreen**

To stop: press **Ctrl+C** in the terminal.

---

## Run Tests

**With Maven (JUnit):**
```bash
mvn test
```

**With Java only (no Maven):**
```bash
mkdir out -Force
javac -d out src/main/java/com/shop/inventory/entity/Product.java src/main/java/com/shop/inventory/entity/Part.java src/main/java/com/shop/inventory/entity/InhousePart.java src/main/java/com/shop/inventory/entity/OutsourcedPart.java
javac -cp out -d out manual-test/com/shop/inventory/RunPartChecks.java
java -cp out com.shop.inventory.RunPartChecks
```
Expected: `Result: 2 passed, 0 failed`.

---

## Project Structure

```
src/main/java/com/shop/inventory/
├── InventoryApplication.java      # Entry point
├── controller/                     # Web controllers
├── entity/                         # Part, Product, InhousePart, OutsourcedPart
├── repository/                     # File-based inventory storage
├── service/                        # Business logic, sample data, buy-now
└── validator/                      # Part & product validation
src/main/resources/
├── application.properties
├── messages.properties
└── templates/                      # Thymeleaf HTML (mainscreen, about, part/product forms)
src/test/java/.../entity/
└── PartTest.java                  # Unit tests for Part min/max
```

---

## Customer: Fret & Sound Guitar Shop

Products are guitars (e.g., Classic Acoustic Guitar, Standard Electric Guitar, Beginner Guitar Pack). Parts are components such as strings, tuners, pickups, necks, and bridges. Sample inventory loads automatically when both part and product lists are empty.

---

## What to Submit (WGU Assessment)

- **Git repository URL** (Requirement A) – GitLab project URL.
- **Repository branch history** (Requirement A) – Commit messages and dates (e.g. `git log`). Complete tasks C–J before capturing.
- **This project** – Code and this README (B).
- **Supporting PDF** (if used) – Screenshots or other docs, submitted separately.
- **Professional communication** (K) – Run through Grammarly for Education before submitting.

---

## Where to Find Changes (Parts C–J)

Each note includes the **prompt**, **file name**, **line number(s)**, and **change** made.

---

**C. Customize the HTML user interface (shop name, product names, part names).**

| File | Line(s) | Change |
|------|---------|--------|
| `src/main/resources/templates/mainscreen.html` | 5, 27, 59–60, 86–87 | Title and H1 "Fret & Sound Guitar Shop"; Parts/Products tables show names from model. |
| `src/main/resources/templates/about.html` | 5, 19 | Shop name in title and H1. |
| `src/main/resources/templates/inhousepartform.html` | 11 | Page title includes shop name. |
| `src/main/resources/templates/outsourcedpartform.html` | 11 | Page title includes shop name. |
| `src/main/resources/templates/productform.html` | 11 | Page title includes shop name. |
| `src/main/java/com/shop/inventory/service/InventoryService.java` | 88–165 | Sample inventory: guitar-shop part and product names. |

---

**D. Add "About" page and navigation.**

| File | Line(s) | Change |
|------|---------|--------|
| `src/main/resources/templates/about.html` | (entire file) | About page for Fret & Sound Guitar Shop. |
| `src/main/resources/templates/mainscreen.html` | 29–31 | Nav: Main Screen, About. |
| `src/main/resources/templates/about.html` | 20–22 | Nav: Main Screen, About. |
| `src/main/java/com/shop/inventory/controller/MainController.java` | 32–35 | `GET /about` mapping. |

---

**E. Sample inventory (5 parts, 5 products; only when both lists empty).**

| File | Line(s) | Change |
|------|---------|--------|
| `src/main/java/com/shop/inventory/service/InventoryService.java` | 62–72 | `addSampleInventoryIfEmpty()`; returns false if parts or products non-empty. |
| `src/main/java/com/shop/inventory/service/InventoryService.java` | 74–165 | `addGuitarShopSampleInventory()`: 5 parts, 5 products; Set for product parts. |
| `src/main/java/com/shop/inventory/controller/MainController.java` | 21 | Main screen load calls `addSampleInventoryIfEmpty()`. |

---

**F. "Buy Now" button (next to Update/Delete; decrement product by one; message).**

| File | Line(s) | Change |
|------|---------|--------|
| `src/main/resources/templates/mainscreen.html` | 90–92 | Buy Now button; form POSTs to `/products/buy/{id}`. |
| `src/main/resources/templates/mainscreen.html` | 35–36 | Message div for `buyMessage` (success/error). |
| `src/main/java/com/shop/inventory/controller/ProductController.java` | 111–120 | `buyNow(id)`; RedirectAttributes for message. |
| `src/main/java/com/shop/inventory/service/InventoryService.java` | 168–176 | `buyNow(productId)` decrements product only; returns boolean. |

---

**G. Parts track max/min inventory (entity, sample, forms, storage file, enforcement).**

| File | Line(s) | Change |
|------|---------|--------|
| `src/main/java/com/shop/inventory/entity/Part.java` | 10–11, 76–92 | `min`, `max` fields and getters/setters. |
| `src/main/java/com/shop/inventory/service/InventoryService.java` | 88–135 | Sample parts use `setMin()`/`setMax()`. |
| `src/main/resources/templates/inhousepartform.html` | 38–48 | Min/Max inputs. |
| `src/main/resources/templates/outsourcedpartform.html` | 38–48 | Min/Max inputs. |
| `src/main/java/com/shop/inventory/repository/FileInventoryRepository.java` | 18 | Storage file: `guitar_shop_inventory.dat`. |
| `src/main/java/com/shop/inventory/validator/PartValidator.java` | 28–35 | Validation: inv between min and max. |
| `src/main/resources/templates/mainscreen.html` | 62–63 | Parts table: Min, Max columns. |

---

**H. Validation (low inventory, product update lowers part below min, inv > max).**

| File | Line(s) | Change |
|------|---------|--------|
| `src/main/java/com/shop/inventory/validator/PartValidator.java` | 30–35 | Errors for inv < min, inv > max. |
| `src/main/java/com/shop/inventory/validator/ProductValidator.java` | 42–55 | `validateProductInventoryUpdate()`; global error if part would go below min. |
| `src/main/java/com/shop/inventory/controller/ProductController.java` | 82–83 | Call `validateProductInventoryUpdate()` before save. |
| `src/main/resources/templates/productform.html` | 30 | Display global errors. |
| Part form templates | 28, 32, 36, 40, 44 | `th:errors` for name, price, inv, min, max. |

---

**I. Unit tests for max/min in PartTest.**

| File | Line(s) | Change |
|------|---------|--------|
| `src/test/java/com/shop/inventory/entity/PartTest.java` | (entire file) | New test class. |
| `src/test/java/com/shop/inventory/entity/PartTest.java` | 17–28 | `partMinAndMaxFieldsAreStoredAndRetrievedCorrectly()`. |
| `src/test/java/com/shop/inventory/entity/PartTest.java` | 31–41 | `partInventoryWithinMinMaxIsValid()`. |

---

**J. Remove unused validators.**

| File | Line(s) | Change |
|------|---------|--------|
| N/A | — | Only PartValidator and ProductValidator exist; both used. No unused validators to remove. |

---

## License

This project was developed for educational assessment purposes.
