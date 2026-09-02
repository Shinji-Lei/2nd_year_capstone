package smartcanteen.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A solid-food menu item. Supports optional add-on toppings.
 */
public class FoodItem extends MenuItem {
    private boolean isSpicy;
    private double extraToppingsPrice;
    private List<String> toppings;

    public FoodItem(String itemId, String name, double basePrice, Store store, boolean isSpicy) {
        super(itemId, name, basePrice, store);
        this.isSpicy = isSpicy;
        this.extraToppingsPrice = 0.0;
        this.toppings = new ArrayList<>();
    }

    public void addTopping(String toppingName, double price) {
        toppings.add(toppingName);
        extraToppingsPrice += price;
    }

    @Override
    public double calculateTotalPrice() {
        return getBasePrice() + extraToppingsPrice;
    }

    @Override
    public String getItemDetails() {
        StringBuilder sb = new StringBuilder(getName());
        if (isSpicy) {
            sb.append(" (Spicy)");
        }
        if (!toppings.isEmpty()) {
            sb.append(" + ").append(String.join(", ", toppings));
        }
        return sb.toString();
    }

    public boolean isSpicy() {
        return isSpicy;
    }

    public List<String> getToppings() {
        return toppings;
    }
}
