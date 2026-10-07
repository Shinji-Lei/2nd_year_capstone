# SmartCanteen — Self-Service Ordering System (Java Swing)

A desktop GUI implementation of the OOP Final Project proposal: a
multi-stall campus canteen ordering app for Cebu Eastern College's
College of Information Technology — with a customer ordering screen
**and** a live staff/kitchen display that update each other in real time.

## Requirements

- JDK 17 or later (the status badges use `switch` expressions that require 17+)

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

Once running, click **"Staff / Kitchen Display"** in the top-left of the
header to open the staff window alongside the customer window — orders
placed on the customer side appear there instantly.

## Features

### Customer side (`MainFrame`)
- **5-store tabbed navigation** — a pill-style nav bar switches between
  `StorePanel` tabs (one per `Store`), each listing that stall's menu in a
  dark-themed card layout with a quantity spinner (1–20) and an
  **Add to Cart** button per item.
- **Editable customer name** — a header text field drives the name printed
  on receipts; defaults to "Guest Customer" if left blank.
- **Multi-stall cart (`CartPanel`)** — a sidebar groups cart contents by
  store, showing each item's line total, a per-store subtotal, and a
  running grand total; **Clear Cart** empties it and both buttons disable
  automatically when the cart is empty.

### Checkout (`CheckoutDialog`)
- **Per-store payment method** — for every store in the cart, the customer
  chooses **Exact Cash** or **Big Bill / Custom Amount**.
- **Quick-bill entry** — `+100`, `+500`, `+1000` buttons append amounts into
  the input field; the field also accepts multiple bills at once
  (e.g. `100 100 100` or `500,500`) and sums them live as "Entered: ₱…".
- **Validation before confirming** — blocks checkout with a specific error
  if a Big Bill store has no amount entered, an invalid (non-numeric or
  ≤0) amount, or an amount below that store's subtotal.
- Confirming builds a `PaymentProcessor`, computes change per store, and
  pushes every paid sub-order onto the shared `OrderBoard` the kitchen
  display reads from.

### Receipt & live tracking (`ReceiptDialog`)
- **Master receipt** — `MasterReceipt.generateSummary()` prints customer
  name, items/prices/subtotal/cash/change per store, and the grand total.
- **Change-prep alerts** — any store paid with a Big Bill shows a
  "prepare change: ₱…" banner alongside its queue number.
- **Live order tracker** — a non-modal dialog that stays open and updates
  itself (via an `OrderBoard` listener) as staff advance each store's
  order from *In Preparation* to *Ready for Pickup*, with no manual
  refresh needed.

### Staff / Kitchen Display (`KitchenDisplayFrame`, `KitchenStorePanel`)
- A separate window with one pill-tab per store, showing every sub-order
  submitted to that stall as a live, auto-updating feed.
- Each order card shows its queue number, items, a color-coded status
  badge (**New Order → In Preparation → Ready for Pickup**), and a
  change-preparation alert for Big Bill payments.
- Staff advance an order with **Start Preparing Order** then **Mark Ready
  for Pickup** — each click updates the `OrderBoard`, which immediately
  refreshes the matching customer-side receipt tracker.

### Menu item types
| Type | Behavior |
|------|----------|
| `FoodItem` | Optional spicy flag; add-on toppings that add to the price and appear in the item description |
| `BeverageItem` | Selectable size (Small/Medium/Large) with an upsize fee (+₱10 / +₱20) and an ice-level field |
| `ComboMeal` | Bundles other `MenuItem`s together and applies a flat discount off their combined price |

### Sample data (`SampleData`)
Seeds 5 stalls so the app runs immediately — swap this for a real
menu/database loader for production use:

| Store | Sample items |
|-------|--------------|
| TUDTUD STORE | Steamed Rice, Chicken Adobo, Sizzling Sisig |
| SUAREZ STORE | Classic Cheeseburger (+Extra Cheese), French Fries, Burger Combo |
| SHINJI STORE | Pancit Canton, Special Lomi (spicy) |
| MARIEGINE STORE | Iced Tea, Milk Tea, Soft Drink |
| PRINCESS STORE | Taho, Halo-Halo |

## Where the OOP concepts live

| Concept | Where |
|---------|-------|
| Encapsulation | Private fields with public getters/setters throughout `model/` (e.g. `SubOrder.cashTendered`, `PaymentProcessor.grandTotal`) |
| Inheritance | `MenuItem` (abstract) → `FoodItem`, `BeverageItem`, `ComboMeal` |
| Polymorphism | `calculateTotalPrice()` and `getItemDetails()` overridden differently in each `MenuItem` subclass |
| Abstraction | `PaymentProcessor.processCheckout()` hides per-store validation and change math behind one call |
| Observer-style pub/sub | `OrderBoard` lets the kitchen display and the customer's receipt tracker both react live to the same submitted orders, without polling |

## Project layout

```
src/smartcanteen/
  Main.java                     entry point
  model/
    Store.java
    MenuItem.java                (abstract)
    FoodItem.java
    BeverageItem.java
    ComboMeal.java
    Customer.java
    OrderItem.java               menu item + quantity line
    SubOrder.java                one store's portion of an order; tracks status/payment/queue #
    PaymentProcessor.java        validates & processes all sub-orders in a checkout
    MasterReceipt.java           builds the printable multi-store receipt
    OrderBoard.java              shared pub/sub board linking customer checkout to staff display
    SampleData.java              seeds 5 sample stalls/menus
  gui/
    MainFrame.java                customer main window: nav bar + store tabs + cart
    StorePanel.java               one store's menu with qty spinners & add-to-cart
    CartPanel.java                sidebar cart grouped by store
    CheckoutDialog.java           per-store payment method selection
    ReceiptDialog.java            master receipt + live per-store status tracker
    KitchenDisplayFrame.java      staff window: one tab per store
    KitchenStorePanel.java        staff view of one store's incoming orders + status controls
```

`SampleData.java` seeds the 5 stalls above with placeholder menu items and
prices — swap this out for your real menu data or a file/database-backed
loader for an actual deployment.


screenshots
<img width="1084" height="687" alt="image" src="https://github.com/user-attachments/assets/90affc1b-5df1-4c5b-a458-eb932b05fbed" />
<img width="518" height="609" alt="image" src="https://github.com/user-attachments/assets/a1e0b95c-860b-46b6-b43a-7c470d8f13d4" />
<img width="461" height="649" alt="image" src="https://github.com/user-attachments/assets/a002aded-c91e-47ed-b18f-06db78487b71" />
<img width="803" height="691" alt="image" src="https://github.com/user-attachments/assets/c8c0a72b-3d43-48a8-ab17-cda813a00181" />
<img width="806" height="689" alt="image" src="https://github.com/user-attachments/assets/7ba0c3c2-67b7-4432-969e-afd102802eb7" />
<img width="460" height="649" alt="image" src="https://github.com/user-attachments/assets/f4e1dbde-82f3-402a-966c-d1188de129b5" />







