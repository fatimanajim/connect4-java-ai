package connect4.ui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class Main {

    // ==========================================
    // PALETTE GAMING PREMIUM
    // ==========================================

    public static final Color BACKGROUND =
            new Color(12, 16, 32);

    public static final Color PANEL =
            new Color(23, 31, 55);

    public static final Color BLUE =
            new Color(35, 145, 255);

    public static final Color PURPLE =
            new Color(130, 75, 245);

    public static final Color RED =
            new Color(255, 65, 90);

    public static final Color YELLOW =
            new Color(255, 205, 55);

    public static final Color WHITE =
            new Color(245, 248, 255);

    public static final Color TEXT_SECONDARY =
            new Color(165, 180, 210);

    public static final Font TITLE_FONT =
            new Font("Segoe UI", Font.BOLD, 28);

    public static final Font TEXT_FONT =
            new Font("Segoe UI", Font.PLAIN, 15);

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            try {
                UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception e) {
                e.printStackTrace();
            }

            configurePremiumStyle();

            Connect4GUI gui = new Connect4GUI();

            gui.setVisible(true);
        });
    }

    // ==========================================
    // STYLE GLOBAL DES FENÊTRES
    // ==========================================

    private static void configurePremiumStyle() {

        // Fenêtres de dialogue
        UIManager.put("OptionPane.background", BACKGROUND);
        UIManager.put("Panel.background", BACKGROUND);
        UIManager.put("OptionPane.messageForeground", WHITE);
        UIManager.put("OptionPane.messageFont",
                new Font("Segoe UI", Font.BOLD, 16));

        UIManager.put("OptionPane.border",
                BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(
                        new Color(70, 95, 155), 1
                    ),
                    BorderFactory.createEmptyBorder(
                        18, 20, 18, 20
                    )
                )
        );

        // Boutons des fenêtres
        
// ==========================================
// BOUTONS DES FENÊTRES DE DIALOGUE
// ==========================================

UIManager.put("Button.background", new Color(35, 145, 255));

// IMPORTANT : texte sombre pour rester lisible
// même si Windows impose un fond blanc.
UIManager.put("Button.foreground", new Color(15, 25, 50));

UIManager.put("Button.font",
        new Font("Segoe UI", Font.BOLD, 14));

UIManager.put("Button.opaque", true);
UIManager.put("Button.contentAreaFilled", true);
UIManager.put("Button.focusPainted", false);
UIManager.put("Button.borderPainted", true);

UIManager.put("Button.border",
        BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(
                new Color(35, 145, 255), 1
            ),
            BorderFactory.createEmptyBorder(
                10, 22, 10, 22
            )
        )
);


        // Champs de saisie
        UIManager.put("TextField.background", PANEL);
        UIManager.put("TextField.foreground", WHITE);
        UIManager.put("TextField.caretForeground", BLUE);
        UIManager.put("TextField.selectionBackground", BLUE);
        UIManager.put("TextField.selectionForeground", WHITE);

        UIManager.put("TextField.font", TEXT_FONT);

        UIManager.put("TextField.border",
                BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(
                        new Color(65, 95, 155), 1
                    ),
                    BorderFactory.createEmptyBorder(
                        8, 10, 8, 10
                    )
                )
        );

        // Listes déroulantes
        
// ==========================================
// LISTES DÉROULANTES
// ==========================================

UIManager.put("ComboBox.background", Color.WHITE);

UIManager.put("ComboBox.foreground",
        new Color(15, 25, 50));

UIManager.put("ComboBox.font",
        new Font("Segoe UI", Font.BOLD, 14));

UIManager.put("ComboBox.selectionBackground",
        new Color(35, 145, 255));

UIManager.put("ComboBox.selectionForeground",
        Color.WHITE);


        // Listes
        UIManager.put("List.background", PANEL);
        UIManager.put("List.foreground", WHITE);
        UIManager.put("List.selectionBackground", BLUE);
        UIManager.put("List.selectionForeground", WHITE);
        UIManager.put("List.font", TEXT_FONT);

        // Tooltips
        UIManager.put("ToolTip.background",
                new Color(35, 45, 75));

        UIManager.put("ToolTip.foreground", WHITE);
        UIManager.put("ToolTip.font",
                new Font("Segoe UI", Font.PLAIN, 13));

        // Couleurs des menus
        UIManager.put("Menu.background", PANEL);
        UIManager.put("Menu.foreground", WHITE);

        UIManager.put("MenuItem.background", PANEL);
        UIManager.put("MenuItem.foreground", WHITE);
        UIManager.put("MenuItem.selectionBackground", BLUE);
        UIManager.put("MenuItem.selectionForeground", WHITE);
    }

    // ==========================================
    // OUTIL DE STYLISATION DES BOUTONS
    // ==========================================

    public static void styleButton(
            JButton button,
            Color baseColor
    ) {

        button.setFont(
                new Font("Segoe UI", Font.BOLD, 14)
        );

        button.setForeground(WHITE);
        button.setBackground(baseColor);

        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);

        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        button.setBorder(
                BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(
                        baseColor.brighter(), 1
                    ),
                    BorderFactory.createEmptyBorder(
                        10, 20, 10, 20
                    )
                )
        );

        Color original = baseColor;

        button.addMouseListener(
            new java.awt.event.MouseAdapter() {

                @Override
                public void mouseEntered(
                        java.awt.event.MouseEvent e
                ) {
                    button.setBackground(original.brighter());
                }

                @Override
                public void mouseExited(
                        java.awt.event.MouseEvent e
                ) {
                    button.setBackground(original);
                }
            }
        );
    }
}
