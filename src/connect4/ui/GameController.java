package connect4.ui;

import connect4.ai.AIDifficulty;
import connect4.ai.Connect4Search;
import connect4.core.Connect4Move;
import connect4.core.Connect4Position;
import connect4.core.IllegalMoveException;
import connect4.storage.GameSaveManager;
import connect4.storage.GameState;

import java.util.Scanner;

/**
 * Contrôleur principal orchestrant la partie de Puissance 4.
 * Gère la boucle de jeu, la gestion des erreurs (IllegalMoveException),
 * les tours de jeu, l'IA, la sauvegarde et l'aide au joueur.
 * 
 * @author Profil 3 (IHM) & Profil 2 (Persistance) & Profil 1 (IA)
 */
public class GameController {

    private final Scanner scanner;
    private final Connect4Search aiEngine;
    private HintManager hintManager;

    private Connect4Position currentPosition;
    private String gameMode;     // "HUMAIN_VS_HUMAIN" ou "HUMAIN_VS_IA"
    private String difficulty;   // "FACILE", "MOYEN", "DIFFICILE"
    private String strategy;     // "ALPHA_BETA", "MINIMAX"
    private int searchDepth;

    public GameController(Scanner scanner) {
        this.scanner = scanner;
        this.aiEngine = new Connect4Search();
        this.hintManager = new HintManager();
    }

    /**
     * Lance une partie (nouvelle ou restaurée).
     */
    public void start(String mode, AIDifficulty diffConfig, GameState restoredState) {
        if (restoredState != null) {
            this.currentPosition = restoredState.position;
            this.gameMode = restoredState.gameMode;
            this.difficulty = restoredState.difficulty;
            this.strategy = restoredState.strategy;
            this.hintManager = new HintManager(restoredState.hintsLeft);
            this.searchDepth = resolveDepth(this.difficulty);
            System.out.println("\n[OK] Partie restituee avec succes !");
            System.out.println("Mode : " + gameMode + " | Coups deja joues : " + currentPosition.nbMoves);
        } else {
            this.currentPosition = new Connect4Position();
            this.gameMode = mode;
            this.difficulty = diffConfig.name();
            this.strategy = (diffConfig.getStrategy() == AIDifficulty.Strategy.ALPHABETA) ? "ALPHA_BETA" : "MINIMAX";
            this.searchDepth = diffConfig.getDepth();
            this.hintManager = new HintManager(HintManager.DEFAULT_MAX_HINTS);

            System.out.println("\n--- Debut de la partie de Puissance 4 ---");
            System.out.println("Mode : " + (mode.equals("HUMAIN_VS_HUMAIN") ? "2 Joueurs Humains" : "Humain (Rouge) vs Machine (Jaune)"));
            System.out.println("Complexite IA : Profondeur " + searchDepth + " (" + difficulty + ")");
            System.out.println("Strategie : " + (strategy.equals("ALPHA_BETA") ? "Elagage Alpha-Beta (Diapo 19)" : "Minimax standard"));
            System.out.println("Regle : Les ROUGES ('R') jouent en premier !");
        }

        runGameLoop();
    }

    private void runGameLoop() {
        boolean usePruning = strategy.equals("ALPHA_BETA");

        while (!currentPosition.isGameOver()) {
            ConsoleView.printBoard(currentPosition);

            int currentPlayer = currentPosition.currentPlayer();
            boolean isMachineTurn = gameMode.equals("HUMAIN_VS_IA") && (currentPlayer == Connect4Position.YELLOW);

            if (isMachineTurn) {
                // Tour de la Machine
                playComputerTurn(usePruning);
            } else {
                // Tour du Joueur Humain
                boolean continueGame = playHumanTurn(usePruning);
                if (!continueGame) {
                    return; // Le joueur a quitté
                }
            }
        }

        // Fin de la partie
        ConsoleView.printBoard(currentPosition);
        if (currentPosition.hasWon(Connect4Position.RED)) {
            System.out.println("\n[VICTOIRE] Les ROUGES ('R') remportent la partie !");
        } else if (currentPosition.hasWon(Connect4Position.YELLOW)) {
            if (gameMode.equals("HUMAIN_VS_IA")) {
                System.out.println("\n[IA] La Machine ('J') a gagne la partie !");
            } else {
                System.out.println("\n[VICTOIRE] Les JAUNES ('J') remportent la partie !");
            }
        } else if (currentPosition.isFull()) {
            System.out.println("\n[EGALITE] Match nul ! La grille est pleine sans alignement de 4.");
        }

        System.out.println("\nAppuyez sur Entree pour revenir au menu principal...");
        scanner.nextLine();
    }

    private boolean playHumanTurn(boolean usePruning) {
        int player = currentPosition.currentPlayer();
        String playerName = (player == Connect4Position.RED) ? "Joueur 1 (ROUGE 'R')" : "Joueur 2 (JAUNE 'J')";

        while (true) {
            System.out.println("\n" + playerName + " a vous de jouer !");
            System.out.println("[Options : Entrez 1-7 | 'A' pour AIDE (" + hintManager.getHintsRemaining() + " restantes) | 'S' pour SAUVEGARDER | 'Q' pour QUITTER]");
            System.out.print("Votre choix > ");

            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("Q")) {
                System.out.print("Voulez-vous vraiment quitter vers le menu ? (O/N) > ");
                if (scanner.nextLine().trim().equalsIgnoreCase("O")) {
                    return false;
                }
                continue;
            }

            if (input.equalsIgnoreCase("S")) {
                handleSave();
                continue;
            }

            if (input.equalsIgnoreCase("A")) {
                handleHint(usePruning);
                continue;
            }

            // Tentative de jouer un coup avec gestion des erreurs
            try {
                int colChoice = Integer.parseInt(input);
                int colIndex = colChoice - 1; // 1-indexed to 0-indexed

                // drop() vérifie et applique la gravité, ou lève IllegalMoveException
                currentPosition.drop(colIndex);
                return true;

            } catch (NumberFormatException e) {
                System.out.println("[!] ERREUR : Saisie non reconnue. Entrez un numero de colonne (1 a 7) ou 'A', 'S', 'Q'.");
            } catch (IllegalMoveException e) {
                System.out.println("[!] ERREUR : " + e.getMessage());
            }
        }
    }

    private void playComputerTurn(boolean usePruning) {
        System.out.println("\n[IA] Reflexion de la Machine en cours (Profondeur " + searchDepth + ")...");
        long start = System.currentTimeMillis();

        Connect4Move best = aiEngine.computeBestMove(currentPosition, searchDepth, usePruning);
        long elapsed = System.currentTimeMillis() - start;

        if (best != null) {
            System.out.println("[IA] La Machine joue dans la " + best + " (Calcule en " + elapsed + " ms).");
            currentPosition.drop(best.column);
        } else {
            // Repli
            for (int c : currentPosition.legalMoves()) {
                currentPosition.drop(c);
                break;
            }
        }
    }

    private void handleHint(boolean usePruning) {
        if (!hintManager.hasHints()) {
            System.out.println("[X] Vous avez epuise vos 3 aides pour cette partie (0 restante) !");
            return;
        }

        System.out.println("[CONSEIL] Analyse de la position par l'IA...");
        Connect4Move hint = hintManager.requestHint(currentPosition, searchDepth, usePruning);

        if (hint != null) {
            System.out.println("[CONSEIL] L'IA vous recommande de jouer dans la " + hint + " !");
            System.out.println("Il vous reste desormais " + hintManager.getHintsRemaining() + " joker(s) d'aide.");
        } else {
            System.out.println("[X] Aucune recommandation disponible.");
        }
    }

    private void handleSave() {
        System.out.print("Entrez un nom pour votre sauvegarde (ex: partie1) > ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            name = "partie_" + System.currentTimeMillis();
        }

        GameState state = new GameState();
        state.position = currentPosition.copy();
        state.gameMode = gameMode;
        state.difficulty = difficulty;
        state.strategy = strategy;
        state.hintsLeft = hintManager.getHintsRemaining();
        state.humanColor = Connect4Position.RED;

        try {
            GameSaveManager.save(state, name);
            System.out.println("[OK] Partie sauvegardee avec succes sous le nom : '" + name + "' dans 'saves/'.");
        } catch (Exception e) {
            System.out.println("[X] Echec de la sauvegarde : " + e.getMessage());
        }
    }

    private int resolveDepth(String diff) {
        if ("FACILE".equalsIgnoreCase(diff)) return 2;
        if ("DIFFICILE".equalsIgnoreCase(diff)) return 6;
        return 4; // MOYEN
    }
}
