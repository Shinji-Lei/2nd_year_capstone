package smartcanteen.model;

/**
 * The person placing the order.
 */
public class Customer {
    private String customerId;
    private String fullName;
    private String contactNumber;

    public Customer(String customerId, String fullName, String contactNumber) {
        this.customerId = customerId;
        this.fullName = fullName;
        this.contactNumber = contactNumber;
    }

    public String getName() {
        return fullName;
    }

    public void setName(String fullName) {
        this.fullName = fullName;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    /**
     * Delegates the actual checkout work to the PaymentProcessor
     * (Abstraction: the customer doesn't need to know the details).
     */
    public void placeOrder(PaymentProcessor processor) {
        processor.processCheckout();
    }
}
