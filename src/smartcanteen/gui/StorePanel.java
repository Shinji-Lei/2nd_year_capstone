package smartcanteen.gui;

import smartcanteen.model.MenuItem;
import smartcanteen.model.Store;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicSpinnerUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Displays one store's menu inside a dark-themed container,
 * complete with quantity selection spinners and add-to-cart controls per item.
 */
public class StorePanel extends JPanel {

    /**
     * Constructs the store panel displaying store metadata and scrollable menu items.
     *
     * @param store  The target store entity containing menu data and location info.
     * @param parent Main application window reference for triggering shopping cart additions.
     */
    public StorePanel(Store store, MainFrame parent) {
        // Main panel setup using vertical spacing with dark backdrop
        setLayout(new BorderLayout(0, 12));
        setBackground(new Color(18, 20, 26)); // Dark main background
        setBorder(new EmptyBorder(16, 16, 16, 16));

        // ==========================================
        // 1. STORE HEADER CARD
        // Displays store title, location, and dark badge
        // ==========================================
        JPanel headerCard = new JPanel(new BorderLayout());
        headerCard.setBackground(new Color(30, 34, 45)); // Dark panel card surface
        headerCard.setBorder(new CompoundBorder(
                new LineBorder(new Color(45, 50, 65), 1, true), // Muted dark border
                new EmptyBorder(14, 16, 14, 16)
        ));

        // Vertical text layout for Store Name and Location
        JPanel headerLeft = new JPanel();
        headerLeft.setLayout(new BoxLayout(headerLeft, BoxLayout.Y_AXIS));
        headerLeft.setOpaque(false);

        JLabel title = new JLabel(store.getStoreName());
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(Color.WHITE); // High-contrast primary text

        JLabel location = new JLabel("Location: " + store.getLocation());
        location.setFont(new Font("SansSerif", Font.PLAIN, 12));
        location.setForeground(new Color(156, 163, 175)); // Muted secondary text

        headerLeft.add(title);
        headerLeft.add(Box.createRigidArea(new Dimension(0, 2))); // Vertical spacing
        headerLeft.add(location);

        // Dark accent status badge ("STORE MENU")
        JLabel menuBadge = new JLabel("STORE MENU");
        menuBadge.setFont(new Font("SansSerif", Font.BOLD, 11));
        menuBadge.setForeground(new Color(74, 222, 128)); // Bright emerald green
        menuBadge.setBackground(new Color(16, 42, 28));    // Deep green container fill
        menuBadge.setOpaque(true);
        menuBadge.setBorder(new CompoundBorder(
                new LineBorder(new Color(34, 197, 94), 1), // Vibrant green border
                new EmptyBorder(6, 10, 6, 10)
        ));

        headerCard.add(headerLeft, BorderLayout.WEST);
        headerCard.add(menuBadge, BorderLayout.EAST);

        add(headerCard, BorderLayout.NORTH);

        // ==========================================
        // 2. SCROLLABLE MENU ITEMS LIST
        // Dynamically populates and lays out store items
        // ==========================================
        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setOpaque(false);

        // Populate items row by row
        for (MenuItem item : store.getMenu()) {
            list.add(buildItemRow(store, item, parent));
            list.add(Box.createRigidArea(new Dimension(0, 8))); // Gap between item rows
        }

        // Custom dark JScrollPane container configuration
        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16); // Smooth scroll speed

        add(scroll, BorderLayout.CENTER);
    }

    /**
     * Builds an individual menu item row with title, pricing, dark quantity spinner, and add button.
     *
     * @param store  Parent store object.
     * @param item   The specific menu item data.
     * @param parent MainFrame controller for cart actions.
     * @return JPanel representing a styled row card.
     */
    private JPanel buildItemRow(Store store, MenuItem item, MainFrame parent) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setBackground(new Color(30, 34, 45)); // Dark row card surface
        row.setBorder(new CompoundBorder(
                new LineBorder(new Color(45, 50, 65), 1, true), // Dark card border
                new EmptyBorder(10, 16, 10, 16)
        ));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        row.setPreferredSize(new Dimension(0, 64));

        // ------------------------------------------
        // LEFT CONTENT: Item Description & Price
        // ------------------------------------------
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(item.getItemDetails());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        nameLabel.setForeground(Color.WHITE); // Primary white title

        JLabel priceLabel = new JLabel(String.format("\u20B1%.2f", item.calculateTotalPrice()));
        priceLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        priceLabel.setForeground(new Color(74, 222, 128)); // Bright green accent price

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        infoPanel.add(priceLabel);

        row.add(infoPanel, BorderLayout.CENTER);

        // ------------------------------------------
        // RIGHT CONTROLS: Dark Qty Spinner & Add Button
        // ------------------------------------------
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controls.setOpaque(false);

        // Quantity Label
        JLabel qtyLabel = new JLabel("Qty:");
        qtyLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        qtyLabel.setForeground(new Color(156, 163, 175)); // Muted grey text

        // Dark-themed Quantity JSpinner
        JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
        qtySpinner.setFont(new Font("SansSerif", Font.BOLD, 12));
        qtySpinner.setPreferredSize(new Dimension(56, 32));
        qtySpinner.setUI(new BasicSpinnerUI()); // Resets native OS overrides for full style control
        qtySpinner.setBorder(new LineBorder(new Color(60, 66, 82), 1));

        // Style JSpinner internal text field for Dark Theme
        JComponent editor = qtySpinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
            tf.setBackground(new Color(20, 24, 33)); // Dark inset background
            tf.setForeground(Color.WHITE);          // White numerical text
            tf.setCaretColor(Color.WHITE);
        }

        // Blue Accent "Add to Cart" Button
        JButton addBtn = new JButton("Add to Cart");
        addBtn.setUI(new BasicButtonUI()); // Overrides OS button skinning
        addBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        addBtn.setFocusPainted(false);
        addBtn.setOpaque(true);
        addBtn.setBackground(new Color(59, 130, 246));  // Primary accent blue
        addBtn.setForeground(Color.WHITE);
        addBtn.setBorder(new CompoundBorder(
                new LineBorder(new Color(37, 99, 235), 1), // Blue border outline
                new EmptyBorder(6, 14, 6, 14)
        ));
        addBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover Effect Handlers (Blue Highlight Transition)
        addBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                addBtn.setBackground(new Color(29, 78, 216)); // Darker blue hover state
            }
            @Override
            public void mouseExited(MouseEvent e) {
                addBtn.setBackground(new Color(59, 130, 246)); // Default accent blue
            }
        });

        // Click Action: Retrieves spinner qty and triggers main application order callback
        addBtn.addActionListener(e -> {
            int qty = (Integer) qtySpinner.getValue();
            parent.addToCart(store, item, qty);
        });

        // Assemble control container elements
        controls.add(qtyLabel);
        controls.add(qtySpinner);
        controls.add(addBtn);

        row.add(controls, BorderLayout.EAST);

        return row;
    }
}