package smartcanteen.gui;

import smartcanteen.model.MasterReceipt;
import smartcanteen.model.OrderBoard;
import smartcanteen.model.PaymentProcessor;
import smartcanteen.model.SubOrder;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Shows the final master receipt, any big-bill change alerts, and a
 * live "In Preparation" / "Ready for Pickup" status per store that
 * updates automatically as staff advance each sub-order on the kitchen
 * display. Non-modal so the customer can keep browsing or step away
 * while it stays open and refreshes.
 */
public class ReceiptDialog extends JDialog {
    private final PaymentProcessor processor;
    private final OrderBoard board;
    private final JPanel statusContainer;

    public ReceiptDialog(Frame owner, MasterReceipt receipt, PaymentProcessor processor, OrderBoard board) {
        super(owner, "Order Confirmed - SmartCanteen", false);
        this.processor = processor;
        this.board = board;

        setSize(480, 660);
        setMinimumSize(new Dimension(420, 520));
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 247, 250));

        // 1. Dark Top Banner
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(new Color(24, 28, 36));
        headerPanel.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel titleLabel = new JLabel("Order Confirmed!");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Track your real-time store queue status below");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(180, 185, 195));

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        headerPanel.add(subtitleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // 2. Center Content Area
        JPanel centerContent = new JPanel();
        centerContent.setLayout(new BoxLayout(centerContent, BoxLayout.Y_AXIS));
        centerContent.setOpaque(false);
        centerContent.setBorder(new EmptyBorder(12, 16, 12, 16));

        // Kitchen Change Alert Banner (If Applicable)
        if (!processor.getChangeAlerts().isEmpty()) {
            JPanel alertCard = new JPanel(new BorderLayout(8, 0));
            alertCard.setBackground(new Color(254, 242, 242));
            alertCard.setBorder(new CompoundBorder(
                    new LineBorder(new Color(252, 165, 165), 1, true),
                    new EmptyBorder(10, 12, 10, 12)
            ));
            alertCard.setAlignmentX(Component.LEFT_ALIGNMENT);

            JTextArea alertText = new JTextArea();
            alertText.setEditable(false);
            alertText.setLineWrap(true);
            alertText.setWrapStyleWord(true);
            alertText.setOpaque(false);
            alertText.setFont(new Font("SansSerif", Font.BOLD, 12));
            alertText.setForeground(new Color(185, 28, 28));

            StringBuilder sb = new StringBuilder("Kitchen Display Alerts:\n");
            for (String alert : processor.getChangeAlerts()) {
                sb.append("\u2022 ").append(alert).append("\n");
            }
            alertText.setText(sb.toString().trim());

            alertCard.add(alertText, BorderLayout.CENTER);
            centerContent.add(alertCard);
            centerContent.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        // Live Order Tracker Header & Container
        JLabel trackerTitle = new JLabel("Live Order Tracker");
        trackerTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        trackerTitle.setForeground(new Color(24, 28, 36));
        trackerTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        centerContent.add(trackerTitle);
        centerContent.add(Box.createRigidArea(new Dimension(0, 6)));

        statusContainer = new JPanel();
        statusContainer.setLayout(new BoxLayout(statusContainer, BoxLayout.Y_AXIS));
        statusContainer.setOpaque(false);
        statusContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        centerContent.add(statusContainer);
        centerContent.add(Box.createRigidArea(new Dimension(0, 12)));

        // Monospaced Receipt Preview Container Card
        JPanel receiptCard = new JPanel(new BorderLayout());
        receiptCard.setBackground(Color.WHITE);
        receiptCard.setBorder(new LineBorder(new Color(210, 215, 225), 1, true));
        receiptCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea summaryArea = new JTextArea(receipt.generateSummary());
        summaryArea.setEditable(false);
        summaryArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        summaryArea.setBackground(Color.WHITE);
        summaryArea.setForeground(new Color(24, 28, 36));
        summaryArea.setBorder(new EmptyBorder(10, 12, 10, 12));

        JScrollPane scrollPane = new JScrollPane(summaryArea);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);

        receiptCard.add(scrollPane, BorderLayout.CENTER);
        centerContent.add(receiptCard);

        add(centerContent, BorderLayout.CENTER);

        // 3. Bottom Close Window Action Bar
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setBackground(Color.WHITE);
        bottomBar.setBorder(new CompoundBorder(
                new LineBorder(new Color(210, 215, 225), 1),
                new EmptyBorder(12, 16, 12, 16)
        ));

        JButton closeBtn = new JButton("Close Window");
        closeBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        closeBtn.setFocusPainted(false);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setOpaque(true);
        closeBtn.setBackground(new Color(24, 28, 36)); // High-contrast solid dark
        closeBtn.setForeground(Color.WHITE);            // Visible white text
        closeBtn.setBorder(new CompoundBorder(
                new LineBorder(new Color(15, 20, 28), 1),
                new EmptyBorder(8, 0, 8, 0)
        ));
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        closeBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                closeBtn.setBackground(new Color(45, 52, 64));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                closeBtn.setBackground(new Color(24, 28, 36));
            }
        });

        closeBtn.addActionListener(e -> dispose());

        bottomBar.add(closeBtn, BorderLayout.CENTER);
        add(bottomBar, BorderLayout.SOUTH);

        refreshStatus();
        board.addListener(this::refreshStatus);
    }

    private void refreshStatus() {
        SwingUtilities.invokeLater(() -> {
            statusContainer.removeAll();
            for (SubOrder so : processor.getSubOrdersList()) {
                statusContainer.add(createStatusRow(so));
                statusContainer.add(Box.createRigidArea(new Dimension(0, 6)));
            }
            statusContainer.revalidate();
            statusContainer.repaint();
        });
    }

    private JPanel createStatusRow(SubOrder so) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setBackground(Color.WHITE);
        row.setBorder(new CompoundBorder(
                new LineBorder(new Color(225, 228, 232), 1, true),
                new EmptyBorder(10, 12, 10, 12)
        ));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        boolean isReady = so.getStatus() == SubOrder.Status.READY || so.getStatus() == SubOrder.Status.COMPLETED;

        // Store & Queue Title
        JLabel storeInfo = new JLabel(String.format("%s  \u2022  Queue #%d", so.getStore().getStoreName(), so.getQueueNumber()));
        storeInfo.setFont(new Font("SansSerif", Font.BOLD, 13));
        storeInfo.setForeground(new Color(24, 28, 36));

        // Styled Status Badge Pill
        JLabel statusBadge = new JLabel(isReady ? "Ready for Pickup" : "In Preparation");
        statusBadge.setFont(new Font("SansSerif", Font.BOLD, 11));
        statusBadge.setForeground(statusColor(so));
        statusBadge.setBackground(statusBackgroundColor(so));
        statusBadge.setOpaque(true);
        statusBadge.setBorder(new CompoundBorder(
                new LineBorder(statusColor(so), 1, true),
                new EmptyBorder(3, 8, 3, 8)
        ));

        row.add(storeInfo, BorderLayout.WEST);
        row.add(statusBadge, BorderLayout.EAST);
        return row;
    }

    private Color statusColor(SubOrder so) {
        return (so.getStatus() == SubOrder.Status.READY || so.getStatus() == SubOrder.Status.COMPLETED)
                ? new Color(22, 101, 52)  // Dark Forest Green
                : new Color(180, 83, 9);   // Warm Amber / Orange
    }

    private Color statusBackgroundColor(SubOrder so) {
        return (so.getStatus() == SubOrder.Status.READY || so.getStatus() == SubOrder.Status.COMPLETED)
                ? new Color(240, 253, 244)  // Light Green Tint
                : new Color(254, 243, 199);  // Light Amber Tint
    }
}