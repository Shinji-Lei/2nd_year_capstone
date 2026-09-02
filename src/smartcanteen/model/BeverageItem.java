package smartcanteen.model;

/**
 * A drink menu item with a selectable size that affects price.
 */
public class BeverageItem extends MenuItem {
    public enum Size { SMALL, MEDIUM, LARGE }

    private Size drinkSize;
    private String iceLevel;
    private double upsizeFee;

    public BeverageItem(String itemId, String name, double basePrice, Store store) {
        super(itemId, name, basePrice, store);
        this.drinkSize = Size.SMALL;
        this.iceLevel = "Normal Ice";
        this.upsizeFee = 0.0;
    }

    public void setDrinkSize(Size size) {
        this.drinkSize = size;
        switch (size) {
            case SMALL:
                upsizeFee = 0.0;
                break;
            case MEDIUM:
                upsizeFee = 10.0;
                break;
            case LARGE:
                upsizeFee = 20.0;
                break;
        }
    }

    public void setIceLevel(String iceLevel) {
        this.iceLevel = iceLevel;
    }

    @Override
    public double calculateTotalPrice() {
        return getBasePrice() + upsizeFee;
    }

    @Override
    public String getItemDetails() {
        return getName() + " (" + drinkSize + ", " + iceLevel + ")";
    }

    public Size getDrinkSize() {
        return drinkSize;
    }

    public String getIceLevel() {
        return iceLevel;
    }
}
