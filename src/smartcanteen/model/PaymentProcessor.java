package smartcanteen.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles validation and processing of all sub-orders in a checkout.
 * Demonstrates Abstraction: callers just invoke processCheckout(); the
 * per-store validation, balance checks, and change math stay hidden here.
 */
public class PaymentProcessor {
    private List<SubOrder> subOrdersList;
    private double grandTotal;
    private double totalChange;
    private List<String> changeAlerts;

    public PaymentProcessor() {
        this.subOrdersList = new ArrayList<>();
        this.changeAlerts = new ArrayList<>();
    }

    public void addSubOrder(SubOrder subOrder) {
        subOrdersList.add(subOrder);
    }

    public boolean validateStorePayments() {
        for (SubOrder so : subOrdersList) {
            if (so.getCashTendered() < so.getSubTotal()) {
                return false;
            }
        }
        return true;
    }

    public void processCheckout() {
        grandTotal = 0;
        totalChange = 0;
        changeAlerts.clear();

        for (SubOrder so : subOrdersList) {
            so.calculateStoreChange();
            grandTotal += so.getSubTotal();
            totalChange += so.getChangeToReturn();
            so.updateStatus(SubOrder.Status.PAID);

            if (so.getPaymentType() == SubOrder.PaymentType.BIG_BILL) {
                changeAlerts.add(String.format(
                        "[%s] Queue #%d \u2014 prepare change: \u20B1%.2f",
                        so.getStore().getStoreName(), so.getQueueNumber(), so.getChangeToReturn()));
            }
        }
    }

    public double getGrandTotal() {
        return grandTotal;
    }

    public double getTotalChange() {
        return totalChange;
    }

    public List<SubOrder> getSubOrdersList() {
        return subOrdersList;
    }

    public List<String> getChangeAlerts() {
        return changeAlerts;
    }
}
