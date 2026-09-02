package smartcanteen.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Shared board that the customer side submits paid sub-orders to, and
 * that the staff/kitchen display reads from. This is what makes
 * SmartCanteen a genuine two-way system: the customer's checkout on
 * one screen shows up live on the staff's kitchen screen.
 */
public class OrderBoard {
    private List<SubOrder> submittedOrders;
    private List<Runnable> listeners;

    public OrderBoard() {
        this.submittedOrders = new ArrayList<>();
        this.listeners = new ArrayList<>();
    }

    /** Called by the customer side once a sub-order is paid. */
    public void submit(SubOrder subOrder) {
        submittedOrders.add(subOrder);
        notifyListeners();
    }

    /** All sub-orders ever submitted for a given store, most recent last. */
    public List<SubOrder> getOrdersForStore(Store store) {
        List<SubOrder> result = new ArrayList<>();
        for (SubOrder so : submittedOrders) {
            if (so.getStore() == store) {
                result.add(so);
            }
        }
        return result;
    }

    /** Staff UIs register here to be told when something changes. */
    public void addListener(Runnable listener) {
        listeners.add(listener);
    }

    /** Call after mutating a SubOrder's status so all displays refresh. */
    public void touch() {
        notifyListeners();
    }

    private void notifyListeners() {
        for (Runnable r : listeners) {
            r.run();
        }
    }
}
