# SmartCanteen — Self-Service Ordering System (Java Swing)

A desktop GUI implementation of the OOP Final Project proposal: a
multi-stall campus canteen ordering app for Cebu Eastern College's
College of Information Technology.

## Requirements
- JDK 17 or later (uses `switch` statements compatible with 17+)

## How to compile and run

From the project root (the folder containing `src/`):

```bash
# Compile
find src -name "*.java" > sources.txt
javac -d out @sources.txt

# Run
java -cp out smartcanteen.Main
```

Or, in an IDE (IntelliJ IDEA, as named in the proposal):
1. Open this folder as a project.
2. Mark `src` as the Sources Root.
3. Run `smartcanteen.Main`.

## What it does

- **5-store tabbed navigation** — `MainFrame` builds one `StorePanel`
  tab per `Store`, each listing that stall's menu with quantity
  selectors and Add-to-cart buttons.
- **Multi-stall cart** — `CartPanel` keeps a running per-store
  breakdown and grand total as items are added from any tab.
- **Per-store payment setup** — `CheckoutDialog` lets the customer
  choose Exact Cash or a Big Bill (₱500 / ₱1,000 quick buttons) for
  each stall separately, validates the tendered amount against that
  stall's subtotal, and blocks checkout until every stall is covered.
- **Targeted change alerts** — stalls paid with a Big Bill get a
  "prepare change" alert (their queue number + amount), shown in the
  receipt dialog as a stand-in for a kitchen display.
- **Master receipt** — `MasterReceipt.generateSummary()` prints
  customer info, items and prices per store, subtotal, cash paid, and
  change per stall, plus the grand total and each stall's queue number.

## Where the OOP concepts live

| Concept | Where |
|---|---|
| Encapsulation | Private fields with public getters/setters throughout `model/` (e.g. `SubOrder.cashTendered`, `PaymentProcessor.grandTotal`) |
| Inheritance | `MenuItem` (abstract) → `FoodItem`, `BeverageItem`, `ComboMeal` |
| Polymorphism | `calculateTotalPrice()` and `getItemDetails()` overridden differently in each `MenuItem` subclass |
| Abstraction | `PaymentProcessor.processCheckout()` hides per-store validation and change math behind one call |

## Project layout

```
src/smartcanteen/
  Main.java                 entry point
  model/
    Store.java
    MenuItem.java            (abstract)
    FoodItem.java
    BeverageItem.java
    ComboMeal.java
    Customer.java
    OrderItem.java            (menu item + quantity line)
    SubOrder.java
    PaymentProcessor.java
    MasterReceipt.java
    SampleData.java           sample stores/menu so the app runs immediately
  gui/
    MainFrame.java
    StorePanel.java
    CartPanel.java
    CheckoutDialog.java
    ReceiptDialog.java
```

`SampleData.java` seeds 5 sample stalls (Kusina ni Aling Nena, Burger
Bites, Noodle House, Drink Stop, Sweet Treats) with placeholder menu
items and prices — swap this out for your real menu data or a
file/database-backed loader.
