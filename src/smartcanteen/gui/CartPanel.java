package smartcanteen.gui;

import smartcanteen.model.OrderItem;
import smartcanteen.model.Store;
import smartcanteen.model.SubOrder;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

/**
 * Dark-themed sidebar panel displaying cart contents grouped by store,
 * running grand total, and checkout/clear controls.
 */
public class CartPanel extends JPanel {
    private final MainFrame parent;
    private final JPanel cartContentPanel;
    private final JLabel totalLabel;
    private final JButton checkoutBtn;
    private final JButton clearBtn;

    /**
     * Constructs the sidebar panel with dark UI styling and action listeners.
     *
     * @param parent MainFrame controller reference.
     */
    public CartPanel(MainFrame parent) {
        this.parent = parent;

        setPreferredSize(new Dimension(320, 0));
        setLayout(new BorderLayout());
        setBackground(new Color(18, 20, 26)); // Main dark panel background

        // Outer Panel Border
        setBorder(new CompoundBorder(
                new LineBorder(new Color(45, 50, 65), 1),
                new EmptyBorder(16, 16, 16, 16)
        ));

        // ==========================================
        // 1. SIDEBAR HEADER
        // Top section title display
        // ==========================================
        JLabel headerLabel = new JLabel("Your Cart");
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        headerLabel.setForeground(Color.WHITE); // Primary white heading
        headerLabel.setBorder(new EmptyBorder(0, 0, 12, 0));
        add(headerLabel, BorderLayout.NORTH);

        // ==========================================
        // 2. SCROLLABLE CART ITEMS CONTAINER
        // Dynamic area for grouped store sub-orders
        // ==========================================
        cartContentPanel = new JPanel();
        cartContentPanel.setLayout(new BoxLayout(cartContentPanel, BoxLayout.Y_AXIS));
        cartContentPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(cartContentPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        add(scrollPane, BorderLayout.CENTER);

        // ==========================================
        // 3. BOTTOM SUMMARY & ACTIONS FOOTER
        // Displays total price and Checkout/Clear buttons
        // ==========================================
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(45, 50, 65)), // Top divider line
                new EmptyBorder(12, 0, 0, 0)
        ));

        // Total Summary Row
        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setOpaque(false);

        JLabel totalText = new JLabel("Total:");
        totalText.setFont(new Font("SansSerif", Font.BOLD, 16));
        totalText.setForeground(Color.WHITE);

        totalLabel = new JLabel("\u20B10.00");
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        totalLabel.setForeground(new Color(74, 222, 128)); // Emerald green grand total

        totalRow.add(totalText, BorderLayout.WEST);
        totalRow.add(totalLabel, BorderLayout.EAST);

        bottomPanel.add(totalRow);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        // Button Controls
        clearBtn = new JButton("Clear Cart");
        clearBtn.setUI(new BasicButtonUI()); // Overrides OS native skin
        clearBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        clearBtn.setFocusPainted(false);
        clearBtn.setOpaque(true);
        clearBtn.setBackground(new Color(30, 34, 45));
        clearBtn.setForeground(new Color(156, 163, 175)); // Muted grey text
        clearBtn.setBorder(new CompoundBorder(
                new LineBorder(new Color(45, 50, 65), 1),
                new EmptyBorder(8, 0, 8, 0)
        ));
        clearBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        clearBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (clearBtn.isEnabled()) {
                    clearBtn.setBackground(new Color(45, 50, 65));
                    clearBtn.setForeground(new Color(248, 113, 113)); // Soft red highlight
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (clearBtn.isEnabled()) {
                    clearBtn.setBackground(new Color(30, 34, 45));
                    clearBtn.setForeground(new Color(156, 163, 175));
                }
            }
        });
        clearBtn.addActionListener(e -> parent.clearCart());

        checkoutBtn = new JButton("Checkout");
        checkoutBtn.setUI(new BasicButtonUI());
        checkoutBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        checkoutBtn.setFocusPainted(false);
        checkoutBtn.setOpaque(true);
        checkoutBtn.setBackground(new Color(22, 101, 52)); // Solid Forest Green
        checkoutBtn.setForeground(Color.WHITE);             // White Text
        checkoutBtn.setBorder(new CompoundBorder(
                new LineBorder(new Color(34, 197, 94), 1), // Green accent border
                new EmptyBorder(8, 0, 8, 0)
        ));
        checkoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        checkoutBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (checkoutBtn.isEnabled()) checkoutBtn.setBackground(new Color(30, 125, 65));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (checkoutBtn.isEnabled()) checkoutBtn.setBackground(new Color(22, 101, 52));
            }
        });
        checkoutBtn.addActionListener(e -> onCheckout());

        JPanel btnRow = new JPanel(new GridLayout(1, 2, 8, 0));
        btnRow.setOpaque(false);
        btnRow.add(clearBtn);
        btnRow.add(checkoutBtn);

        bottomPanel.add(btnRow);
        add(bottomPanel, BorderLayout.SOUTH);

        refresh();
    }

    /**
     * Rebuilds the cart contents dynamically based on active cart state.
     */
    public void refresh() {
        cartContentPanel.removeAll();
        double total = 0.0;
        Map<Store, SubOrder> cart = parent.getCart();

        boolean isEmpty = cart.isEmpty() || cart.values().stream().allMatch(so -> so.getItemsList().isEmpty());

        if (isEmpty) {
            showEmptyState();
            totalLabel.setText("\u20B10.00");
            checkoutBtn.setEnabled(false);
            clearBtn.setEnabled(false);
        } else {
            checkoutBtn.setEnabled(true);
            clearBtn.setEnabled(true);

            for (SubOrder so : cart.values()) {
                if (so.getItemsList().isEmpty()) continue;

                // Dark Store Group Card
                JPanel storeCard = new JPanel();
                storeCard.setLayout(new BoxLayout(storeCard, BoxLayout.Y_AXIS));
                storeCard.setBackground(new Color(30, 34, 45)); // Card container surface
                storeCard.setBorder(new CompoundBorder(
                        new LineBorder(new Color(45, 50, 65), 1, true),
                        new EmptyBorder(10, 12, 10, 12)
                ));

                // Store Title Label
                JLabel storeName = new JLabel(so.getStore().getStoreName());
                storeName.setFont(new Font("SansSerif", Font.BOLD, 13));
                storeName.setForeground(Color.WHITE);
                storeCard.add(storeName);
                storeCard.add(Box.createRigidArea(new Dimension(0, 8)));

                // Items list inside store group
                for (OrderItem oi : so.getItemsList()) {
                    storeCard.add(createItemRow(oi));
                    storeCard.add(Box.createRigidArea(new Dimension(0, 4)));
                }

                // Store Subtotal Footer Row
                JPanel subtotalRow = new JPanel(new BorderLayout());
                subtotalRow.setOpaque(false);
                subtotalRow.setBorder(new CompoundBorder(
                        BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(45, 50, 65)),
                        new EmptyBorder(6, 0, 0, 0)
                ));

                JLabel subtotalText = new JLabel("Subtotal:");
                subtotalText.setFont(new Font("SansSerif", Font.ITALIC, 11));
                subtotalText.setForeground(new Color(156, 163, 175));

                JLabel subtotalVal = new JLabel(String.format("\u20B1%.2f", so.getSubTotal()));
                subtotalVal.setFont(new Font("SansSerif", Font.BOLD, 11));
                subtotalVal.setForeground(new Color(74, 222, 128)); // Bright green subtotal

                subtotalRow.add(subtotalText, BorderLayout.WEST);
                subtotalRow.add(subtotalVal, BorderLayout.EAST);

                storeCard.add(subtotalRow);

                cartContentPanel.add(storeCard);
                cartContentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

                total += so.getSubTotal();
            }

            totalLabel.setText(String.format("\u20B1%.2f", total));
        }

        cartContentPanel.revalidate();
        cartContentPanel.repaint();
    }

    /**
     * Builds individual item entries inside a store card.
     */
    private JPanel createItemRow(OrderItem oi) {
        JPanel row = new JPanel(new BorderLayout(6, 0));
        row.setOpaque(false);

        JLabel nameAndQty = new JLabel(String.format("%s (\u00D7%d)", oi.getMenuItem().getItemDetails(), oi.getQuantity()));
        nameAndQty.setFont(new Font("SansSerif", Font.PLAIN, 12));
        nameAndQty.setForeground(new Color(209, 213, 219)); // Secondary text shade

        JLabel price = new JLabel(String.format("\u20B1%.2f", oi.getLineTotal()));
        price.setFont(new Font("SansSerif", Font.BOLD, 12));
        price.setForeground(Color.WHITE);

        row.add(nameAndQty, BorderLayout.WEST);
        row.add(price, BorderLayout.EAST);
        return row;
    }

    /**
     * Renders centered empty cart status when no items exist.
     */
    private void showEmptyState() {
        JPanel emptyPanel = new JPanel();
        emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
        emptyPanel.setOpaque(false);
        emptyPanel.setBorder(new EmptyBorder(40, 10, 10, 10));

        JLabel emptyMsg1 = new JLabel("Your cart is empty", SwingConstants.CENTER);
        emptyMsg1.setAlignmentX(Component.CENTER_ALIGNMENT);
        emptyMsg1.setFont(new Font("SansSerif", Font.BOLD, 14));
        emptyMsg1.setForeground(new Color(156, 163, 175));

        JLabel emptyMsg2 = new JLabel("Add items from any store to begin.", SwingConstants.CENTER);
        emptyMsg2.setAlignmentX(Component.CENTER_ALIGNMENT);
        emptyMsg2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        emptyMsg2.setForeground(new Color(107, 114, 128));

        emptyPanel.add(emptyMsg1);
        emptyPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        emptyPanel.add(emptyMsg2);

        cartContentPanel.add(emptyPanel);
    }

    /**
     * Handles checkout button action with empty validation.
     */
    private void onCheckout() {
        if (parent.getCart().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty.",
                    "SmartCanteen", JOptionPane.WARNING_MESSAGE);
            return;
        }
        CheckoutDialog dialog = new CheckoutDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), parent);
        dialog.setVisible(true);
    }
}