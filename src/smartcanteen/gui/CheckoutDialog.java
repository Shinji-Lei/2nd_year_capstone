package smartcanteen.gui;

import smartcanteen.model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Lets the customer choose, per store, between Exact Cash and a Big Bill
 * (e.g. \u20B1500 / \u20B11000), validates the tendered amount, then runs
 * the PaymentProcessor and shows the resulting receipt.
 */
public class CheckoutDialog extends JDialog {
    private MainFrame parent;
    private Map<Store, JRadioButton> exactBtns = new LinkedHashMap<>();
    private Map<Store, JRadioButton> bigBillBtns = new LinkedHashMap<>();
    private Map<Store, JTextField> amountFields = new LinkedHashMap<>();
    private Map<Store, JLabel> enteredLabels = new LinkedHashMap<>();

    public CheckoutDialog(Frame owner, MainFrame parent) {
        super(owner, "Checkout \u2014 Per-Store Payment", true);
        this.parent = parent;
        setSize(540, 560);
        setLocationRelativeTo(owner);

        // Root Dialog Container with Dark Background
        JPanel rootPanel = new JPanel(new BorderLayout(0, 10));
        rootPanel.setBackground(MainFrame.BG_DARK);
        rootPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Scrollable Stores Container
        JPanel storesPanel = new JPanel();
        storesPanel.setLayout(new BoxLayout(storesPanel, BoxLayout.Y_AXIS));
        storesPanel.setBackground(MainFrame.BG_DARK);

        for (Map.Entry<Store, SubOrder> entry : parent.getCart().entrySet()) {
            storesPanel.add(buildStoreRow(entry.getKey(), entry.getValue()));
            storesPanel.add(Box.createVerticalStrut(12));
        }

        JScrollPane scrollPane = new JScrollPane(storesPanel);
        scrollPane.getViewport().setBackground(MainFrame.BG_DARK);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        rootPanel.add(scrollPane, BorderLayout.CENTER);

        // Confirm Payment Action Button
        JButton confirmBtn = createCustomButton(
                "Confirm Payment",
                MainFrame.ACCENT_BLUE,
                MainFrame.ACCENT_BLUE.brighter(),
                Color.WHITE,
                new Dimension(180, 40),
                14
        );
        confirmBtn.addActionListener(e -> onConfirm());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.setOpaque(false);
        bottom.add(confirmBtn);
        rootPanel.add(bottom, BorderLayout.SOUTH);

        setContentPane(rootPanel);
    }

    private JPanel buildStoreRow(Store store, SubOrder subOrder) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setBackground(MainFrame.PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MainFrame.BORDER_COLOR, 1),
                new EmptyBorder(12, 14, 12, 14)
        ));

        // Store Header Title
        JLabel storeHeader = new JLabel(store.getStoreName() +
                String.format("   \u2014   Subtotal: \u20B1%.2f", subOrder.getSubTotal()));
        storeHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        storeHeader.setForeground(MainFrame.TEXT_LIGHT);
        storeHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(storeHeader);
        panel.add(Box.createVerticalStrut(8));

        // Radio Button Payment Options
        ButtonGroup group = new ButtonGroup();
        JRadioButton exact = createRadioButton("Exact Cash", true);
        JRadioButton big = createRadioButton("Big Bill", false);
        group.add(exact);
        group.add(big);
        exactBtns.put(store, exact);
        bigBillBtns.put(store, big);

        // Bill Amount Text Input
        JTextField amountField = new JTextField();
        amountField.setFont(MainFrame.BODY_FONT);
        amountField.setBackground(MainFrame.BG_DARK);
        amountField.setForeground(MainFrame.TEXT_LIGHT);
        amountField.setCaretColor(MainFrame.TEXT_LIGHT);
        amountField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MainFrame.BORDER_COLOR, 1),
                new EmptyBorder(4, 6, 4, 6)
        ));
        amountField.setEnabled(false);
        amountField.setMaximumSize(new Dimension(140, 28));
        amountField.setPreferredSize(new Dimension(140, 28));
        amountField.setToolTipText("Type any amount, or list several bills separated by spaces (e.g. 100 100 100)");
        amountFields.put(store, amountField);

        JLabel enteredLabel = new JLabel("Entered: \u20B10.00");
        enteredLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        enteredLabel.setForeground(MainFrame.TEXT_MUTED);
        enteredLabels.put(store, enteredLabel);

        amountField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void refresh() {
                enteredLabel.setText(String.format("Entered: \u20B1%.2f", parseBillTotal(amountField.getText())));
            }
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { refresh(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { refresh(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { refresh(); }
        });

        // Quick Addition Buttons
        JPanel bigBillOptions = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        bigBillOptions.setOpaque(false);

        JButton btn100 = createSmallButton("+100");
        JButton btn500 = createSmallButton("+500");
        JButton btn1000 = createSmallButton("+1000");
        JButton clearBtn = createSmallButton("Clear");

        btn100.addActionListener(e -> appendAmount(amountField, "100"));
        btn500.addActionListener(e -> appendAmount(amountField, "500"));
        btn1000.addActionListener(e -> appendAmount(amountField, "1000"));
        clearBtn.addActionListener(e -> amountField.setText(""));

        bigBillOptions.add(amountField);
        bigBillOptions.add(btn100);
        bigBillOptions.add(btn500);
        bigBillOptions.add(btn1000);
        bigBillOptions.add(clearBtn);

        JPanel bigBillRow = new JPanel();
        bigBillRow.setLayout(new BoxLayout(bigBillRow, BoxLayout.Y_AXIS));
        bigBillRow.setOpaque(false);
        bigBillRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        bigBillOptions.setAlignmentX(Component.LEFT_ALIGNMENT);
        enteredLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        bigBillRow.add(bigBillOptions);
        bigBillRow.add(Box.createVerticalStrut(4));
        bigBillRow.add(enteredLabel);

        exact.addActionListener(e -> amountField.setEnabled(false));
        big.addActionListener(e -> amountField.setEnabled(true));

        panel.add(exact);
        panel.add(Box.createVerticalStrut(4));
        panel.add(big);
        panel.add(Box.createVerticalStrut(6));
        panel.add(bigBillRow);

        return panel;
    }

    private JRadioButton createRadioButton(String text, boolean selected) {
        JRadioButton radio = new JRadioButton(text, selected);
        radio.setFont(MainFrame.BODY_FONT);
        radio.setForeground(MainFrame.TEXT_LIGHT);
        radio.setBackground(MainFrame.PANEL_BG);
        radio.setFocusPainted(false);
        radio.setAlignmentX(Component.LEFT_ALIGNMENT);
        return radio;
    }

    private JButton createSmallButton(String text) {
        return createCustomButton(
                text,
                MainFrame.BG_DARK,
                MainFrame.BORDER_COLOR,
                MainFrame.TEXT_LIGHT,
                new Dimension(62, 28),
                11
        );
    }

    /** Generic custom button painter bypassing native OS look and feel */
    private JButton createCustomButton(String text, Color baseColor, Color hoverColor, Color textColor, Dimension size, int fontSize) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (!isEnabled()) {
                    g2.setColor(new Color(45, 50, 60));
                } else if (getModel().isPressed()) {
                    g2.setColor(baseColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(hoverColor);
                } else {
                    g2.setColor(baseColor);
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);

                // Draw Border for Dark Buttons
                if (baseColor.equals(MainFrame.BG_DARK) && isEnabled()) {
                    g2.setColor(MainFrame.BORDER_COLOR);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                }

                g2.setFont(getFont());
                g2.setColor(isEnabled() ? textColor : MainFrame.TEXT_MUTED);

                FontMetrics fm = g2.getFontMetrics();
                int stringWidth = fm.stringWidth(getText());
                int stringHeight = fm.getAscent();
                g2.drawString(getText(), (getWidth() - stringWidth) / 2, (getHeight() + stringHeight) / 2 - 2);

                g2.dispose();
            }
        };

        button.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(size);

        return button;
    }

    /** Appends a bill amount to whatever's already typed, space-separated. */
    private void appendAmount(JTextField field, String value) {
        String current = field.getText().trim();
        field.setText(current.isEmpty() ? value : current + " " + value);
    }

    /**
     * Parses a field that may contain a single amount or several bills
     * separated by spaces, commas, or plus signs (e.g. "100 100 100",
     * "100,100,100", "100+100+100") and returns their sum. Invalid or
     * empty tokens are ignored so partial typing doesn't throw mid-entry.
     */
    private double parseBillTotal(String text) {
        double total = 0;
        for (String token : text.trim().split("[\\s,+]+")) {
            if (token.isEmpty()) continue;
            try {
                total += Double.parseDouble(token);
            } catch (NumberFormatException ignored) {
                // skip incomplete/invalid tokens while the user is still typing
            }
        }
        return total;
    }

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

        MasterReceipt receipt = new MasterReceipt(
                "R-" + (System.currentTimeMillis() % 100000),
                parent.getCustomer().getName(),
                processor);

        Frame owner = (Frame) getOwner();
        dispose();
        parent.clearCart();

        new ReceiptDialog(owner, receipt, processor).setVisible(true);
    }
}