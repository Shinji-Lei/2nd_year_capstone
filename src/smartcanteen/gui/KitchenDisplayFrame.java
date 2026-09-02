package smartcanteen.gui;

import smartcanteen.model.OrderBoard;
import smartcanteen.model.Store;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * The staff/kitchen side of SmartCanteen: a separate window, one store per tab,
 * showing every paid sub-order live as customers check out, with
 * the change-preparation alerts described in the proposal's flowchart.
 */
public class KitchenDisplayFrame extends JFrame {
    private final List<KitchenStorePanel> storePanels = new ArrayList<>();
    private final List<JButton> navButtons = new ArrayList<>();
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);

    public KitchenDisplayFrame(List<Store> stores, OrderBoard board) {
        super("SmartCanteen \u2014 Staff Kitchen Display");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(820, 700);
        setMinimumSize(new Dimension(580, 500));
        setLocationByPlatform(true);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 247, 250));

        // Header Panel Construction
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(24, 28, 36));
        headerPanel.setBorder(new EmptyBorder(16, 20, 16, 20));

        JPanel headerText = new JPanel();
        headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));
        headerText.setOpaque(false);

        JLabel titleLabel = new JLabel("Staff Kitchen Display");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Live order queue & change-preparation alerts per store");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(180, 185, 195));

        headerText.add(titleLabel);
        headerText.add(Box.createRigidArea(new Dimension(0, 4)));
        headerText.add(subtitleLabel);

        // Live Indicator Badge
        JLabel liveBadge = new JLabel("\u25CF LIVE QUEUE");
        liveBadge.setFont(new Font("SansSerif", Font.BOLD, 11));
        liveBadge.setForeground(new Color(34, 197, 94));
        liveBadge.setBackground(new Color(20, 40, 30));
        liveBadge.setOpaque(true);
        liveBadge.setBorder(new CompoundBorder(
                new LineBorder(new Color(34, 197, 94), 1, true),
                new EmptyBorder(4, 10, 4, 10)
        ));

        headerPanel.add(headerText, BorderLayout.WEST);
        headerPanel.add(liveBadge, BorderLayout.EAST);

        // Combine Header and Redesigned Nav Bar in a single top container
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.add(headerPanel);
        topContainer.add(createRedesignedNavBar(stores, board));

        add(topContainer, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        // Bottom Status Bar
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setBackground(Color.WHITE);
        bottomBar.setBorder(new CompoundBorder(
                new LineBorder(new Color(225, 228, 232), 1),
                new EmptyBorder(10, 20, 10, 20)
        ));

        JLabel footerNote = new JLabel("Advancing order status automatically updates the customer's live tracker.");
        footerNote.setFont(new Font("SansSerif", Font.ITALIC, 11));
        footerNote.setForeground(new Color(108, 117, 125));

        bottomBar.add(footerNote, BorderLayout.WEST);
        add(bottomBar, BorderLayout.SOUTH);

        board.addListener(this::refreshAll);
    }

    /**
     * Replaces default JTabbedPane with a horizontal pill-based segmented navigation bar.
     */
    private JPanel createRedesignedNavBar(List<Store> stores, OrderBoard board) {
        JPanel navWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        navWrapper.setBackground(new Color(238, 241, 246));
        navWrapper.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 224, 230), 1),
                new EmptyBorder(4, 12, 4, 12)
        ));

        for (int i = 0; i < stores.size(); i++) {
            Store store = stores.get(i);
            String cardName = "STORE_" + i;

            // Instantiate content view
            KitchenStorePanel panel = new KitchenStorePanel(store, board);
            storePanels.add(panel);
            contentPanel.add(panel, cardName);

            // Create pill button
            JButton navBtn = createTabButton(store.getStoreName(), cardName);
            navButtons.add(navBtn);
            navWrapper.add(navBtn);

            // Activate first tab by default
            if (i == 0) {
                applyActiveStyle(navBtn);
            }
        }

        return navWrapper;
    }

    private JButton createTabButton(String label, String cardName) {
        JButton button = new JButton(label);
        button.setUI(new BasicButtonUI());
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        applyInactiveStyle(button);

        button.addActionListener(e -> {
            cardLayout.show(contentPanel, cardName);
            for (JButton btn : navButtons) {
                if (btn == button) {
                    applyActiveStyle(btn);
                } else {
                    applyInactiveStyle(btn);
                }
            }
        });

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!isButtonActive(button)) {
                    button.setBackground(new Color(225, 230, 238));
                    button.setForeground(new Color(24, 28, 36));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!isButtonActive(button)) {
                    applyInactiveStyle(button);
                }
            }
        });

        return button;
    }

    private void applyActiveStyle(JButton button) {
        button.setBackground(new Color(24, 28, 36));
        button.setForeground(Color.WHITE);
        button.setBorder(new CompoundBorder(
                new LineBorder(new Color(24, 28, 36), 1, true),
                new EmptyBorder(6, 14, 6, 14)
        ));
    }

    private void applyInactiveStyle(JButton button) {
        button.setBackground(new Color(238, 241, 246));
        button.setForeground(new Color(100, 110, 125));
        button.setBorder(new CompoundBorder(
                new LineBorder(new Color(210, 215, 224), 1, true),
                new EmptyBorder(6, 14, 6, 14)
        ));
    }

    private boolean isButtonActive(JButton button) {
        return button.getBackground().equals(new Color(24, 28, 36));
    }

    private void refreshAll() {
        SwingUtilities.invokeLater(() -> {
            for (KitchenStorePanel panel : storePanels) {
                panel.refresh();
            }
        });
    }
}