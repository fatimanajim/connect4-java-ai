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
 * Contrôleur principal orchestrant une partie de Puissance 4.
 * <p>
 * Il gère la boucle de jeu, l'alternance des tours (humain / machine), les erreurs
 * de saisie ({@link IllegalMoveException}, saisie non numérique), l'aide au joueur
 * (jokers), la sauvegarde de la partie et le score cumulé de la session.
 * Les règles du jeu restent entièrement dans le moteur ({@link Connect4Position}).
 * </p>
 *
 * @author Profil 3 (IHM) & Profil 2 (Persistance) & Profil 1 (IA)
 */
public class GameController {

    /** Mode de jeu : deux joueurs humains. */
    public static final String MODE_HUMAN_VS_HUMAN = "HUMAIN_VS_HUMAIN";
    /** Mode de jeu : humain (Rouge) contre la machine (Jaune). */
    public static final String MODE_HUMAN_VS_AI = "HUMAIN_VS_IA";
    /** Valeur de stratégie : élagage Alpha-Bêta (diapositive 19). */
    public static final String STRATEGY_ALPHA_BETA = "ALPHA_BETA";
    /** Valeur de stratégie : Minimax sans élagage. */
    public static final String STRATEGY_MINIMAX = "MINIMAX";

    private final Scanner scanner;
    private final Connect4Search aiEngine;
    private HintManager hintManager;

    private Connect4Position currentPosition;
    private String gameMode;     // MODE_HUMAN_VS_HUMAN ou MODE_HUMAN_VS_AI
    private String difficulty;   // "FACILE", "MOYEN", "DIFFICILE"
    private String strategy;     // STRATEGY_ALPHA_BETA ou STRATEGY_MINIMAX
    private int searchDepth;

    /** Scores cumulés (victoires) des Rouges et des Jaunes pendant la session. */
    private int scoreRed;
    private int scoreYellow;

    /**
     * Crée un contrôleur.
     *
     * @param scanner lecteur d'entrée partagé avec le menu principal
     */
    public GameController(Scanner scanner) {
        this.scanner = scanner;
        this.aiEngine = new Connect4Search();
        this.hintManager = new HintManager();
    }

    /** @return le nombre de victoires cumulées des Rouges */
    public int getScoreRed() {
        return scoreRed;
    }

    /** @return le nombre de victoires cumulées des Jaunes */
    public int getScoreYellow() {
        return scoreYellow;
    }

    /**
     * Lance une partie (nouvelle ou restaurée) en utilisant la stratégie par défaut du niveau.
     *
     * @param mode          {@link #MODE_HUMAN_VS_HUMAN} ou {@link #MODE_HUMAN_VS_AI}
     * @param diffConfig    niveau de complexité (profondeur de recherche)
     * @param restoredState état sauvegardé à restituer, ou {@code null} pour une nouvelle partie
     */
    public void start(String mode, AIDifficulty diffConfig, GameState restoredState) {
        String defaultStrategy = (diffConfig.getStrategy() == AIDifficulty.Strategy.ALPHABETA)
                ? STRATEGY_ALPHA_BETA : STRATEGY_MINIMAX;
        start(mode, diffConfig, defaultStrategy, restoredState);
    }

    /**
     * Lance une partie (nouvelle ou restaurée) avec une stratégie choisie explicitement.
     * La stratégie s'applique à la machine ET à l'aide au joueur.
     *
     * @param mode           {@link #MODE_HUMAN_VS_HUMAN} ou {@link #MODE_HUMAN_VS_AI}
     * @param diffConfig     niveau de complexité (profondeur de recherche)
     * @param strategyChoice {@link #STRATEGY_ALPHA_BETA} ou {@link #STRATEGY_MINIMAX}
     * @param restoredState  état sauvegardé à restituer, ou {@code null} pour une nouvelle partie
     */
    public void start(String mode, AIDifficulty diffConfig, String strategyChoice, GameState restoredState) {
        if (restoredState != null) {
            this.currentPosition = restoredState.position;
            this.gameMode = restoredState.gameMode;
            this.difficulty = restoredState.difficulty;
            this.strategy = restoredState.strategy;
            this.hintManager = new HintManager(restoredState.hintsLeft);
            this.searchDepth = resolveDepth(this.difficulty);
            this.scoreRed = restoredState.scoreRed;
            this.scoreYellow = restoredState.scoreYellow;
            ConsoleView.printSuccess("Partie restituee avec succes !");
            System.out.println("Mode : " + gameMode + " | Coups deja joues : " + currentPosition.nbMoves
                    + " | Aides restantes : " + hintManager.getHintsRemaining());
        } else {
            this.currentPosition = new Connect4Position();
            this.gameMode = mode;
            this.difficulty = diffConfig.name();
            this.strategy = strategyChoice;
            this.searchDepth = diffConfig.getDepth();
            this.hintManager = new HintManager(HintManager.DEFAULT_MAX_HINTS);

            System.out.println("\n--- Debut de la partie de Puissance 4 ---");
            System.out.println("Mode : " + (mode.equals(MODE_HUMAN_VS_HUMAN) ? "2 Joueurs Humains" : "Humain (Rouge) vs Machine (Jaune)"));
            System.out.println("Complexite IA : Profondeur " + searchDepth + " (" + difficulty + ")");
            System.out.println("Strategie : " + (strategy.equals(STRATEGY_ALPHA_BETA) ? "Elagage Alpha-Beta (Diapo 19)" : "Minimax standard"));
            System.out.println("Regle : Les ROUGES ('R') jouent en premier !");
        }

        runGameLoop();
    }

    /** Boucle principale : alterne les tours jusqu'à la fin de la partie ou l'abandon. */
    private void runGameLoop() {
        boolean usePruning = strategy.equals(STRATEGY_ALPHA_BETA);

        while (!currentPosition.isGameOver()) {
            ConsoleView.printBoard(currentPosition);
            ConsoleView.printStatus(currentPosition.nbMoves, hintManager.getHintsRemaining(), scoreRed, scoreYellow);

            int currentPlayer = currentPosition.currentPlayer();
            boolean isMachineTurn = gameMode.equals(MODE_HUMAN_VS_AI) && (currentPlayer == Connect4Position.YELLOW);

            if (isMachineTurn) {
                playComputerTurn(usePruning);
            } else {
                boolean continueGame = playHumanTurn(usePruning);
                if (!continueGame) {
                    return; // Le joueur a quitté vers le menu
                }
            }
        }

        announceResult();

        System.out.println("\nAppuyez sur Entree pour revenir au menu principal...");
        scanner.nextLine();
    }

    /** Affiche la grille finale, annonce le résultat et met à jour les scores. */
    private void announceResult() {
        ConsoleView.printBoard(currentPosition);
        if (currentPosition.hasWon(Connect4Position.RED)) {
            scoreRed++;
            System.out.println("\n[VICTOIRE] Les ROUGES ('R') remportent la partie !");
        } else if (currentPosition.hasWon(Connect4Position.YELLOW)) {
            scoreYellow++;
            if (gameMode.equals(MODE_HUMAN_VS_AI)) {
                System.out.println("\n[IA] La Machine ('J') a gagne la partie !");
            } else {
                System.out.println("\n[VICTOIRE] Les JAUNES ('J') remportent la partie !");
            }
        } else if (currentPosition.isFull()) {
            System.out.println("\n[EGALITE] Match nul ! La grille est pleine sans alignement de 4.");
        }
        System.out.println("Score de la session : Rouge " + scoreRed + " - " + scoreYellow + " Jaune");
    }

    /**
     * Tour d'un joueur humain : lit une commande jusqu'à obtenir un coup valide.
     * Commandes : numéro de colonne (1 à 7), A (aide), S (sauvegarde), Q (quitter).
     * Toute saisie interdite est refusée avec un message explicite, sans planter.
     *
     * @param usePruning {@code true} si l'aide doit utiliser l'élagage Alpha-Bêta
     * @return {@code false} si le joueur a choisi de quitter la partie, {@code true} sinon
     */
    private boolean playHumanTurn(boolean usePruning) {
        int player = currentPosition.currentPlayer();
        String playerName = (player == Connect4Position.RED) ? "Joueur 1 (ROUGE 'R')" : "Joueur 2 (JAUNE 'J')";

        while (true) {
            System.out.println("\n" + playerName + " a vous de jouer !");
            System.out.println("[Options : Entrez 1-7 | 'A' pour AIDE (" + hintManager.getHintsRemaining()
                    + " restantes) | 'S' pour SAUVEGARDER | 'Q' pour QUITTER]");
            System.out.print("Votre choix > ");

            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("Q")) {
                if (confirmQuit()) {
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

            // Tentative de jouer un coup, avec gestion des erreurs.
            try {
                int colIndex = Integer.parseInt(input) - 1; // saisie 1..7 -> indice 0..6
                // drop() vérifie la validité, applique la gravité, ou lève IllegalMoveException.
                currentPosition.drop(colIndex);
                return true;

            } catch (NumberFormatException e) {
                ConsoleView.printError("Saisie non reconnue. Entrez un numero de colonne (1 a 7) ou 'A', 'S', 'Q'.");
            } catch (IllegalMoveException e) {
                ConsoleView.printError(e.getMessage());
            }
        }
    }

    /**
     * Demande confirmation avant de quitter, puis propose de sauvegarder la partie.
     *
     * @return {@code true} si le joueur confirme son départ vers le menu
     */
    private boolean confirmQuit() {
        System.out.print("Voulez-vous vraiment quitter vers le menu ? (O/N) > ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("O")) {
            return false;
        }
        System.out.print("Sauvegarder la partie avant de quitter ? (O/N) > ");
        if (scanner.nextLine().trim().equalsIgnoreCase("O")) {
            handleSave();
        }
        return true;
    }

    /**
     * Tour de la machine : calcule le meilleur coup avec la stratégie et la profondeur choisies.
     *
     * @param usePruning {@code true} pour l'élagage Alpha-Bêta, {@code false} pour Minimax
     */
    private void playComputerTurn(boolean usePruning) {
        System.out.println("\n[IA] Reflexion de la Machine en cours (Profondeur " + searchDepth + ")...");
        long start = System.currentTimeMillis();

        Connect4Move best = aiEngine.computeBestMove(currentPosition, searchDepth, usePruning);
        long elapsed = System.currentTimeMillis() - start;

        if (best != null) {
            System.out.println("[IA] La Machine joue dans la " + best + " (Calcule en " + elapsed + " ms).");
            currentPosition.drop(best.column);
        } else {
            // Repli de sécurité : premier coup légal.
            for (int c : currentPosition.legalMoves()) {
                currentPosition.drop(c);
                break;
            }
        }
    }

    /**
     * Traite la commande « A » : affiche le coup conseillé par l'IA et décrémente le compteur de jokers.
     * Refuse proprement quand il n'y a plus de joker.
     *
     * @param usePruning {@code true} pour l'élagage Alpha-Bêta, {@code false} pour Minimax
     */
    private void handleHint(boolean usePruning) {
        if (!hintManager.hasHints()) {
            System.out.println("[X] Vous avez epuise vos " + HintManager.DEFAULT_MAX_HINTS
                    + " aides pour cette partie (0 restante) !");
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

    /**
     * Traite la commande « S » : sauvegarde l'état complet de la partie
     * (grille, mode, difficulté, stratégie, aides restantes, scores).
     */
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
        state.scoreRed = scoreRed;
        state.scoreYellow = scoreYellow;

        try {
            GameSaveManager.save(state, name);
            ConsoleView.printSuccess("Partie sauvegardee avec succes sous le nom : '" + name + "' dans 'saves/'.");
        } catch (Exception e) {
            System.out.println("[X] Echec de la sauvegarde : " + e.getMessage());
        }
    }

    /**
     * Convertit un niveau de difficulté sauvegardé en profondeur de recherche.
     *
     * @param diff "FACILE", "MOYEN" ou "DIFFICILE"
     * @return 2, 4 ou 6 (4 par défaut)
     */
    private int resolveDepth(String diff) {
        if ("FACILE".equalsIgnoreCase(diff)) return 2;
        if ("DIFFICILE".equalsIgnoreCase(diff)) return 6;
        return 4; // MOYEN
    }
}
