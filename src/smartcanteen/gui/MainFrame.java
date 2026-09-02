package smartcanteen.gui;

import smartcanteen.model.Customer;
import smartcanteen.model.OrderItem;
import smartcanteen.model.SampleData;
import smartcanteen.model.Store;
import smartcanteen.model.SubOrder;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Main application window: 5-store tabbed navigation menu + cart sidebar.
 */
public class MainFrame extends JFrame {

    // Central Color Palette (Accessible by other GUI components)
    public static final Color BG_DARK = new Color(24, 28, 36);
    public static final Color PANEL_BG = new Color(33, 39, 50);
    public static final Color BORDER_COLOR = new Color(50, 55, 65);
    public static final Color ACCENT_BLUE = new Color(13, 110, 253);
    public static final Color TEXT_LIGHT = new Color(240, 240, 240);
    public static final Color TEXT_MUTED = new Color(160, 165, 175);

    // Central Fonts
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font TAB_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    private java.util.List<Store> stores;
    private Customer customer;
    private Map<Store, SubOrder> cart;
    private CartPanel cartPanel;
    private int subOrderCounter = 0;

    public MainFrame() {
        super("SmartCanteen - Self-Service Ordering System");

        this.stores = SampleData.buildStores();
        this.customer = new Customer("C-0001", "Guest Customer", "N/A");
        this.cart = new LinkedHashMap<>();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750); // Increased default dimensions
        setMinimumSize(new Dimension(950, 600));
        setLocationRelativeTo(null);

        // Root Container Setup
        JPanel rootPanel = new JPanel(new BorderLayout(12, 12));
        rootPanel.setBackground(BG_DARK);
        rootPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // 1. TOP: Modern Header Bar
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PANEL_BG);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(14, 20, 14, 20)
        ));

        JLabel title = new JLabel("SmartCanteen \u2014 Cebu Eastern College", SwingConstants.LEFT);
        title.setFont(TITLE_FONT);
        title.setForeground(TEXT_LIGHT);
        header.add(title, BorderLayout.WEST);

        // Customer Name Input Panel
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        namePanel.setOpaque(false);

        JLabel nameLabel = new JLabel("Customer Name:");
        nameLabel.setFont(BODY_FONT);
        nameLabel.setForeground(TEXT_MUTED);

        JTextField nameField = new JTextField(customer.getName(), 16);
        nameField.setFont(BODY_FONT);
        nameField.setBackground(BG_DARK);
        nameField.setForeground(TEXT_LIGHT);
        nameField.setCaretColor(TEXT_LIGHT);
        nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(6, 10, 6, 10)
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

        rootPanel.add(header, BorderLayout.NORTH);

        // 2. CENTER: Tabbed Navigation Pane with Custom Dark UI
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(TAB_FONT);
        tabs.setUI(new EnhancedDarkTabbedPaneUI(tabs));

        for (Store store : stores) {
            tabs.addTab(store.getStoreName(), new StorePanel(store, this));
        }
        rootPanel.add(tabs, BorderLayout.CENTER);

        // 3. EAST: Cart Sidebar
        cartPanel = new CartPanel(this);
        rootPanel.add(cartPanel, BorderLayout.EAST);

        setContentPane(rootPanel);
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

    public void clearCart() {
        cart.clear();
        cartPanel.refresh();
    }

    /**
     * Enhanced Custom Tabbed UI: Adds active accent indicators, mouse hover effects,
     * enlarged padding, and enforces dark colors over native L&F rendering.
     */
    private static class EnhancedDarkTabbedPaneUI extends BasicTabbedPaneUI {
        private int hoveredIndex = -1;

        public EnhancedDarkTabbedPaneUI(JTabbedPane tabbedPane) {
            tabbedPane.addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    int index = tabbedPane.indexAtLocation(e.getX(), e.getY());
                    if (index != hoveredIndex) {
                        hoveredIndex = index;
                        tabbedPane.repaint();
                    }
                }
            });
            tabbedPane.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseExited(MouseEvent e) {
                    if (hoveredIndex != -1) {
                        hoveredIndex = -1;
                        tabbedPane.repaint();
                    }
                }
            });
        }

        @Override
        protected void installDefaults() {
            super.installDefaults();
            tabAreaInsets = new Insets(6, 6, 0, 6);
            selectedTabPadInsets = new Insets(0, 0, 0, 0);
            contentBorderInsets = new Insets(1, 1, 1, 1);
        }

        @Override
        protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
            return super.calculateTabHeight(tabPlacement, tabIndex, fontHeight) + 16; // Generous height padding
        }

        @Override
        protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
            return super.calculateTabWidth(tabPlacement, tabIndex, metrics) + 24; // Generous width padding
        }

        @Override
        protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
                                          int x, int y, int w, int h, boolean isSelected) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (isSelected) {
                g2.setColor(PANEL_BG);
            } else if (tabIndex == hoveredIndex) {
                g2.setColor(new Color(40, 47, 60)); // Subtle hover highlight
            } else {
                g2.setColor(BG_DARK);
            }

            g2.fillRect(x, y, w, h);
            g2.dispose();
        }

        @Override
        protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
                                      int x, int y, int w, int h, boolean isSelected) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(BORDER_COLOR);
            g2.drawRect(x, y, w - 1, h);

            if (isSelected) {
                g2.setColor(ACCENT_BLUE);
                g2.fillRect(x, y, w - 1, 4); // Thicker primary blue accent bar on top
            }

            g2.dispose();
        }

        @Override
        protected void paintText(Graphics g, int tabPlacement, Font font,
                                 FontMetrics metrics, int tabIndex, String title,
                                 Rectangle textRect, boolean isSelected) {
            g.setFont(font);
            if (isSelected) {
                g.setColor(TEXT_LIGHT);
            } else if (tabIndex == hoveredIndex) {
                g.setColor(Color.WHITE);
            } else {
                g.setColor(TEXT_MUTED);
            }
            g.drawString(title, textRect.x, textRect.y + metrics.getAscent());
        }

        @Override
        protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects,
                                           int tabIndex, Rectangle iconRect, Rectangle textRect,
                                           boolean isSelected) {
            // Disabled default dotted focus ring
        }

        @Override
        protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(BORDER_COLOR);
            int width = tabPane.getWidth();
            int height = tabPane.getHeight();
            Insets insets = tabPane.getInsets();

            int x = insets.left;
            int y = insets.top + calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
            int w = width - insets.left - insets.right;
            int h = height - y - insets.bottom;

            g2.drawRect(x, y, w - 1, h - 1);
            g2.dispose();
        }
    }
}