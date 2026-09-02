package smartcanteen.gui;

import smartcanteen.model.MasterReceipt;
import smartcanteen.model.PaymentProcessor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Shows the final master receipt plus any kitchen-display change alerts
 * for stalls that received a big bill.
 */
public class ReceiptDialog extends JDialog {
    public ReceiptDialog(Frame owner, MasterReceipt receipt, PaymentProcessor processor) {
        super(owner, "Order Confirmed", true);
        setSize(520, 620); // Expanded size to fit wide bill text comfortably
        setLocationRelativeTo(owner);

        // Root Container
        JPanel rootPanel = new JPanel(new BorderLayout(0, 10));
        rootPanel.setBackground(MainFrame.BG_DARK);
        rootPanel.setBorder(new EmptyBorder(16, 16, 16, 16));

        // Center Content Box (Alerts + Receipt)
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        // Kitchen Change Alerts Banner
        if (!processor.getChangeAlerts().isEmpty()) {
            JTextArea alerts = new JTextArea();
            alerts.setEditable(false);
            alerts.setLineWrap(true);
            alerts.setWrapStyleWord(true);
            alerts.setOpaque(false);
            alerts.setForeground(new Color(255, 120, 120));
            alerts.setFont(new Font("Segoe UI", Font.BOLD, 12));

            StringBuilder sb = new StringBuilder("KITCHEN DISPLAY ALERTS:\n");
            for (String a : processor.getChangeAlerts()) {
                sb.append("• ").append(a).append("\n");
            }
            alerts.setText(sb.toString().trim());

            JPanel alertCard = new JPanel(new BorderLayout());
            alertCard.setBackground(new Color(60, 20, 20));
            alertCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(180, 60, 60), 1),
                    new EmptyBorder(10, 12, 10, 12)
            ));
            alertCard.add(alerts, BorderLayout.CENTER);

            // Wrap alerts in a scroll pane with a capped height so long text won't push the dialog
            JScrollPane alertScroll = new JScrollPane(alertCard);
            alertScroll.getViewport().setBackground(MainFrame.BG_DARK);
            alertScroll.setBorder(null);
            alertScroll.setPreferredSize(new Dimension(0, 95));
            alertScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
            alertScroll.setAlignmentX(Component.LEFT_ALIGNMENT);

            centerPanel.add(alertScroll);
            centerPanel.add(Box.createVerticalStrut(10));
        }

        // Receipt Summary Text Area
        JTextArea area = new JTextArea(receipt.generateSummary());
        area.setEditable(false);
        area.setMargin(new Insets(10, 10, 10, 10));
        area.setFont(new Font("Consolas", Font.PLAIN, 12));
        area.setBackground(MainFrame.PANEL_BG);
        area.setForeground(MainFrame.TEXT_LIGHT);
        area.setCaretColor(MainFrame.TEXT_LIGHT);

        JScrollPane scrollPane = new JScrollPane(area);
        scrollPane.getViewport().setBackground(MainFrame.PANEL_BG);
        scrollPane.setBorder(BorderFactory.createLineBorder(MainFrame.BORDER_COLOR, 1));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        centerPanel.add(scrollPane);
        rootPanel.add(centerPanel, BorderLayout.CENTER);

        // Action Buttons Row (Always visible at bottom)
        JButton closeBtn = createCustomButton(
                "Close",
                MainFrame.ACCENT_BLUE,
                MainFrame.ACCENT_BLUE.brighter(),
                Color.WHITE,
                new Dimension(120, 36),
                13
        );
        closeBtn.addActionListener(e -> dispose());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.setOpaque(false);
        bottom.add(closeBtn);
        rootPanel.add(bottom, BorderLayout.SOUTH);

        setContentPane(rootPanel);
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
}