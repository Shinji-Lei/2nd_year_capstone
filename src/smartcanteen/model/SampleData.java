package smartcanteen.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds sample stores and menus so the app is usable out of the box.
 * Replace with a database or file-backed loader for a real deployment.
 */
public class SampleData {
    public static List<Store> buildStores() {
        List<Store> stores = new ArrayList<>();

        Store s1 = new Store("S1", "TUDTUD STORE", "Stall 1");
        FoodItem rice = new FoodItem("F101", "Steamed Rice", 15.0, s1, false);
        FoodItem adobo = new FoodItem("F102", "Chicken Adobo", 65.0, s1, false);
        FoodItem sisig = new FoodItem("F103", "Sizzling Sisig", 75.0, s1, true);
        s1.addMenuItem(rice);
        s1.addMenuItem(adobo);
        s1.addMenuItem(sisig);

        Store s2 = new Store("S2", "SUAREZ STORE", "Stall 2");
        FoodItem burger = new FoodItem("F201", "Classic Cheeseburger", 55.0, s2, false);
        burger.addTopping("Extra Cheese", 10.0);
        FoodItem fries = new FoodItem("F202", "French Fries", 40.0, s2, false);
        ComboMeal comboMeal = new ComboMeal("C201", "Burger Combo", 0.0, s2, 15.0);
        comboMeal.addComponent(burger);
        comboMeal.addComponent(fries);
        s2.addMenuItem(burger);
        s2.addMenuItem(fries);
        s2.addMenuItem(comboMeal);

        Store s3 = new Store("S3", "SHINJI STORE", "Stall 3");
        FoodItem pancit = new FoodItem("F301", "Pancit Canton", 50.0, s3, false);
        FoodItem lomi = new FoodItem("F302", "Special Lomi", 60.0, s3, true);
        s3.addMenuItem(pancit);
        s3.addMenuItem(lomi);

        Store s4 = new Store("S4", "MARIEGINE STORE", "Stall 4");
        BeverageItem icedTea = new BeverageItem("B401", "Iced Tea", 25.0, s4);
        BeverageItem milkTea = new BeverageItem("B402", "Milk Tea", 45.0, s4);
        BeverageItem soda = new BeverageItem("B403", "Soft Drink", 20.0, s4);
        s4.addMenuItem(icedTea);
        s4.addMenuItem(milkTea);
        s4.addMenuItem(soda);

        Store s5 = new Store("S5", "PRINCESS STORE", "Stall 5");
        FoodItem taho = new FoodItem("F501", "Taho", 20.0, s5, false);
        FoodItem haloHalo = new FoodItem("F502", "Halo-Halo", 55.0, s5, false);
        s5.addMenuItem(taho);
        s5.addMenuItem(haloHalo);

        stores.add(s1);
        stores.add(s2);
        stores.add(s3);
        stores.add(s4);
        stores.add(s5);
        return stores;
    }
}
