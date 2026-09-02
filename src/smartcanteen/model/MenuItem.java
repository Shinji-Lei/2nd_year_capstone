package smartcanteen.model;

/**
 * Abstract superclass for anything that can be ordered.
 * Demonstrates Inheritance (subclassed by FoodItem, BeverageItem, ComboMeal)
 * and Polymorphism (calculateTotalPrice overridden per subclass).
 */
public abstract class MenuItem {
    private String itemId;
    private String name;
    private double basePrice;
    private Store store;

    protected MenuItem(String itemId, String name, double basePrice, Store store) {
        this.itemId = itemId;
        this.name = name;
        this.basePrice = basePrice;
        this.store = store;
    }

    public abstract double calculateTotalPrice();

    public abstract String getItemDetails();

    public String getItemId() {
        return itemId;
    }

    public String getName() {
        return name;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public Store getStore() {
        return store;
    }

    @Override
    public String toString() {
        return String.format("%s - \u20B1%.2f", name, calculateTotalPrice());
    }
}
