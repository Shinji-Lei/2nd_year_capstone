package smartcanteen.gui;

import smartcanteen.model.MenuItem;
import smartcanteen.model.Store;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Displays one store's menu with styled card layouts and modern design.
 */
public class StorePanel extends JPanel {

    // Color Palette matching MainFrame
    private static final Color BG_DARK = new Color(24, 28, 36);
    private static final Color CARD_BG = new Color(33, 39, 50);
    private static final Color BORDER_COLOR = new Color(50, 55, 65);
    private static final Color ACCENT_PRIMARY = new Color(13, 110, 253);
    private static final Color TEXT_LIGHT = new Color(240, 240, 240);
    private static final Color TEXT_MUTED = new Color(160, 165, 175);

    // Fonts
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font ITEM_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 12);

    public StorePanel(Store store, MainFrame parent) {
        setLayout(new BorderLayout(0, 12));
        setBackground(BG_DARK);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Store Header Title
        JLabel title = new JLabel(store.getStoreName() + "  \u2014  " + store.getLocation());
        title.setFont(TITLE_FONT);
        title.setForeground(TEXT_LIGHT);
        title.setBorder(new EmptyBorder(0, 0, 5, 0));
        add(title, BorderLayout.NORTH);

        // Container Panel for Menu Items
        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBackground(BG_DARK);

        for (MenuItem item : store.getMenu()) {
            list.add(buildItemRow(store, item, parent));
            list.add(Box.createVerticalStrut(10));
        }

        // Scroll Pane Styling
        JScrollPane scroll = new JScrollPane(list);
        scroll.getViewport().setBackground(BG_DARK);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildItemRow(Store store, MenuItem item, MainFrame parent) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(CARD_BG);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(10, 14, 10, 14)));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

        // Item Name & Calculated Price
        JLabel name = new JLabel(item.getItemDetails() +
                String.format("   \u2014   \u20B1%.2f", item.calculateTotalPrice()));
        name.setFont(ITEM_FONT);
        name.setForeground(TEXT_LIGHT);
        row.add(name, BorderLayout.CENTER);

        // Right Controls: Quantity Label, Spinner, Add Button
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        controls.setOpaque(false);

        JLabel qtyLabel = new JLabel("Qty:");
        qtyLabel.setFont(LABEL_FONT);
        qtyLabel.setForeground(TEXT_MUTED);

        JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
        styleSpinner(qtySpinner);

        // Add Button
        JButton addBtn = createCustomButton("Add");

        addBtn.addActionListener(e -> {
            int qty = (Integer) qtySpinner.getValue();
            parent.addToCart(store, item, qty);
        });

        controls.add(qtyLabel);
        controls.add(qtySpinner);
        controls.add(addBtn);
        row.add(controls, BorderLayout.EAST);

        return row;
    }

    private void styleSpinner(JSpinner spinner) {
        spinner.setPreferredSize(new Dimension(55, 30));
        spinner.setFont(LABEL_FONT);
        spinner.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        // Style Text Editor Field
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
            tf.setBackground(BG_DARK);
            tf.setForeground(TEXT_LIGHT);
            tf.setCaretColor(TEXT_LIGHT);
            tf.setHorizontalAlignment(JTextField.CENTER);
            tf.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        }

        // Style Spinner Arrow Buttons
        for (Component c : spinner.getComponents()) {
            if (c instanceof JButton) {
                JButton arrowBtn = (JButton) c;
                arrowBtn.setBackground(CARD_BG);
                arrowBtn.setForeground(TEXT_LIGHT);
                arrowBtn.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
                arrowBtn.setFocusPainted(false);
            }
        }
    }

    /** Creates a JButton with custom background painting to bypass OS native styling. */
    private JButton createCustomButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(ACCENT_PRIMARY.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(ACCENT_PRIMARY.brighter());
                } else {
                    g2.setColor(ACCENT_PRIMARY);
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);

                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int stringWidth = fm.stringWidth(getText());
                int stringHeight = fm.getAscent();
                g2.drawString(getText(), (getWidth() - stringWidth) / 2, (getHeight() + stringHeight) / 2 - 2);

                g2.dispose();
            }
        };

        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(65, 30));

        return button;
    }
}