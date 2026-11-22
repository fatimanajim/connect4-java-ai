package connect4.ui;

import connect4.core.Connect4Position;

/**
 * Vue console du jeu du Puissance 4.
 * Gère l'affichage stylisé de la grille, des bannières, menus et messages.
 * 
 * @author Profil 3 & Intégration
 */
public class ConsoleView {

    public static void printBanner() {
        System.out.println("===============================================================");
        System.out.println("        PUISSANCE 4 (CONNECT 4) - INTELLIGENCE ARTIFICIELLE    ");
        System.out.println("           Devoir 2 - Enseignant : M'hamed AIT KBIR           ");
        System.out.println("           Algorithmes : Minimax & Alpha-Bêta (Diapo 19)       ");
        System.out.println("===============================================================");
    }

    public static void printMainMenu(String currentDifficulty, String currentStrategy) {
        System.out.println("\n--- MENU PRINCIPAL ---");
        System.out.println("1. Nouvelle partie : 2 Joueurs (Humain vs Humain)");
        System.out.println("2. Nouvelle partie : Solo (Humain vs Machine)");
        System.out.println("3. Reprendre une partie sauvegardée");
        System.out.println("4. Configuration [Complexité: " + currentDifficulty + " | Stratégie: " + currentStrategy + "]");
        System.out.println("5. Consulter les Règles du Jeu");
        System.out.println("6. Quitter l'application");
    }

    public static void printBoard(Connect4Position position) {
        System.out.println(position.toString());
    }

    public static void printRules() {
        System.out.println("\n====================== RÈGLES DU PUISSANCE 4 ======================");
        System.out.println("1. Grille verticale de 7 colonnes et 6 lignes.");
        System.out.println("2. Deux joueurs s'affrontent :");
        System.out.println("   - Les ROUGES ('R') jouent TOUJOURS en premier.");
        System.out.println("   - Les JAUNES ('J') jouent en second.");
        System.out.println("3. Gravité : Le pion descend jusqu'à la position libre la plus basse.");
        System.out.println("4. Victoire : 4 pions consécutifs (horizontal, vertical, diagonal).");
        System.out.println("5. Match nul : Grille pleine (42 pions) sans alignement de 4.");
        System.out.println("6. Aide : Vous disposez d'un nombre limité de jokers pour demander");
        System.out.println("   le meilleur coup à l'IA.");
        System.out.println("===================================================================\n");
    }
}
