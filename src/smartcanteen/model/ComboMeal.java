package smartcanteen.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A bundle of other MenuItems sold together at a discount.
 */
public class ComboMeal extends MenuItem {
    private List<MenuItem> components;
    private double comboDiscount;

    public ComboMeal(String itemId, String name, double basePrice, Store store, double comboDiscount) {
        super(itemId, name, basePrice, store);
        this.components = new ArrayList<>();
        this.comboDiscount = comboDiscount;
    }

    public void addComponent(MenuItem item) {
        components.add(item);
    }

    @Override
    public double calculateTotalPrice() {
        double total = getBasePrice();
        for (MenuItem item : components) {
            total += item.calculateTotalPrice();
        }
        total -= comboDiscount;
        return Math.max(total, 0);
    }

    @Override
    public String getItemDetails() {
        StringBuilder sb = new StringBuilder(getName());
        if (!components.isEmpty()) {
            sb.append(" (");
            for (int i = 0; i < components.size(); i++) {
                sb.append(components.get(i).getName());
                if (i < components.size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append(")");
        }
        return sb.toString();
    }

    public List<MenuItem> getComponents() {
        return components;
    }
}
