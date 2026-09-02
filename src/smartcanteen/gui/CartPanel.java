package smartcanteen.gui;

import smartcanteen.model.OrderItem;
import smartcanteen.model.Store;
import smartcanteen.model.SubOrder;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;

/**
 * Sidebar panel showing every store's items currently in the cart,
 * a running total, and Checkout / Clear actions.
 */
public class CartPanel extends JPanel {
    private MainFrame parent;
    private JPanel itemsContainer;
    private JLabel totalLabel;
    private JButton checkoutBtn;
    private JButton clearBtn;

    public CartPanel(MainFrame parent) {
        this.parent = parent;
        setPreferredSize(new Dimension(320, 0));
        setLayout(new BorderLayout(0, 10));
        setBackground(MainFrame.PANEL_BG);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, MainFrame.BORDER_COLOR),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Sidebar Header Title
        JLabel cartTitle = new JLabel("Your Cart");
        cartTitle.setFont(MainFrame.TITLE_FONT);
        cartTitle.setForeground(MainFrame.TEXT_LIGHT);
        add(cartTitle, BorderLayout.NORTH);

        // Dynamic Scrollable Items Container
        itemsContainer = new JPanel();
        itemsContainer.setLayout(new BoxLayout(itemsContainer, BoxLayout.Y_AXIS));
        itemsContainer.setBackground(MainFrame.PANEL_BG);

        JScrollPane scrollPane = new JScrollPane(itemsContainer);
        scrollPane.getViewport().setBackground(MainFrame.PANEL_BG);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Controls (Total Label + Action Buttons)
        JPanel bottom = new JPanel(new BorderLayout(0, 10));
        bottom.setOpaque(false);

        totalLabel = new JLabel("Total: \u20B10.00");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalLabel.setForeground(MainFrame.TEXT_LIGHT);
        bottom.add(totalLabel, BorderLayout.NORTH);

        // Button Actions Row with Custom Painter
        clearBtn = createCustomButton("Clear Cart", MainFrame.BG_DARK, MainFrame.BORDER_COLOR, MainFrame.TEXT_LIGHT);
        clearBtn.addActionListener(e -> parent.clearCart());

        checkoutBtn = createCustomButton("Checkout", MainFrame.ACCENT_BLUE, MainFrame.ACCENT_BLUE.brighter(), Color.WHITE);
        checkoutBtn.addActionListener(e -> onCheckout());

        JPanel btnRow = new JPanel(new GridLayout(1, 2, 8, 0));
        btnRow.setOpaque(false);
        btnRow.add(clearBtn);
        btnRow.add(checkoutBtn);
        bottom.add(btnRow, BorderLayout.SOUTH);

        add(bottom, BorderLayout.SOUTH);
        refresh();
    }

    public void refresh() {
        itemsContainer.removeAll();
        double total = 0;
        Map<Store, SubOrder> cart = parent.getCart();

        if (cart.isEmpty()) {
            JLabel emptyLabel = new JLabel("<html><body style='width: 180px;'>Cart is empty.<br><br>Add items from any store tab to begin.</body></html>");
            emptyLabel.setFont(MainFrame.BODY_FONT);
            emptyLabel.setForeground(MainFrame.TEXT_MUTED);
            emptyLabel.setBorder(new EmptyBorder(12, 12, 12, 12));
            itemsContainer.add(emptyLabel);
        } else {
            for (SubOrder so : cart.values()) {
                if (so.getItemsList().isEmpty()) continue;

                // Store Group Container
                JPanel storeGroup = new JPanel();
                storeGroup.setLayout(new BoxLayout(storeGroup, BoxLayout.Y_AXIS));
                storeGroup.setBackground(MainFrame.BG_DARK);
                storeGroup.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(MainFrame.BORDER_COLOR, 1),
                        new EmptyBorder(8, 10, 8, 10)
                ));
                storeGroup.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1000));

                JLabel storeHeader = new JLabel(so.getStore().getStoreName());
                storeHeader.setFont(new Font("Segoe UI", Font.BOLD, 13));
                storeHeader.setForeground(MainFrame.ACCENT_BLUE);
                storeGroup.add(storeHeader);
                storeGroup.add(Box.createVerticalStrut(6));

                // Individual Cart Item Cards
                for (OrderItem oi : so.getItemsList()) {
                    JPanel itemCard = new JPanel(new BorderLayout());
                    itemCard.setOpaque(false);
                    itemCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));

                    JLabel nameLabel = new JLabel(oi.getMenuItem().getName());
                    nameLabel.setFont(MainFrame.BODY_FONT);
                    nameLabel.setForeground(MainFrame.TEXT_LIGHT);

                    JLabel priceLabel = new JLabel(String.format("x%d  \u20B1%.2f", oi.getQuantity(), oi.getLineTotal()));
                    priceLabel.setFont(MainFrame.BODY_FONT);
                    priceLabel.setForeground(MainFrame.TEXT_MUTED);

                    itemCard.add(nameLabel, BorderLayout.WEST);
                    itemCard.add(priceLabel, BorderLayout.EAST);

                    storeGroup.add(itemCard);
                    storeGroup.add(Box.createVerticalStrut(4));
                }

                // Subtotal Display per Store
                JLabel subtotalLabel = new JLabel(String.format("Subtotal: \u20B1%.2f", so.getSubTotal()));
                subtotalLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
                subtotalLabel.setForeground(MainFrame.TEXT_MUTED);
                subtotalLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                storeGroup.add(Box.createVerticalStrut(4));
                storeGroup.add(subtotalLabel);

                itemsContainer.add(storeGroup);
                itemsContainer.add(Box.createVerticalStrut(8));

                total += so.getSubTotal();
            }
        }

        totalLabel.setText(String.format("Total: \u20B1%.2f", total));
        boolean hasItems = total > 0;
        checkoutBtn.setEnabled(hasItems);
        clearBtn.setEnabled(hasItems);

        itemsContainer.revalidate();
        itemsContainer.repaint();
    }

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

    /** Custom button renderer bypassing native OS look and feel */
    private JButton createCustomButton(String text, Color baseColor, Color hoverColor, Color textColor) {
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

                // Draw Border for Secondary Buttons
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

        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(0, 36));

        return button;
    }
}