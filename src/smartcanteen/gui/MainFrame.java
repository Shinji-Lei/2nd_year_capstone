package smartcanteen.gui;

import smartcanteen.model.Customer;
import smartcanteen.model.OrderBoard;
import smartcanteen.model.OrderItem;
import smartcanteen.model.SampleData;
import smartcanteen.model.Store;
import smartcanteen.model.SubOrder;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Main application window: 5-store tabbed navigation menu + cart sidebar.
 */
public class MainFrame extends JFrame {
    private final java.util.List<Store> stores;
    private final Customer customer;
    private final Map<Store, SubOrder> cart;
    private final CartPanel cartPanel;
    private final OrderBoard orderBoard;
    private int subOrderCounter = 0;

    // Modern Tab Navigation State
    private final CardLayout storeCardLayout = new CardLayout();
    private final JPanel storeCardContainer = new JPanel(storeCardLayout);
    private final Map<Store, JButton> navButtons = new HashMap<>();
    private final JLabel storeLocationLabel = new JLabel();
    private Store currentSelectedStore;

    public MainFrame() {
        super("SmartCanteen - Self-Service Ordering System");
        this.stores = SampleData.buildStores();
        this.customer = new Customer("C-0001", "Guest Customer", "N/A");
        this.cart = new LinkedHashMap<>();
        this.orderBoard = new OrderBoard();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setMinimumSize(new Dimension(850, 550));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header Panel Construction
        JPanel header = createHeaderPanel();
        add(header, BorderLayout.NORTH);

        // Center Area: Custom Modern Store Navigation Bar + Store Panels
        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.setBackground(new Color(245, 247, 250));

        JPanel navBar = createModernNavBar();
        centerContainer.add(navBar, BorderLayout.NORTH);

        // Populate Card Container with StorePanels
        for (Store store : stores) {
            storeCardContainer.add(new StorePanel(store, this), store.getStoreName());
        }
        centerContainer.add(storeCardContainer, BorderLayout.CENTER);

        add(centerContainer, BorderLayout.CENTER);

        // Sidebar Cart Panel
        cartPanel = new CartPanel(this);
        add(cartPanel, BorderLayout.EAST);

        // Select initial store tab
        if (!stores.isEmpty()) {
            selectStoreTab(stores.get(0));
        }
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout(15, 0));
        header.setBackground(new Color(24, 28, 36)); // Dark slate header background
        header.setBorder(new EmptyBorder(12, 16, 12, 16));

        // Staff Action Button (Left) - Explicit styling for text visibility
        JButton staffBtn = new JButton("Staff / Kitchen Display");
        staffBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        staffBtn.setFocusPainted(false);
        staffBtn.setContentAreaFilled(false);
        staffBtn.setOpaque(true);
        staffBtn.setBackground(new Color(45, 52, 64));
        staffBtn.setForeground(Color.WHITE);
        staffBtn.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(new Color(75, 85, 100), 1),
                new EmptyBorder(8, 14, 8, 14)
        ));
        staffBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        staffBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                staffBtn.setBackground(new Color(60, 70, 85));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                staffBtn.setBackground(new Color(45, 52, 64));
            }
        });

        staffBtn.addActionListener(e ->
                new KitchenDisplayFrame(stores, orderBoard).setVisible(true));

        JPanel staffPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        staffPanel.setOpaque(false);
        staffPanel.add(staffBtn);
        header.add(staffPanel, BorderLayout.WEST);

        // Center Title Branding
        JLabel title = new JLabel("SmartCanteen \u2014 Cebu Eastern College", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.CENTER);

        // Right Customer Name Input Panel
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        namePanel.setOpaque(false);

        JLabel nameLabel = new JLabel("Customer Name:");
        nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        nameLabel.setForeground(new Color(200, 205, 215));

        JTextField nameField = new JTextField(customer.getName(), 14);
        nameField.setFont(new Font("SansSerif", Font.BOLD, 12));
        nameField.setBackground(new Color(36, 42, 54));
        nameField.setForeground(Color.WHITE);
        nameField.setCaretColor(Color.WHITE);
        nameField.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(new Color(75, 85, 100), 1),
                new EmptyBorder(5, 10, 5, 10)
        ));

        nameField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void sync() {
                String text = nameField.getText().trim();
                customer.setName(text.isEmpty() ? "Guest Customer" : text);
            }
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { sync(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { sync(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { sync(); }
        });

        namePanel.add(nameLabel);
        namePanel.add(nameField);
        header.add(namePanel, BorderLayout.EAST);

        return header;
    }

    /**
     * Builds the pill-style navigation bar for switching between canteen stores.
     */
    private JPanel createModernNavBar() {
        JPanel navContainer = new JPanel(new BorderLayout());
        navContainer.setBackground(Color.WHITE);
        navContainer.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(225, 228, 232)),
                new EmptyBorder(10, 16, 10, 16)
        ));

        // Pill Tab Button Group
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttonRow.setOpaque(false);

        for (Store store : stores) {
            JButton tabBtn = new JButton(store.getStoreName());
            tabBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
            tabBtn.setFocusPainted(false);
            tabBtn.setBorderPainted(false);
            tabBtn.setContentAreaFilled(false);
            tabBtn.setOpaque(true);
            tabBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            tabBtn.setMargin(new Insets(8, 16, 8, 16));

            tabBtn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!store.equals(currentSelectedStore)) {
                        tabBtn.setBackground(new Color(225, 230, 236));
                    }
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    if (!store.equals(currentSelectedStore)) {
                        tabBtn.setBackground(new Color(240, 243, 246));
                    }
                }
            });

            tabBtn.addActionListener(e -> selectStoreTab(store));
            navButtons.put(store, tabBtn);
            buttonRow.add(tabBtn);
        }

        // Active Store Subtitle / Location Chip
        storeLocationLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        storeLocationLabel.setForeground(new Color(108, 117, 125));

        navContainer.add(buttonRow, BorderLayout.WEST);
        navContainer.add(storeLocationLabel, BorderLayout.EAST);

        return navContainer;
    }

    /**
     * Updates navigation tab highlight states and switches store views.
     */
    private void selectStoreTab(Store store) {
        this.currentSelectedStore = store;

        for (Map.Entry<Store, JButton> entry : navButtons.entrySet()) {
            JButton btn = entry.getValue();
            if (entry.getKey().equals(store)) {
                btn.setBackground(new Color(24, 28, 36));
                btn.setForeground(Color.WHITE);
            } else {
                btn.setBackground(new Color(240, 243, 246));
                btn.setForeground(new Color(80, 88, 100));
            }
        }

        storeLocationLabel.setText("Location: " + store.getLocation() + "  ");
        storeCardLayout.show(storeCardContainer, store.getStoreName());
    }

    public void addToCart(Store store, smartcanteen.model.MenuItem item, int qty) {
        SubOrder subOrder = cart.get(store);
        if (subOrder == null) {
            subOrder = new SubOrder("SO-" + (++subOrderCounter), store);
            cart.put(store, subOrder);
        }
        subOrder.addItem(new OrderItem(item, qty));
        cartPanel.refresh();
    }

    public Map<Store, SubOrder> getCart() {
        return cart;
    }

    public Customer getCustomer() {
        return customer;
    }

    public OrderBoard getOrderBoard() {
        return orderBoard;
    }

    public void clearCart() {
        cart.clear();
        cartPanel.refresh();
    }
}