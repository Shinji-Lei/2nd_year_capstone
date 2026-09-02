package smartcanteen.model;

import java.util.List;

/**
 * The consolidated receipt for an entire multi-store checkout.
 */
public class MasterReceipt {
    private String receiptId;
    private String customerName;
    private List<SubOrder> subOrdersList;
    private double grandTotal;

    public MasterReceipt(String receiptId, String customerName, PaymentProcessor processor) {
        this.receiptId = receiptId;
        this.customerName = customerName;
        this.subOrdersList = processor.getSubOrdersList();
        this.grandTotal = processor.getGrandTotal();
    }

    public String generateSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("        SMARTCANTEEN MASTER RECEIPT\n");
        sb.append("========================================\n");
        sb.append("Receipt No: ").append(receiptId).append("\n");
        sb.append("Customer  : ").append(customerName).append("\n");
        sb.append("----------------------------------------\n");

        for (SubOrder so : subOrdersList) {
            sb.append(String.format("Store: %s  (Queue #%d)\n",
                    so.getStore().getStoreName(), so.getQueueNumber()));
            for (OrderItem oi : so.getItemsList()) {
                sb.append(String.format("  %-25s x%d  \u20B1%.2f\n",
                        oi.getMenuItem().getItemDetails(), oi.getQuantity(), oi.getLineTotal()));
            }
            sb.append(String.format("  Subtotal : \u20B1%.2f\n", so.getSubTotal()));
            sb.append(String.format("  Cash Paid: \u20B1%.2f   Change: \u20B1%.2f\n",
                    so.getCashTendered(), so.getChangeToReturn()));
            sb.append("----------------------------------------\n");
        }

        sb.append(String.format("GRAND TOTAL: \u20B1%.2f\n", grandTotal));
        sb.append("========================================\n");
        sb.append("        Thank you for ordering!\n");
        return sb.toString();
    }

    public void printReceipt() {
        System.out.println(generateSummary());
    }

    public String getReceiptId() {
        return receiptId;
    }
}
