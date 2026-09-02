package smartcanteen.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single canteen stall.
 */
public class Store {
    private String storeId;
    private String storeName;
    private String location;
    private boolean isAvailable;
    private List<MenuItem> menu;

    public Store(String storeId, String storeName, String location) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.location = location;
        this.isAvailable = true;
        this.menu = new ArrayList<>();
    }

    public List<MenuItem> getMenu() {
        return menu;
    }

    public void addMenuItem(MenuItem item) {
        menu.add(item);
    }

    public void updateStatus(boolean available) {
        this.isAvailable = available;
    }

    public String getStoreId() {
        return storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getLocation() {
        return location;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    @Override
    public String toString() {
        return storeName;
    }
}
