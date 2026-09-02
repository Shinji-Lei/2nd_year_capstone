package smartcanteen.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The portion of a customer's order that belongs to a single store.
 */
public class SubOrder {
    public enum Status { PENDING, PAID, PREPARING, READY, COMPLETED }
    public enum PaymentType { EXACT_CASH, BIG_BILL }

    private static int queueCounter = 100;

    private String subOrderId;
    private Store store;
    private List<OrderItem> itemsList;
    private double subTotal;
    private double cashTendered;
    private double changeToReturn;
    private Status status;
    private PaymentType paymentType;
    private int queueNumber;

    public SubOrder(String subOrderId, Store store) {
        this.subOrderId = subOrderId;
        this.store = store;
        this.itemsList = new ArrayList<>();
        this.status = Status.PENDING;
        this.paymentType = PaymentType.EXACT_CASH;
        this.queueNumber = ++queueCounter;
    }

    public void addItem(OrderItem item) {
        itemsList.add(item);
        recalculateSubTotal();
    }

    private void recalculateSubTotal() {
        subTotal = 0;
        for (OrderItem oi : itemsList) {
            subTotal += oi.getLineTotal();
        }
    }

    public double calculateStoreChange() {
        changeToReturn = cashTendered - subTotal;
        return changeToReturn;
    }

    public void updateStatus(Status status) {
        this.status = status;
    }

    public String getSubOrderId() {
        return subOrderId;
    }

    public Store getStore() {
        return store;
    }

    public List<OrderItem> getItemsList() {
        return itemsList;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public double getCashTendered() {
        return cashTendered;
    }

    public void setCashTendered(double cashTendered) {
        this.cashTendered = cashTendered;
    }

    public double getChangeToReturn() {
        return changeToReturn;
    }

    public Status getStatus() {
        return status;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public int getQueueNumber() {
        return queueNumber;
    }
}
