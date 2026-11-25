
package connect4.ui;

import connect4.ai.AIDifficulty;
import connect4.ai.Connect4Search;
import connect4.core.Connect4Move;
import connect4.core.Connect4Position;
import connect4.storage.GameSaveManager;
import connect4.storage.GameState;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class Connect4GUI extends JFrame {

    
    private static final Color BACKGROUND =
        new Color(12, 16, 32);

    private static final Color BOARD_COLOR =
        new Color(30, 100, 205);   

    private static final Color RED =
        new Color(255, 65, 90);

    private static final Color YELLOW =
        new Color(255, 205, 55);

    private static final Color EMPTY =
        new Color(225, 232, 245);


    private Connect4Position position;
    private Connect4Search ai;
    private HintManager hintManager;

    private String gameMode;
    private String strategy;
    private AIDifficulty difficulty;

    private JButton[][] cells;
    private JLabel statusLabel;
    private JLabel scoreLabel;
    private JLabel hintsLabel;

    private int scoreRed = 0;
    private int scoreYellow = 0;

    private boolean gameFinished = false;
    private boolean aiThinking = false;

    public Connect4GUI() {

        setTitle("QUATRE MASTERS - Le défi des 4 alignés");
        setSize(900, 750);
        setMinimumSize(new Dimension(750, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        ai = new Connect4Search();
        hintManager = new HintManager();

        difficulty = AIDifficulty.MOYEN;
        strategy = GameController.STRATEGY_ALPHA_BETA;

        showMainMenu();
    }

    // ==========================================
    // 1. MENU PRINCIPAL
    // ==========================================

    
private void showMainMenu() {

    getContentPane().removeAll();

    setSize(1100, 720);
    setLocationRelativeTo(null);

    // Fond principal
    JPanel mainPanel = new MenuBackgroundPanel();
    mainPanel.setLayout(new BorderLayout(20, 10));
    mainPanel.setBorder(
        BorderFactory.createEmptyBorder(25, 35, 20, 35)
    );

    // Titre
    
    JLabel title = new JLabel(
       "QUATRE MASTERS",
        SwingConstants.CENTER
    );

    title.setFont(new Font("Arial", Font.BOLD, 55));
    title.setForeground(new Color(255, 195, 45));

    JPanel titlePanel = new JPanel(
    new FlowLayout(FlowLayout.CENTER)
    );

    titlePanel.setOpaque(false);
    titlePanel.add(title);


    mainPanel.add(titlePanel, BorderLayout.NORTH);

    // Partie centrale : illustration + menu
    JPanel centerPanel = new JPanel(new GridLayout(1, 2, 35, 0));
    centerPanel.setOpaque(false);

    // Illustration du plateau
    BoardIllustration board = new BoardIllustration();
    centerPanel.add(board);

    // Menu à droite
    JPanel menuPanel = new JPanel();
    menuPanel.setOpaque(false);
    menuPanel.setLayout(
        new BoxLayout(menuPanel, BoxLayout.Y_AXIS)
    );

    menuPanel.add(Box.createVerticalGlue());

    addMenuButton(menuPanel, "Nouvelle partie",
            this::showConfiguration);

    addMenuButton(menuPanel, "Charger une partie",
            this::loadSavedGame);

    addMenuButton(menuPanel, "Configuration",
            this::showSettings);

    addMenuButton(menuPanel, "Comment jouer ?",
            this::showRules);

    addMenuButton(menuPanel, "Quitter",
            () -> System.exit(0));

    menuPanel.add(Box.createVerticalGlue());

    centerPanel.add(menuPanel);

    mainPanel.add(centerPanel, BorderLayout.CENTER);

    // Score en bas
    JLabel score = new JLabel(
        "SCORE : ROUGE " + scoreRed
        + "       |       JAUNE " + scoreYellow,
        SwingConstants.CENTER
    );

    score.setFont(new Font("Arial", Font.BOLD, 18));
    score.setForeground(Color.WHITE);

    mainPanel.add(score, BorderLayout.SOUTH);

    setContentPane(mainPanel);

    revalidate();
    repaint();
}

    

private void addMenuButton(
        JPanel panel,
        String text,
        Runnable action
) {

    JButton button = new JButton(text);

    Main.styleButton(button, new Color(48, 105, 220));

    button.setFont(
        new Font("Segoe UI", Font.BOLD, 18)
    );

    button.setPreferredSize(new Dimension(320, 62));
    button.setMaximumSize(new Dimension(320, 62));

    button.setAlignmentX(Component.CENTER_ALIGNMENT);

    button.setToolTipText("Cliquez pour " + text.toLowerCase());

    button.addActionListener(e -> action.run());

    panel.add(button);
    panel.add(Box.createVerticalStrut(18));
}


    // ==========================================
    // 2. CONFIGURATION DE LA PARTIE
    // ==========================================

    private void showConfiguration() {

        String[] modes = {
                "Humain contre Humain",
                "Humain contre IA"
        };

        String mode = (String) JOptionPane.showInputDialog(
                this,
                "Choisissez le mode de jeu :",
                "Mode de jeu",
                JOptionPane.QUESTION_MESSAGE,
                null,
                modes,
                modes[1]
        );

        if (mode == null) return;

        gameMode = mode.equals("Humain contre Humain")
                ? GameController.MODE_HUMAN_VS_HUMAN
                : GameController.MODE_HUMAN_VS_AI;

        if (gameMode.equals(GameController.MODE_HUMAN_VS_AI)) {

            String[] levels = {
                    "Facile - Profondeur 2",
                    "Moyen - Profondeur 4",
                    "Difficile - Profondeur 6"
            };

            String level = (String) JOptionPane.showInputDialog(
                    this,
                    "Choisissez la difficulté :",
                    "Difficulté",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    levels,
                    levels[1]
            );

            if (level == null) return;

            if (level.startsWith("Facile")) {
                difficulty = AIDifficulty.FACILE;
            } else if (level.startsWith("Moyen")) {
                difficulty = AIDifficulty.MOYEN;
            } else {
                difficulty = AIDifficulty.DIFFICILE;
            }

            chooseStrategy();
        }

        startNewGame();
    }

    private void chooseStrategy() {

        String[] strategies = {
                "Minimax",
                "Alpha-Bêta"
        };

        String selected = (String) JOptionPane.showInputDialog(
                this,
                "Choisissez l'algorithme :",
                "Intelligence artificielle",
                JOptionPane.QUESTION_MESSAGE,
                null,
                strategies,
                strategies[1]
        );

        if (selected == null) {
            strategy = difficulty.getStrategy()
                    == AIDifficulty.Strategy.ALPHABETA
                    ? GameController.STRATEGY_ALPHA_BETA
                    : GameController.STRATEGY_MINIMAX;
            return;
        }

        strategy = selected.equals("Minimax")
                ? GameController.STRATEGY_MINIMAX
                : GameController.STRATEGY_ALPHA_BETA;
    }

    private void showSettings() {

        String[] levels = {
                "Facile",
                "Moyen",
                "Difficile"
        };

        String selected = (String) JOptionPane.showInputDialog(
                this,
                "Choisissez la difficulté par défaut :",
                "Configuration",
                JOptionPane.QUESTION_MESSAGE,
                null,
                levels,
                "Moyen"
        );

        if (selected == null) return;

        switch (selected) {
            case "Facile":
                difficulty = AIDifficulty.FACILE;
                break;

            case "Moyen":
                difficulty = AIDifficulty.MOYEN;
                break;

            case "Difficile":
                difficulty = AIDifficulty.DIFFICILE;
                break;
        }

        strategy = difficulty.getStrategy()
                == AIDifficulty.Strategy.ALPHABETA
                ? GameController.STRATEGY_ALPHA_BETA
                : GameController.STRATEGY_MINIMAX;

        JOptionPane.showMessageDialog(
                this,
                "Configuration enregistrée !"
        );
    }

    // ==========================================
    // 3. DÉMARRAGE D'UNE PARTIE
    // ==========================================

    private void startNewGame() {

        position = new Connect4Position();
        hintManager = new HintManager();

        gameFinished = false;
        aiThinking = false;

        showGameBoard();
        updateBoard();
        updateStatus();

    }

    // ==========================================
    // 4. CONSTRUCTION DU PLATEAU GRAPHIQUE
    // ==========================================

    private void showGameBoard() {

        getContentPane().removeAll();

        JPanel main = new JPanel(new BorderLayout(15, 15));
        main.setBackground(BACKGROUND);
        main.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        
        JLabel title = new JLabel(
            "✦  QUATRE MASTERS  ✦",
            SwingConstants.CENTER
        );

        title.setFont(
           new Font("Segoe UI", Font.BOLD, 34)
        );

        title.setForeground(new Color(255, 205, 65));

        title.setBorder(
           BorderFactory.createEmptyBorder(5, 5, 18, 5)
        );

        main.add(title, BorderLayout.NORTH);


        main.add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(
                Connect4Position.ROWS,
                Connect4Position.COLS,
                8,
                8
        ));

        center.setBackground(BOARD_COLOR);
        center.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        cells = new JButton[Connect4Position.ROWS]
                             [Connect4Position.COLS];

        for (int row = 0; row < Connect4Position.ROWS; row++) {

            for (int col = 0; col < Connect4Position.COLS; col++) {

                JButton cell = new JButton();

                cell.setPreferredSize(new Dimension(75, 75));
                cell.setFont(new Font("Arial", Font.BOLD, 35));
                cell.setFocusPainted(false);
                cell.setBorderPainted(false);
                cell.setOpaque(true);

                final int selectedColumn = col;

                cell.addActionListener(e ->
                        playHumanMove(selectedColumn)
                );

                cells[row][col] = cell;
                center.add(cell);
            }
        }

        main.add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setBackground(BACKGROUND);

        statusLabel = new JLabel("Au tour du joueur rouge");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 18));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        scoreLabel = new JLabel();
        scoreLabel.setForeground(YELLOW);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 15));
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        hintsLabel = new JLabel();
        hintsLabel.setForeground(Color.WHITE);
        hintsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        bottom.add(statusLabel);
        bottom.add(Box.createVerticalStrut(8));
        bottom.add(scoreLabel);
        bottom.add(Box.createVerticalStrut(8));
        bottom.add(hintsLabel);
        bottom.add(Box.createVerticalStrut(15));

        JPanel actions = new JPanel(new FlowLayout());
        actions.setBackground(BACKGROUND);

        JButton hintButton = createActionButton(
                "Demander une aide",
                this::requestHint
        );

        JButton saveButton = createActionButton(
                "Sauvegarder",
                this::saveCurrentGame
        );

        JButton restartButton = createActionButton(
                "Recommencer",
                this::confirmRestart
        );

        JButton menuButton = createActionButton(
                "Menu principal",
                this::returnToMenu
        );

        actions.add(hintButton);
        actions.add(saveButton);
        actions.add(restartButton);
        actions.add(menuButton);

        bottom.add(actions);

        main.add(bottom, BorderLayout.SOUTH);

        setContentPane(main);

        revalidate();
        repaint();
    }

    
private JButton createActionButton(
        String text,
        Runnable action
) {

    JButton button = new JButton(text);

    Color buttonColor;

    switch (text) {

        case "Demander une aide":
            buttonColor = new Color(15, 165, 195);
            break;

        case "Sauvegarder":
            buttonColor = new Color(45, 110, 230);
            break;

        case "Recommencer":
            buttonColor = new Color(225, 145, 30);
            break;

        default:
            buttonColor = new Color(125, 75, 205);
            break;
    }

    Main.styleButton(button, buttonColor);

    button.setFont(
        new Font("Segoe UI", Font.BOLD, 13)
    );

    button.setPreferredSize(new Dimension(190, 46));

    button.addActionListener(e -> action.run());

    return button;
}


    // ==========================================
    // 5. ACTUALISATION DU PLATEAU
    // ==========================================

    private void updateBoard() {

        if (position == null || cells == null) return;

        for (int row = 0; row < Connect4Position.ROWS; row++) {

            for (int col = 0; col < Connect4Position.COLS; col++) {

                int value = position.get(row, col);

                JButton cell = cells[row][col];

                cell.setText("");

                if (value == Connect4Position.RED) {

                    cell.setBackground(RED);
                    cell.setText("●");
                    cell.setForeground(Color.WHITE);

                } else if (value == Connect4Position.YELLOW) {

                    cell.setBackground(YELLOW);
                    cell.setText("●");
                    cell.setForeground(Color.WHITE);

                } else {

                    cell.setBackground(EMPTY);
                }

                cell.setEnabled(
                        !gameFinished
                        && !aiThinking
                        && !position.isGameOver()
                        && position.isLegal(col)
                        && !isComputerTurn()
                );
            }
        }

        if (hintsLabel != null) {
            hintsLabel.setText(
                    "Jokers disponibles : "
                    + hintManager.getHintsRemaining()
                    + " / 3"
            );
        }

        if (scoreLabel != null) {
            scoreLabel.setText(
                    "Score : Rouge " + scoreRed
                    + "  -  Jaune " + scoreYellow
            );
        }
    }

    private void updateStatus() {

        if (position == null || statusLabel == null) return;

        if (gameFinished) return;

        int player = position.currentPlayer();

        String name = player == Connect4Position.RED
                ? "Rouge"
                : "Jaune";

        if (isComputerTurn()) {
            statusLabel.setText("L'intelligence artificielle réfléchit...");
        } else {
            statusLabel.setText("Au tour du joueur " + name);
        }
    }

    // ==========================================
    // 6. GESTION DES COUPS HUMAINS
    // ==========================================

    private void playHumanMove(int column) {

        if (gameFinished || aiThinking || isComputerTurn()) {
            return;
        }

        if (!position.isLegal(column)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Cette colonne est pleine !"
            );
            return;
        }

        position.drop(column);

        updateBoard();

        if (checkGameResult()) return;

        updateStatus();

        if (isComputerTurn()) {
            playComputerMove();
        }
    }

    // ==========================================
    // 7. TOUR DE L'INTELLIGENCE ARTIFICIELLE
    // ==========================================

    private boolean isComputerTurn() {

        return gameMode != null
                && gameMode.equals(GameController.MODE_HUMAN_VS_AI)
                && position != null
                && position.currentPlayer() == Connect4Position.YELLOW;
    }

    private void playComputerMove() {

        if (gameFinished || !isComputerTurn()) return;

        aiThinking = true;

        updateBoard();
        updateStatus();

        final Connect4Position current = position.copy();

        final int depth = difficulty.getDepth();

        final boolean useAlphaBeta =
                strategy.equals(GameController.STRATEGY_ALPHA_BETA);

        new SwingWorker<Connect4Move, Void>() {

            @Override
            protected Connect4Move doInBackground() {

                return ai.computeBestMove(
                        current,
                        depth,
                        useAlphaBeta
                );
            }

            @Override
            protected void done() {

                aiThinking = false;

                try {

                    Connect4Move move = get();

                    if (move != null && !gameFinished) {

                        position.drop(move.column);
                    }

                    updateBoard();

                    if (!checkGameResult()) {
                        updateStatus();
                    }

                } catch (Exception e) {

                    JOptionPane.showMessageDialog(
                            Connect4GUI.this,
                            "Erreur pendant le calcul de l'IA : "
                                    + e.getMessage()
                    );

                    updateBoard();
                }
            }

        }.execute();
    }

    // ==========================================
    // 8. SYSTÈME D'AIDE / JOKERS
    // ==========================================

    private void requestHint() {

        if (gameFinished || aiThinking || isComputerTurn()) {
            return;
        }

        if (!hintManager.hasHints()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Vous n'avez plus de jokers !"
            );

            return;
        }

        aiThinking = true;
        updateBoard();

        final Connect4Position current = position.copy();

        final boolean useAlphaBeta =
                strategy.equals(GameController.STRATEGY_ALPHA_BETA);

        new SwingWorker<Connect4Move, Void>() {

            @Override
            protected Connect4Move doInBackground() {

                return hintManager.requestHint(
                        current,
                        difficulty.getDepth(),
                        useAlphaBeta
                );
            }

            @Override
            protected void done() {

                aiThinking = false;

                try {

                    Connect4Move move = get();

                    updateBoard();

                    if (move != null) {

                        JOptionPane.showMessageDialog(
                                Connect4GUI.this,
                                "Conseil de l'IA : jouez dans la colonne "
                                        + (move.column + 1)
                                        + "\nJokers restants : "
                                        + hintManager.getHintsRemaining()
                        );

                    } else {

                        JOptionPane.showMessageDialog(
                                Connect4GUI.this,
                                "Aucun conseil disponible."
                        );
                    }

                } catch (Exception e) {

                    JOptionPane.showMessageDialog(
                            Connect4GUI.this,
                            "Erreur lors du calcul de l'aide."
                    );

                    updateBoard();
                }
            }

        }.execute();
    }

    // ==========================================
    // 9. VÉRIFICATION DE LA FIN DE PARTIE
    // ==========================================

    private boolean checkGameResult() {

        int winner = position.winner();

        if (winner == Connect4Position.RED) {

            gameFinished = true;
            scoreRed++;

            updateBoard();

            JOptionPane.showMessageDialog(
                    this,
                    "Félicitations ! Le joueur ROUGE a gagné !"
            );

        } else if (winner == Connect4Position.YELLOW) {

            gameFinished = true;
            scoreYellow++;

            updateBoard();

            String message = isComputerMode()
                    ? "L'intelligence artificielle a gagné !"
                    : "Le joueur JAUNE a gagné !";

            JOptionPane.showMessageDialog(this, message);

        } else if (position.isFull()) {

            gameFinished = true;

            updateBoard();

            JOptionPane.showMessageDialog(
                    this,
                    "Match nul ! La grille est pleine."
            );

        } else {

            return false;
        }

        statusLabel.setText("Partie terminée !");
        updateBoard();

        return true;
    }

    private boolean isComputerMode() {

        return GameController.MODE_HUMAN_VS_AI.equals(gameMode);
    }

    // ==========================================
    // 10. SAUVEGARDE
    // ==========================================

    private void saveCurrentGame() {

        if (position == null || gameFinished || aiThinking) {
            return;
        }

        String name = JOptionPane.showInputDialog(
                this,
                "Nom de la sauvegarde :"
        );

        if (name == null || name.trim().isEmpty()) return;

        GameState state = new GameState(
                position.copy(),
                gameMode,
                difficulty.name(),
                strategy,
                hintManager.getHintsRemaining()
        );

        state.scoreRed = scoreRed;
        state.scoreYellow = scoreYellow;

        try {

            GameSaveManager.save(state, name.trim());

            JOptionPane.showMessageDialog(
                    this,
                    "Partie sauvegardée avec succès !"
            );

        } catch (IOException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Erreur de sauvegarde : " + e.getMessage()
            );
        }
    }

    // ==========================================
    // 11. CHARGEMENT D'UNE PARTIE
    // ==========================================

    private void loadSavedGame() {

        List<String> saves = GameSaveManager.listSaves();

        if (saves.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Aucune sauvegarde disponible."
            );

            return;
        }

        String selected = (String) JOptionPane.showInputDialog(
                this,
                "Choisissez une partie :",
                "Charger une partie",
                JOptionPane.QUESTION_MESSAGE,
                null,
                saves.toArray(),
                saves.get(0)
        );

        if (selected == null) return;

        try {

            GameState state = GameSaveManager.load(selected);

            position = state.position;
            gameMode = state.gameMode;
            difficulty = AIDifficulty.valueOf(state.difficulty);
            strategy = state.strategy;

            hintManager = new HintManager(state.hintsLeft);

            scoreRed = state.scoreRed;
            scoreYellow = state.scoreYellow;

            gameFinished = position.isGameOver();
            aiThinking = false;

            showGameBoard();
            updateBoard();

            if (gameFinished) {
                statusLabel.setText("Partie déjà terminée !");
            } else {
                updateStatus();

                if (isComputerTurn()) {
                    playComputerMove();
                }
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Impossible de charger la partie : "
                            + e.getMessage()
            );
        }
    }

    // ==========================================
    // 12. RECOMMENCER / RETOUR MENU
    // ==========================================

    private void confirmRestart() {

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Voulez-vous recommencer la partie ?",
                "Nouvelle partie",
                JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            startNewGame();
        }
    }

    private void returnToMenu() {

        if (aiThinking) return;

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Voulez-vous revenir au menu principal ?",
                "Retour",
                JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            showMainMenu();
        }
    }

    // ==========================================
    // 13. RÈGLES DU JEU
    // ==========================================

    private void showRules() {

        String rules =
                "RÈGLES DU PUISSANCE 4\n\n"
                + "1. Le jeu contient 6 lignes et 7 colonnes.\n"
                + "2. Le joueur rouge commence.\n"
                + "3. Chaque joueur dépose un pion dans une colonne.\n"
                + "4. Les pions tombent vers le bas.\n"
                + "5. Le premier joueur qui aligne 4 pions gagne.\n"
                + "6. L'alignement peut être horizontal, vertical ou diagonal.\n"
                + "7. Trois jokers sont disponibles pour demander une aide.\n";

        JOptionPane.showMessageDialog(
                this,
                rules,
                "Règles du jeu",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

private class BoardIllustration extends JPanel {

    public BoardIllustration() {
        setOpaque(false);
        setPreferredSize(new Dimension(500, 480));
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        int boardX = 35;
        int boardY = 100;
        int boardWidth = Math.min(440, getWidth() - 60);
        int boardHeight = 355;

        // Ombre du plateau
        g2.setColor(new Color(0, 0, 0, 90));
        g2.fillRoundRect(
            boardX + 12, boardY + 15,
            boardWidth, boardHeight, 25, 25
        );

        // Plateau bleu
        g2.setColor(new Color(25, 115, 245));
        g2.fillRoundRect(
            boardX, boardY,
            boardWidth, boardHeight, 25, 25
        );

        int rows = 6;
        int cols = 7;

        int cellWidth = boardWidth / cols;
        int cellHeight = boardHeight / rows;

        // Trous du plateau
        for (int row = 0; row < rows; row++) {

            for (int col = 0; col < cols; col++) {

                int diameter = Math.min(cellWidth, cellHeight) - 15;

                int x = boardX + col * cellWidth
                        + (cellWidth - diameter) / 2;

                int y = boardY + row * cellHeight
                        + (cellHeight - diameter) / 2;

                g2.setColor(new Color(15, 25, 55));

                g2.fillOval(x, y, diameter, diameter);

                // Reflet
                g2.setColor(new Color(80, 160, 255, 100));
                g2.drawOval(x, y, diameter, diameter);
            }
        }

        // Quelques pions décoratifs
        drawToken(g2, boardX, boardY, cellWidth,
                cellHeight, 0, 5, RED);

        drawToken(g2, boardX, boardY, cellWidth,
                cellHeight, 1, 5, YELLOW);

        drawToken(g2, boardX, boardY, cellWidth,
                cellHeight, 2, 5, RED);

        drawToken(g2, boardX, boardY, cellWidth,
                cellHeight, 3, 4, YELLOW);

        drawToken(g2, boardX, boardY, cellWidth,
                cellHeight, 3, 5, RED);

        drawToken(g2, boardX, boardY, cellWidth,
                cellHeight, 4, 5, YELLOW);

        drawToken(g2, boardX, boardY, cellWidth,
                cellHeight, 5, 5, RED);

        g2.dispose();
    }

    private void drawToken(
            Graphics2D g2,
            int boardX,
            int boardY,
            int cellWidth,
            int cellHeight,
            int col,
            int row,
            Color color) {

        int diameter = Math.min(cellWidth, cellHeight) - 17;

        int x = boardX + col * cellWidth
                + (cellWidth - diameter) / 2;

        int y = boardY + row * cellHeight
                + (cellHeight - diameter) / 2;

        g2.setColor(color.darker());
        g2.fillOval(x + 3, y + 4, diameter, diameter);

        g2.setColor(color);
        g2.fillOval(x, y, diameter, diameter);
    }
}

private static class MenuBackgroundPanel extends JPanel {

    public MenuBackgroundPanel() {
        setOpaque(true);
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        
GradientPaint gradient = new GradientPaint(
        0, 0, new Color(8, 12, 30),
        getWidth(), getHeight(), new Color(55, 35, 105)
);

g2.setPaint(gradient);
g2.fillRect(0, 0, getWidth(), getHeight());

// Lumière décorative
g2.setColor(new Color(40, 130, 255, 25));
g2.fillOval(-100, -100, 350, 350);

g2.setColor(new Color(170, 70, 255, 20));
g2.fillOval(
        getWidth() - 300,
        getHeight() - 250,
        400, 400
);

    }
}
private void styliserBouton(JButton bouton, Color couleur) {
    bouton.setFont(new Font("Segoe UI", Font.BOLD, 14));
    bouton.setForeground(Color.WHITE);
    bouton.setBackground(couleur);
    bouton.setOpaque(true);
    bouton.setContentAreaFilled(true);
    bouton.setBorderPainted(false);
    bouton.setFocusPainted(false);
    bouton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    bouton.setPreferredSize(new Dimension(190, 43));

    bouton.setUI(new javax.swing.plaf.basic.BasicButtonUI());

    Color couleurInitiale = couleur;

    bouton.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseEntered(java.awt.event.MouseEvent e) {
            bouton.setBackground(couleurInitiale.brighter());
        }

        @Override
        public void mouseExited(java.awt.event.MouseEvent e) {
            bouton.setBackground(couleurInitiale);
        }
    });
}

}
