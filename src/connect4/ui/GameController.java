package connect4.ui;

import connect4.ai.AIDifficulty;
import connect4.ai.Connect4Search;
import connect4.core.Connect4Move;
import connect4.core.Connect4Position;
import connect4.core.GameSearch;
import connect4.storage.GameSaveManager;
import connect4.storage.GameState;

import java.util.Scanner;

/**
 * ContrÃ´leur principal de la partie de Puissance 4.
 * Orchestre les tours de jeu, la saisie utilisateur, l'appel Ã  l'IA,
 * la gestion des erreurs, la sauvegarde et l'aide au joueur.
 * 
 * @author Groupe Devoir 2 - IA
 */
public class GameController {

    private final Scanner scanner;
    private final Connect4Search aiEngine;
    private final GameSaveManager saveManager;
    private HintManager hintManager;

    private Connect4Position currentPosition;
    private int currentPlayer; // 1 = RED (Rouge - commence), 2 = YELLOW (Jaune)
    private int gameMode;      // 1 = 2 Joueurs, 2 = Joueur vs Machine
    private AIDifficulty difficulty;

    public GameController(Scanner scanner) {
        this.scanner = scanner;
        this.aiEngine = new Connect4Search();
        this.saveManager = new GameSaveManager();
        this.hintManager = new HintManager();
    }

    /**
     * DÃ©marre une nouvelle partie ou restitue une partie existante.
     */
    public void start(int mode, AIDifficulty difficulty, GameState restoredState) {
        if (restoredState != null) {
            this.currentPosition = restoredState.getPosition();
            this.currentPlayer = restoredState.getCurrentPlayer();
            this.gameMode = restoredState.getGameMode();
            this.difficulty = restoredState.getDifficulty();
            this.hintManager = new HintManager(restoredState.getHintsRemaining());
            System.out.println("\n[OK] Partie restituee avec succes (Sauvegardee le " + restoredState.getFormattedDate() + ")");
        } else {
            this.currentPosition = new Connect4Position();
            this.currentPlayer = Connect4Position.RED; // Les Rouges commencent toujours
            this.gameMode = mode;
            this.difficulty = difficulty;
            this.hintManager = new HintManager(HintManager.DEFAULT_MAX_HINTS);
            System.out.println("\n--- Debut de la partie de Puissance 4 ---");
            System.out.println("Mode : " + (mode == 1 ? "2 Joueurs Humains" : "Humain (Rouge) vs Machine (Jaune)"));
            System.out.println("Complexite IA : " + difficulty.getLabel());
            System.out.println("Strategie : " + difficulty.getStrategy().getLabel());
            System.out.println("Regle : Les ROUGES ('R') jouent en premier !");
        }

        // Boucle principale de jeu
        runGameLoop();
    }

    private void runGameLoop() {
        boolean running = true;

        while (running) {
            System.out.println(currentPosition.toString());

            // 1. VÃ©rification des conditions de fin
            if (currentPosition.checkWin(Connect4Position.RED)) {
                System.out.println("ðŸŽ‰ VICTOIRE DES ROUGES ('R') ! FÃ©licitations !");
                break;
            }
            if (currentPosition.checkWin(Connect4Position.YELLOW)) {
                if (gameMode == GameState.MODE_HUMAN_VS_AI) {
                    System.out.println("ðŸ¤– La Machine ('J') a remportÃ© la partie !");
                } else {
                    System.out.println("ðŸŽ‰ VICTOIRE DES JAUNES ('J') ! FÃ©licitations !");
                }
                break;
            }
            if (currentPosition.isFull()) {
                System.out.println("ðŸ¤ MATCH NUL ! La grille est entiÃ¨rement remplie sans vainqueur.");
                break;
            }

            // 2. Gestion du tour
            if (gameMode == GameState.MODE_HUMAN_VS_AI && currentPlayer == Connect4Position.YELLOW) {
                // Tour de l'ordinateur
                playComputerTurn();
                currentPlayer = Connect4Position.RED;
            } else {
                // Tour du joueur humain
                boolean turnCompleted = playHumanTurn();
                if (!turnCompleted) {
                    // Le joueur a quittÃ© la partie en cours
                    return;
                }
                currentPlayer = (currentPlayer == Connect4Position.RED) ? Connect4Position.YELLOW : Connect4Position.RED;
            }
        }

        System.out.println("\nAppuyez sur EntrÃ©e pour retourner au menu principal...");
        scanner.nextLine();
    }

    /**
     * GÃ¨re la saisie d'un joueur humain avec gestion stricte des erreurs et commandes spÃ©ciales.
     * @return true si le coup a Ã©tÃ© jouÃ©, false si retour au menu demandÃ©
     */
    private boolean playHumanTurn() {
        String playerName = (currentPlayer == Connect4Position.RED) ? "Joueur 1 (ROUGE 'R')" : "Joueur 2 (JAUNE 'J')";
        
        while (true) {
            System.out.println("\n" + playerName + " Ã  vous de jouer !");
            System.out.println("[Options : Entrez 1-7 pour jouer | 'A' pour AIDE (" + hintManager.getRemainingHints() + " restantes) | 'S' pour SAUVEGARDER | 'Q' pour QUITTER]");
            System.out.print("Votre choix > ");
            
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("Q")) {
                System.out.print("Voulez-vous quitter vers le menu ? (O/N) > ");
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
                handleHint();
                continue;
            }

            // Traitement du numÃ©ro de colonne
            try {
                int colChoice = Integer.parseInt(input);
                int colIndex = colChoice - 1; // Conversion 1-based vers 0-based

                if (colIndex < 0 || colIndex >= Connect4Position.COLS) {
                    System.out.println("âš ï¸ ERREUR : La colonne doit Ãªtre comprise entre 1 et 7.");
                    continue;
                }

                if (!currentPosition.isValidMove(colIndex)) {
                    System.out.println("âš ï¸ ERREUR : La colonne " + colChoice + " est dÃ©jÃ  COMPLÃˆTE ! Choisissez une autre colonne libre.");
                    continue;
                }

                // Coup valide : application de la gravitÃ©
                currentPosition = currentPosition.playMove(colIndex, currentPlayer);
                return true;

            } catch (NumberFormatException e) {
                System.out.println("âš ï¸ ERREUR : Commande non reconnue. Entrez un numÃ©ro de colonne (1 Ã  7) ou 'A', 'S', 'Q'.");
            } catch (IllegalArgumentException e) {
                System.out.println("âš ï¸ ERREUR : " + e.getMessage());
            }
        }
    }

    /**
     * GÃ¨re le tour de l'intelligence artificielle.
     */
    private void playComputerTurn() {
        System.out.println("\nðŸ¤– Tour de la Machine (Jaune 'J')...");
        System.out.println("Calcul en cours avec l'algorithme : " + difficulty.getStrategy().getLabel() + " (Profondeur " + difficulty.getDepth() + ")...");

        long start = System.currentTimeMillis();
        boolean useAlphaBeta = (difficulty.getStrategy() == AIDifficulty.Strategy.ALPHABETA);
        Connect4Move bestMove = aiEngine.computeBestMove(currentPosition, GameSearch.PROGRAM, difficulty.getDepth(), useAlphaBeta);
        long duration = System.currentTimeMillis() - start;

        if (bestMove != null) {
            System.out.println("ðŸ¤– La Machine joue dans la " + bestMove + " (CalculÃ© en " + duration + " ms).");
            currentPosition = currentPosition.playMove(bestMove.getColumn(), Connect4Position.YELLOW);
        } else {
            // Repli de secours : premiÃ¨re colonne valide
            for (int c = 0; c < Connect4Position.COLS; c++) {
                if (currentPosition.isValidMove(c)) {
                    currentPosition = currentPosition.playMove(c, Connect4Position.YELLOW);
                    break;
                }
            }
        }
    }

    /**
     * GÃ¨re la demande d'aide avec dÃ©crÃ©mentation des jokers.
     */
    private void handleHint() {
        if (!hintManager.hasHints()) {
            System.out.println("âŒ Vous avez Ã©puisÃ© toutes vos aides disponibles pour cette partie (0 restante) !");
            return;
        }

        System.out.println("ðŸ’¡ Analyse tactique en cours pour vous conseiller...");
        Connect4Move hint = hintManager.requestHint(currentPosition, difficulty);

        if (hint != null) {
            System.out.println("ðŸ’¡ [CONSEIL IA] : Il est recommandÃ© de jouer dans la " + hint + " !");
            System.out.println("Il vous reste dÃ©sormais " + hintManager.getRemainingHints() + " aide(s) disponible(s).");
        } else {
            System.out.println("âŒ Impossible de trouver une recommandation valide.");
        }
    }

    /**
     * GÃ¨re la sauvegarde interactive de la partie en cours.
     */
    private void handleSave() {
        System.out.print("Entrez un nom pour votre sauvegarde (ex: partie1) > ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            name = "partie_" + System.currentTimeMillis();
        }

        GameState state = new GameState(currentPosition, currentPlayer, gameMode, difficulty, hintManager.getRemainingHints());
        boolean success = saveManager.save(state, name);

        if (success) {
            System.out.println("âœ“ Sauvegarde rÃ©ussie sous le nom : " + name + ".dat dans le dossier 'saves/'.");
        } else {
            System.out.println("âŒ Ã‰chec de la sauvegarde.");
        }
    }
}
