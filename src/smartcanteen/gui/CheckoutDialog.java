package smartcanteen.gui;

import smartcanteen.model.*;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Dark-themed modal dialog allowing customers to select per-store payment methods
 * (Exact Cash vs. Big Bill), enter custom cash amounts, and trigger payment processing.
 */
public class CheckoutDialog extends JDialog {
    private final MainFrame parent;
    private final Map<Store, JRadioButton> exactBtns = new LinkedHashMap<>();
    private final Map<Store, JRadioButton> bigBillBtns = new LinkedHashMap<>();
    private final Map<Store, JTextField> amountFields = new LinkedHashMap<>();
    private final Map<Store, JLabel> enteredLabels = new LinkedHashMap<>();

    /**
     * Constructs the dark-themed checkout modal window.
     *
     * @param owner  Parent window frame.
     * @param parent MainFrame controller reference.
     */
    public CheckoutDialog(Frame owner, MainFrame parent) {
        super(owner, "Checkout \u2014 Per-Store Payment", true);
        this.parent = parent;

        setSize(540, 620);
        setMinimumSize(new Dimension(480, 500));
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(18, 20, 26)); // Main dark background

        // ==========================================
        // 1. DIALOG HEADER PANEL
        // Top dark header displaying title and subtitle
        // ==========================================
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(new Color(30, 34, 45)); // Header container fill
        headerPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(45, 50, 65), 1),
                new EmptyBorder(16, 20, 16, 20)
        ));

        JLabel headerTitle = new JLabel("Payment Method");
        headerTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        headerTitle.setForeground(Color.WHITE); // Primary white title

        JLabel headerSubtitle = new JLabel("Select cash options per store");
        headerSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        headerSubtitle.setForeground(new Color(156, 163, 175)); // Muted subtitle

        headerPanel.add(headerTitle);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        headerPanel.add(headerSubtitle);
        add(headerPanel, BorderLayout.NORTH);

        // ==========================================
        // 2. SCROLLABLE STORE LIST CONTAINER
        // Builds a payment options card for each cart store
        // ==========================================
        JPanel storesPanel = new JPanel();
        storesPanel.setLayout(new BoxLayout(storesPanel, BoxLayout.Y_AXIS));
        storesPanel.setOpaque(false);
        storesPanel.setBorder(new EmptyBorder(16, 20, 16, 20));

        for (Map.Entry<Store, SubOrder> entry : parent.getCart().entrySet()) {
            storesPanel.add(buildStoreRow(entry.getKey(), entry.getValue()));
            storesPanel.add(Box.createRigidArea(new Dimension(0, 14))); // Gap between store cards
        }

        JScrollPane scrollPane = new JScrollPane(storesPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        add(scrollPane, BorderLayout.CENTER);

        // ==========================================
        // 3. BOTTOM ACTION BAR
        // Holds the primary "Confirm Payment" button
        // ==========================================
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setBackground(new Color(30, 34, 45)); // Dark action bar card surface
        bottomBar.setBorder(new CompoundBorder(
                new LineBorder(new Color(45, 50, 65), 1),
                new EmptyBorder(12, 20, 12, 20)
        ));

        // Green "Confirm Payment" action button
        JButton confirmBtn = new JButton("Confirm Payment");
        confirmBtn.setUI(new BasicButtonUI()); // Prevents OS look-and-feel color overrides
        confirmBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        confirmBtn.setFocusPainted(false);
        confirmBtn.setOpaque(true);
        confirmBtn.setBackground(new Color(22, 101, 52)); // Dark forest green
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setBorder(new CompoundBorder(
                new LineBorder(new Color(34, 197, 94), 1), // Green accent border
                new EmptyBorder(10, 0, 10, 0)
        ));
        confirmBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Button hover transitions
        confirmBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (confirmBtn.isEnabled()) confirmBtn.setBackground(new Color(30, 125, 65));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (confirmBtn.isEnabled()) confirmBtn.setBackground(new Color(22, 101, 52));
            }
        });

        confirmBtn.addActionListener(e -> onConfirm());

        bottomBar.add(confirmBtn, BorderLayout.CENTER);
        add(bottomBar, BorderLayout.SOUTH);
    }

    /**
     * Builds a dark-styled payment selection card for an individual store sub-order.
     *
     * @param store    The target store.
     * @param subOrder Associated store sub-order.
     * @return Formatted card panel.
     */
    private JPanel buildStoreRow(Store store, SubOrder subOrder) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(new Color(30, 34, 45)); // Dark card background
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(45, 50, 65), 1, true),
                new EmptyBorder(14, 16, 14, 16)
        ));

        // Store Header inside card
        JPanel cardHeader = new JPanel(new BorderLayout());
        cardHeader.setOpaque(false);

        JLabel storeName = new JLabel(store.getStoreName());
        storeName.setFont(new Font("SansSerif", Font.BOLD, 14));
        storeName.setForeground(Color.WHITE); // Primary white label

        JLabel subtotalBadge = new JLabel(String.format("Subtotal: \u20B1%.2f", subOrder.getSubTotal()));
        subtotalBadge.setFont(new Font("SansSerif", Font.BOLD, 14));
        subtotalBadge.setForeground(new Color(74, 222, 128)); // Bright green accent text

        cardHeader.add(storeName, BorderLayout.WEST);
        cardHeader.add(subtotalBadge, BorderLayout.EAST);
        card.add(cardHeader, BorderLayout.NORTH);

        // Payment Options Area
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setOpaque(false);

        ButtonGroup group = new ButtonGroup();
        JRadioButton exact = new JRadioButton("Exact Cash", true);
        JRadioButton big = new JRadioButton("Big Bill / Custom Amount");
        exact.setFont(new Font("SansSerif", Font.PLAIN, 13));
        big.setFont(new Font("SansSerif", Font.PLAIN, 13));
        exact.setForeground(Color.WHITE); // White text for radio controls
        big.setForeground(Color.WHITE);
        exact.setOpaque(false);
        big.setOpaque(false);

        group.add(exact);
        group.add(big);
        exactBtns.put(store, exact);
        bigBillBtns.put(store, big);

        // Dark Text Input Field for custom cash amounts
        JTextField amountField = new JTextField();
        amountField.setFont(new Font("SansSerif", Font.BOLD, 12));
        amountField.setEnabled(false);
        amountField.setPreferredSize(new Dimension(100, 30));
        amountField.setBackground(new Color(20, 24, 33)); // Dark inset field background
        amountField.setForeground(Color.WHITE);            // White text input
        amountField.setCaretColor(Color.WHITE);
        amountField.setBorder(new CompoundBorder(
                new LineBorder(new Color(60, 66, 82), 1),
                new EmptyBorder(4, 8, 4, 8)
        ));
        amountField.setToolTipText("Type any amount or click fast bill buttons");
        amountFields.put(store, amountField);

        JLabel enteredLabel = new JLabel("Entered: \u20B10.00");
        enteredLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        enteredLabel.setForeground(new Color(156, 163, 175)); // Muted status text
        enteredLabels.put(store, enteredLabel);

        amountField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void refresh() {
                enteredLabel.setText(String.format("Entered: \u20B1%.2f", parseBillTotal(amountField.getText())));
            }
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { refresh(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { refresh(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { sync(); }
            private void sync() { refresh(); }
        });

        // Fast Bills & Input Controls Row
        JPanel bigBillControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        bigBillControls.setOpaque(false);

        JButton btn100 = createQuickBillButton("+100", amountField);
        JButton btn500 = createQuickBillButton("+500", amountField);
        JButton btn1000 = createQuickBillButton("+1000", amountField);

        // Clear Cash Input Button
        JButton clearBtn = new JButton("Clear");
        clearBtn.setUI(new BasicButtonUI());
        clearBtn.setFont(new Font("SansSerif", Font.BOLD, 11));
        clearBtn.setFocusPainted(false);
        clearBtn.setOpaque(true);
        clearBtn.setBackground(new Color(45, 50, 65));
        clearBtn.setForeground(new Color(248, 113, 113)); // Crimson accent text
        clearBtn.setBorder(new CompoundBorder(
                new LineBorder(new Color(60, 66, 82), 1),
                new EmptyBorder(4, 10, 4, 10)
        ));
        clearBtn.setEnabled(false);
        clearBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearBtn.addActionListener(e -> amountField.setText(""));

        bigBillControls.add(amountField);
        bigBillControls.add(btn100);
        bigBillControls.add(btn500);
        bigBillControls.add(btn1000);
        bigBillControls.add(clearBtn);

        // Toggle state handlers
        exact.addActionListener(e -> {
            amountField.setEnabled(false);
            btn100.setEnabled(false);
            btn500.setEnabled(false);
            btn1000.setEnabled(false);
            clearBtn.setEnabled(false);
        });

        big.addActionListener(e -> {
            amountField.setEnabled(true);
            btn100.setEnabled(true);
            btn500.setEnabled(true);
            btn1000.setEnabled(true);
            clearBtn.setEnabled(true);
        });

        // Set initial button states
        btn100.setEnabled(false);
        btn500.setEnabled(false);
        btn1000.setEnabled(false);

        optionsPanel.add(exact);
        optionsPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        optionsPanel.add(big);
        optionsPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        optionsPanel.add(bigBillControls);

        JPanel statusRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        statusRow.setOpaque(false);
        statusRow.add(enteredLabel);
        optionsPanel.add(statusRow);

        card.add(optionsPanel, BorderLayout.CENTER);
        return card;
    }

    /**
     * Creates a dark quick-add bill button (+100, +500, +1000).
     *
     * @param text        Button display text.
     * @param targetField Target JTextField to append values into.
     * @return Styled JButton.
     */
    private JButton createQuickBillButton(String text, JTextField targetField) {
        JButton btn = new JButton(text);
        btn.setUI(new BasicButtonUI()); // Overrides OS button skins
        btn.setFont(new Font("SansSerif", Font.BOLD, 11));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBackground(new Color(45, 50, 65));
        btn.setForeground(Color.WHITE);
        btn.setBorder(new CompoundBorder(
                new LineBorder(new Color(60, 66, 82), 1),
                new EmptyBorder(4, 8, 4, 8)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(new Color(60, 66, 82));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(new Color(45, 50, 65));
            }
        });

        btn.addActionListener(e -> appendAmount(targetField, text.replace("+", "")));
        return btn;
    }

    /** Appends a bill amount to whatever's already typed, space-separated. */
    private void appendAmount(JTextField field, String value) {
        String current = field.getText().trim();
        field.setText(current.isEmpty() ? value : current + " " + value);
    }

    /**
     * Parses a field that may contain a single amount or several bills
     * separated by spaces, commas, or plus signs and returns their sum.
     */
    private double parseBillTotal(String text) {
        double total = 0;
        for (String token : text.trim().split("[\\s,+]+")) {
            if (token.isEmpty()) continue;
            try {
                total += Double.parseDouble(token);
            } catch (NumberFormatException ignored) {
                // skip incomplete/invalid tokens while typing
            }
        }
        return total;
    }

    /**
     * Validates payment inputs, constructs the PaymentProcessor, registers sub-orders,
     * disposes this dialog, and launches the receipt view.
     */
    private void onConfirm() {
        PaymentProcessor processor = new PaymentProcessor();

        for (Map.Entry<Store, SubOrder> entry : parent.getCart().entrySet()) {
            Store store = entry.getKey();
            SubOrder subOrder = entry.getValue();

            if (bigBillBtns.get(store).isSelected()) {
                subOrder.setPaymentType(SubOrder.PaymentType.BIG_BILL);
                String amtText = amountFields.get(store).getText().trim();
                if (amtText.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Enter a bill amount for " + store.getStoreName(),
                            "Missing Amount", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                double amt = parseBillTotal(amtText);
                if (amt <= 0) {
                    JOptionPane.showMessageDialog(this,
                            "Enter a valid bill amount for " + store.getStoreName()
                                    + " (e.g. 300 or 100 100 100)",
                            "Invalid Amount", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (amt < subOrder.getSubTotal()) {
                    JOptionPane.showMessageDialog(this,
                            "Bill amount is less than the subtotal for " + store.getStoreName(),
                            "Insufficient Amount", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                subOrder.setCashTendered(amt);
            } else {
                subOrder.setPaymentType(SubOrder.PaymentType.EXACT_CASH);
                subOrder.setCashTendered(subOrder.getSubTotal());
            }
            processor.addSubOrder(subOrder);
        }

        if (!processor.validateStorePayments()) {
            JOptionPane.showMessageDialog(this, "Payment validation failed.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        processor.processCheckout();

        // Push each paid sub-order onto the shared board
        for (SubOrder so : processor.getSubOrdersList()) {
            parent.getOrderBoard().submit(so);
        }

        MasterReceipt receipt = new MasterReceipt(
                "R-" + (System.currentTimeMillis() % 100000),
                parent.getCustomer().getName(),
                processor);

        Frame owner = (Frame) getOwner();
        dispose();
        parent.clearCart();

        new ReceiptDialog(owner, receipt, processor, parent.getOrderBoard()).setVisible(true);
    }
}