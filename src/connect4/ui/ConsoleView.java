package connect4.ui;

import connect4.core.Connect4Position;

/**
 * Vue console du jeu du Puissance 4.
 * <p>
 * Gère l'affichage de la grille (avec couleurs ANSI optionnelles), des bannières,
 * des menus et des messages normalisés (succès, information, erreur).
 * Cette classe ne contient aucune règle de jeu : elle se contente d'afficher
 * l'état fourni par le moteur ({@link Connect4Position}).
 * </p>
 * <p>
 * Les messages sont volontairement écrits sans accents afin de s'afficher
 * correctement dans tous les terminaux Windows (code page 850/437).
 * </p>
 *
 * @author Profil 3 - Interface, Intégration & Rapport
 */
public class ConsoleView {

    /** Séquences ANSI de couleur (reconnues par VS Code, Windows Terminal, Linux, macOS). */
    private static final String RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[1;31m";
    private static final String ANSI_YELLOW = "\u001B[1;33m";
    private static final String ANSI_BLUE = "\u001B[1;34m";
    private static final String ANSI_GREEN = "\u001B[1;32m";
    private static final String ANSI_GRAY = "\u001B[90m";

    /** Indique si les couleurs ANSI sont utilisées (désactivable depuis le menu Configuration). */
    private static boolean colorEnabled = true;

    private ConsoleView() {
        // Classe utilitaire : pas d'instance.
    }

    /**
     * Active ou désactive l'affichage en couleurs.
     *
     * @param enabled {@code true} pour utiliser les couleurs ANSI
     */
    public static void setColorEnabled(boolean enabled) {
        colorEnabled = enabled;
    }

    /**
     * @return {@code true} si l'affichage en couleurs est actif
     */
    public static boolean isColorEnabled() {
        return colorEnabled;
    }

    /**
     * Entoure un texte d'une couleur ANSI si les couleurs sont activées.
     *
     * @param text texte à colorer
     * @param ansi code ANSI de la couleur
     * @return le texte coloré, ou le texte inchangé si les couleurs sont désactivées
     */
    private static String paint(String text, String ansi) {
        return colorEnabled ? ansi + text + RESET : text;
    }

    /** Affiche la bannière de l'application. */
    public static void printBanner() {
        System.out.println("===============================================================");
        System.out.println("        PUISSANCE 4 (CONNECT 4) - INTELLIGENCE ARTIFICIELLE    ");
        System.out.println("           Devoir 2 - Enseignant : M'hamed AIT KBIR            ");
        System.out.println("           Algorithmes : Minimax & Alpha-Beta (Diapo 19)       ");
        System.out.println("===============================================================");
    }

    /**
     * Affiche le menu principal avec les réglages courants et le score de la session.
     *
     * @param currentDifficulty nom du niveau de complexité courant (FACILE, MOYEN, DIFFICILE)
     * @param currentStrategy   stratégie courante (ALPHA_BETA ou MINIMAX)
     * @param scoreRed          victoires cumulées des Rouges dans la session
     * @param scoreYellow       victoires cumulées des Jaunes dans la session
     */
    public static void printMainMenu(String currentDifficulty, String currentStrategy,
                                     int scoreRed, int scoreYellow) {
        System.out.println("\n--- MENU PRINCIPAL ---");
        System.out.println("1. Nouvelle partie : 2 Joueurs (Humain vs Humain)");
        System.out.println("2. Nouvelle partie : Solo (Humain vs Machine)");
        System.out.println("3. Reprendre une partie sauvegardee");
        System.out.println("4. Configuration [Complexite: " + currentDifficulty + " | Strategie: " + currentStrategy + "]");
        System.out.println("5. Consulter les Regles du Jeu");
        System.out.println("6. Quitter l'application");
        System.out.println("Score de la session : Rouge " + scoreRed + " - " + scoreYellow + " Jaune");
    }

    /**
     * Affiche la grille 7x6 : numéros de colonnes (1 à 7), cadre, pions R (rouge) et J (jaune).
     * La ligne 0 du tableau {@code board} est la ligne du haut.
     *
     * @param position position à afficher
     */
    public static void printBoard(Connect4Position position) {
        int rows = position.board.length;
        int cols = position.board[0].length;

        StringBuilder sb = new StringBuilder("\n");

        // En-tête : numéros de colonnes alignés sur le centre des cases.
        for (int c = 1; c <= cols; c++) {
            sb.append("  ").append(paint(String.valueOf(c), ANSI_BLUE)).append(" ");
        }
        sb.append("\n");

        String separator = buildSeparator(cols);
        sb.append(separator).append("\n");

        for (int r = 0; r < rows; r++) {
            sb.append("|");
            for (int c = 0; c < cols; c++) {
                sb.append(" ").append(cellSymbol(position.board[r][c])).append(" |");
            }
            sb.append("\n").append(separator).append("\n");
        }
        System.out.print(sb);
    }

    /**
     * Construit la ligne de séparation horizontale de la grille.
     *
     * @param cols nombre de colonnes
     * @return une chaîne du type {@code +---+---+...+}
     */
    private static String buildSeparator(int cols) {
        StringBuilder sb = new StringBuilder("+");
        for (int c = 0; c < cols; c++) {
            sb.append("---+");
        }
        return sb.toString();
    }

    /**
     * Symbole d'une case : R (rouge), J (jaune) ou point (case vide).
     *
     * @param value contenu de la case (EMPTY, RED ou YELLOW)
     * @return le symbole, coloré si les couleurs sont activées
     */
    private static String cellSymbol(int value) {
        if (value == Connect4Position.RED) {
            return paint("R", ANSI_RED);
        }
        if (value == Connect4Position.YELLOW) {
            return paint("J", ANSI_YELLOW);
        }
        return paint(".", ANSI_GRAY);
    }

    /**
     * Affiche la ligne d'état affichée avant chaque coup.
     *
     * @param nbMoves        nombre de pions déjà posés
     * @param hintsRemaining jokers d'aide restants
     * @param scoreRed       score cumulé des Rouges
     * @param scoreYellow    score cumulé des Jaunes
     */
    public static void printStatus(int nbMoves, int hintsRemaining, int scoreRed, int scoreYellow) {
        System.out.println("Coups joues : " + nbMoves + " | Aides restantes : " + hintsRemaining
                + " | Score : Rouge " + scoreRed + " - " + scoreYellow + " Jaune");
    }

    /** Affiche les règles du jeu. */
    public static void printRules() {
        System.out.println("\n====================== REGLES DU PUISSANCE 4 ======================");
        System.out.println("1. Grille verticale de 7 colonnes et 6 lignes.");
        System.out.println("2. Deux joueurs s'affrontent :");
        System.out.println("   - Les ROUGES ('R') jouent TOUJOURS en premier.");
        System.out.println("   - Les JAUNES ('J') jouent en second.");
        System.out.println("3. Gravite : le pion descend jusqu'a la position libre la plus basse.");
        System.out.println("4. Victoire : 4 pions consecutifs (horizontal, vertical, diagonal).");
        System.out.println("5. Match nul : grille pleine (42 pions) sans alignement de 4.");
        System.out.println("6. Aide : vous disposez de 3 jokers pour demander le meilleur coup a l'IA");
        System.out.println("   (touche 'A' pendant la partie).");
        System.out.println("7. Pendant la partie : 1-7 = jouer | A = aide | S = sauvegarder | Q = quitter.");
        System.out.println("===================================================================\n");
    }

    /**
     * Affiche un message d'erreur normalisé (préfixe {@code [!] ERREUR}).
     *
     * @param message texte de l'erreur
     */
    public static void printError(String message) {
        System.out.println(paint("[!] ERREUR : " + message, ANSI_RED));
    }

    /**
     * Affiche un message de succès normalisé (préfixe {@code [OK]}).
     *
     * @param message texte du message
     */
    public static void printSuccess(String message) {
        System.out.println(paint("[OK] " + message, ANSI_GREEN));
    }

    /**
     * Affiche un message d'information normalisé (préfixe {@code [i]}).
     *
     * @param message texte du message
     */
    public static void printInfo(String message) {
        System.out.println("[i] " + message);
    }
}
