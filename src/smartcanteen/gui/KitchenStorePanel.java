package smartcanteen.gui;

import smartcanteen.model.OrderBoard;
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
import java.util.List;

/**
 * Staff-facing panel for one store: lists every sub-order sent to that
 * stall, its change-preparation alert, and lets staff advance its status.
 */
public class KitchenStorePanel extends JPanel {
    private final Store store;
    private final OrderBoard board;
    private final JPanel listContainer;
    private final JLabel titleLabel;

    public KitchenStorePanel(Store store, OrderBoard board) {
        this.store = store;
        this.board = board;

        setLayout(new BorderLayout(0, 12));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(16, 16, 16, 16));

        // Store Section Header Card
        JPanel headerCard = new JPanel(new BorderLayout());
        headerCard.setBackground(Color.WHITE);
        headerCard.setBorder(new CompoundBorder(
                new LineBorder(new Color(225, 228, 232), 1, true),
                new EmptyBorder(12, 16, 12, 16)
        ));

        titleLabel = new JLabel(store.getStoreName() + " \u2014 Incoming Orders");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(new Color(24, 28, 36));

        JLabel locationLabel = new JLabel("Location: " + store.getLocation());
        locationLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        locationLabel.setForeground(new Color(108, 117, 125));

        JPanel headerLeft = new JPanel();
        headerLeft.setLayout(new BoxLayout(headerLeft, BoxLayout.Y_AXIS));
        headerLeft.setOpaque(false);
        headerLeft.add(titleLabel);
        headerLeft.add(Box.createRigidArea(new Dimension(0, 2)));
        headerLeft.add(locationLabel);

        headerCard.add(headerLeft, BorderLayout.WEST);
        add(headerCard, BorderLayout.NORTH);

        // Scrollable List Container
        listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setOpaque(false);

        JScrollPane scroll = new JScrollPane(listContainer);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        add(scroll, BorderLayout.CENTER);

        refresh();
    }

    public void refresh() {
        listContainer.removeAll();

        List<SubOrder> orders = board.getOrdersForStore(store);
        titleLabel.setText(String.format("%s \u2014 Incoming Orders (%d)", store.getStoreName(), orders.size()));

        if (orders.isEmpty()) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
            emptyPanel.setOpaque(false);
            emptyPanel.setBorder(new EmptyBorder(40, 20, 40, 20));

            JLabel emptyIcon = new JLabel("\u2615", SwingConstants.CENTER);
            emptyIcon.setFont(new Font("SansSerif", Font.PLAIN, 36));
            emptyIcon.setForeground(new Color(180, 185, 195));
            emptyIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel emptyText = new JLabel("No active orders for this store yet.", SwingConstants.CENTER);
            emptyText.setFont(new Font("SansSerif", Font.BOLD, 14));
            emptyText.setForeground(new Color(108, 117, 125));
            emptyText.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel emptySub = new JLabel("New checkout submissions will appear here automatically.", SwingConstants.CENTER);
            emptySub.setFont(new Font("SansSerif", Font.PLAIN, 12));
            emptySub.setForeground(new Color(150, 155, 165));
            emptySub.setAlignmentX(Component.CENTER_ALIGNMENT);

            emptyPanel.add(emptyIcon);
            emptyPanel.add(Box.createRigidArea(new Dimension(0, 8)));
            emptyPanel.add(emptyText);
            emptyPanel.add(Box.createRigidArea(new Dimension(0, 4)));
            emptyPanel.add(emptySub);

            listContainer.add(emptyPanel);
        } else {
            for (int i = orders.size() - 1; i >= 0; i--) {
                listContainer.add(buildOrderCard(orders.get(i)));
                listContainer.add(Box.createRigidArea(new Dimension(0, 12)));
            }
        }

        listContainer.revalidate();
        listContainer.repaint();
    }

    private JPanel buildOrderCard(SubOrder subOrder) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(225, 228, 232), 1, true),
                new EmptyBorder(14, 16, 14, 16)
        ));
        // Allowed natural dynamic height expansion so buttons are never clipped
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Header: Queue # & Status Chip
        JPanel cardHeader = new JPanel(new BorderLayout());
        cardHeader.setOpaque(false);

        JLabel queueLabel = new JLabel("Queue #" + subOrder.getQueueNumber());
        queueLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        queueLabel.setForeground(new Color(24, 28, 36));

        JLabel statusBadge = createStatusBadge(subOrder.getStatus());

        cardHeader.add(queueLabel, BorderLayout.WEST);
        cardHeader.add(statusBadge, BorderLayout.EAST);
        card.add(cardHeader, BorderLayout.NORTH);

        // Center Content: Item List & Alerts
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        // Items Box
        JPanel itemsBox = new JPanel();
        itemsBox.setLayout(new BoxLayout(itemsBox, BoxLayout.Y_AXIS));
        itemsBox.setOpaque(false);
        itemsBox.setBorder(new EmptyBorder(4, 0, 8, 0));

        for (OrderItem oi : subOrder.getItemsList()) {
            JLabel itemLabel = new JLabel(String.format("\u2022  %s  \u00D7  %d",
                    oi.getMenuItem().getItemDetails(), oi.getQuantity()));
            itemLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
            itemLabel.setForeground(new Color(50, 55, 65));
            itemsBox.add(itemLabel);
            itemsBox.add(Box.createRigidArea(new Dimension(0, 4)));
        }
        centerPanel.add(itemsBox);

        // Kitchen Alert Banner
        JPanel alertBanner = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        alertBanner.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (subOrder.getPaymentType() == SubOrder.PaymentType.BIG_BILL) {
            alertBanner.setBackground(new Color(254, 242, 242));
            alertBanner.setBorder(new LineBorder(new Color(254, 202, 202), 1, true));

            JLabel alertLabel = new JLabel(String.format("\u26A0 PREPARE CHANGE: \u20B1%.2f", subOrder.getChangeToReturn()));
            alertLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
            alertLabel.setForeground(new Color(185, 28, 28));
            alertBanner.add(alertLabel);
        } else {
            alertBanner.setBackground(new Color(240, 253, 244));
            alertBanner.setBorder(new LineBorder(new Color(187, 247, 208), 1, true));

            JLabel alertLabel = new JLabel("Exact Cash / No Change Needed");
            alertLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
            alertLabel.setForeground(new Color(21, 128, 61));
            alertBanner.add(alertLabel);
        }
        centerPanel.add(alertBanner);
        card.add(centerPanel, BorderLayout.CENTER);

        // Bottom Action Controls
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actionRow.setOpaque(false);
        actionRow.setBorder(new EmptyBorder(8, 0, 0, 0));

        SubOrder.Status current = subOrder.getStatus();
        if (current == SubOrder.Status.PAID) {
            JButton processBtn = createActionButton(
                    "Start Preparing Order",
                    new Color(24, 28, 36),
                    new Color(45, 52, 64)
            );
            processBtn.addActionListener(e -> advance(subOrder, SubOrder.Status.PREPARING));
            actionRow.add(processBtn);
        } else if (current == SubOrder.Status.PREPARING) {
            JButton readyBtn = createActionButton(
                    "Mark Ready for Pickup",
                    new Color(21, 128, 61),
                    new Color(22, 101, 52)
            );
            readyBtn.addActionListener(e -> advance(subOrder, SubOrder.Status.READY));
            actionRow.add(readyBtn);
        } else {
            JLabel doneLabel = new JLabel("\u2713 Ready for Pickup \u2014 Awaiting Customer");
            doneLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
            doneLabel.setForeground(new Color(21, 128, 61));
            actionRow.add(doneLabel);
        }
        card.add(actionRow, BorderLayout.SOUTH);

        return card;
    }

    /**
     * Creates a custom flat Swing button that overrides BasicButtonUI
     * and handles hover animations explicitly.
     */
    private JButton createActionButton(String text, Color baseColor, Color hoverColor) {
        JButton button = new JButton(text);

        // Strip OS-native UI rendering delegate to fix contrast issues
        button.setUI(new BasicButtonUI());

        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(baseColor);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(8, 16, 8, 16));

        // Smooth hover response
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(baseColor);
            }
        });

        return button;
    }

    private JLabel createStatusBadge(SubOrder.Status status) {
        JLabel badge = new JLabel();
        badge.setFont(new Font("SansSerif", Font.BOLD, 11));
        badge.setOpaque(true);

        switch (status) {
            case PAID -> {
                badge.setText("NEW ORDER");
                badge.setForeground(new Color(30, 64, 175));
                badge.setBackground(new Color(219, 234, 254));
                badge.setBorder(new CompoundBorder(
                        new LineBorder(new Color(191, 219, 254), 1, true),
                        new EmptyBorder(3, 8, 3, 8)
                ));
            }
            case PREPARING -> {
                badge.setText("IN PREPARATION");
                badge.setForeground(new Color(180, 83, 9));
                badge.setBackground(new Color(254, 243, 199));
                badge.setBorder(new CompoundBorder(
                        new LineBorder(new Color(253, 230, 138), 1, true),
                        new EmptyBorder(3, 8, 3, 8)
                ));
            }
            default -> {
                badge.setText("READY FOR PICKUP");
                badge.setForeground(new Color(21, 128, 61));
                badge.setBackground(new Color(220, 252, 231));
                badge.setBorder(new CompoundBorder(
                        new LineBorder(new Color(187, 247, 208), 1, true),
                        new EmptyBorder(3, 8, 3, 8)
                ));
            }
        }
        return badge;
    }

    private void advance(SubOrder subOrder, SubOrder.Status next) {
        subOrder.updateStatus(next);
        board.touch();
    }
}