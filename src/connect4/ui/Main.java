package connect4.ui;

import connect4.ai.AIDifficulty;
import connect4.storage.GameSaveManager;
import connect4.storage.GameState;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Point d'entrée principal de l'application Puissance 4 (Connect 4).
 * <p>
 * Présente le menu, la sélection des modes de jeu, la configuration
 * (complexité, stratégie de la machine et de l'aide, couleurs) et le chargement
 * d'une partie sauvegardée.
 * </p>
 * <p>Option de lancement : {@code --no-color} désactive les couleurs ANSI.</p>
 *
 * @author Groupe Devoir 2 - IA (Enseignant : M'hamed AIT KBIR)
 */
public class Main {

    private static AIDifficulty currentDifficulty = AIDifficulty.MOYEN;
    private static String currentStrategy = defaultStrategyOf(AIDifficulty.MOYEN);
    private static final Scanner scanner = new Scanner(System.in);

    /** Contrôleur unique : il conserve les scores de la session entre les parties. */
    private static GameController controller;

    /**
     * Lance l'application.
     *
     * @param args {@code --no-color} pour désactiver les couleurs
     */
    public static void main(String[] args) {
        if (Arrays.asList(args).contains("--no-color") || System.getenv("NO_COLOR") != null) {
            ConsoleView.setColorEnabled(false);
        }
        controller = new GameController(scanner);

        ConsoleView.printBanner();

        try {
            boolean running = true;
            while (running) {
                ConsoleView.printMainMenu(currentDifficulty.name(), currentStrategy,
                        controller.getScoreRed(), controller.getScoreYellow());
                System.out.print("Entrez votre choix (1-6) > ");
                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1":
                        startNewGame(GameController.MODE_HUMAN_VS_HUMAN);
                        break;
                    case "2":
                        startNewGame(GameController.MODE_HUMAN_VS_AI);
                        break;
                    case "3":
                        loadSavedGame();
                        break;
                    case "4":
                        configureSettings();
                        break;
                    case "5":
                        ConsoleView.printRules();
                        break;
                    case "6":
                        System.out.println("\nMerci d'avoir joue au Puissance 4 ! A bientot.");
                        running = false;
                        break;
                    default:
                        ConsoleView.printError("Choix invalide. Veuillez entrer un chiffre entre 1 et 6.\n");
                }
            }
        } catch (NoSuchElementException e) {
            // Flux d'entrée fermé (Ctrl+D / Ctrl+Z) : sortie propre au lieu d'un plantage.
            System.out.println("\n[i] Entree fermee. Fin de l'application.");
        }
        scanner.close();
    }

    /**
     * Stratégie par défaut associée à un niveau (celle définie dans {@link AIDifficulty}).
     *
     * @param level niveau de complexité
     * @return "ALPHA_BETA" ou "MINIMAX"
     */
    private static String defaultStrategyOf(AIDifficulty level) {
        return (level.getStrategy() == AIDifficulty.Strategy.ALPHABETA)
                ? GameController.STRATEGY_ALPHA_BETA : GameController.STRATEGY_MINIMAX;
    }

    /**
     * Démarre une nouvelle partie avec les réglages courants.
     *
     * @param mode mode de jeu (2 joueurs ou contre la machine)
     */
    private static void startNewGame(String mode) {
        controller.start(mode, currentDifficulty, currentStrategy, null);
    }

    /** Liste les sauvegardes disponibles, laisse choisir, puis restitue la partie. */
    private static void loadSavedGame() {
        List<String> saves = GameSaveManager.listSaves();
        if (saves.isEmpty()) {
            ConsoleView.printInfo("Aucune partie sauvegardee trouvee dans le dossier 'saves/'.");
            return;
        }

        System.out.println("\n--- PARTIES SAUVEGARDEES DISPONIBLES ---");
        for (int i = 0; i < saves.size(); i++) {
            System.out.println((i + 1) + ". " + saves.get(i));
        }
        System.out.print("Entrez le numero de la partie a charger (0 pour annuler) > ");
        String input = scanner.nextLine().trim();

        try {
            int index = Integer.parseInt(input) - 1;
            if (index == -1) {
                return;
            }

            if (index >= 0 && index < saves.size()) {
                GameState state = GameSaveManager.load(saves.get(index));
                if (state != null) {
                    controller.start(state.gameMode, currentDifficulty, state);
                }
            } else {
                ConsoleView.printError("Numero invalide.");
            }
        } catch (NumberFormatException e) {
            ConsoleView.printError("Entrez un numero de la liste (ou 0 pour annuler).");
        } catch (Exception e) {
            ConsoleView.printError("Impossible de charger la sauvegarde : " + e.getMessage());
        }
    }

    /**
     * Menu de configuration : niveau de complexité, stratégie (machine et aide)
     * et affichage en couleurs. Une saisie invalide laisse le réglage inchangé.
     */
    private static void configureSettings() {
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("\n--- CONFIGURATION ---");
            System.out.println("1. Niveau de complexite   (actuel : " + currentDifficulty.getLabel() + ")");
            System.out.println("2. Strategie de recherche (actuelle : " + currentStrategy + ") - machine et aide");
            System.out.println("3. Affichage en couleurs  (actuel : " + (ConsoleView.isColorEnabled() ? "OUI" : "NON") + ")");
            System.out.println("0. Retour au menu principal");
            System.out.print("Votre choix > ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    chooseLevel();
                    break;
                case "2":
                    chooseStrategy();
                    break;
                case "3":
                    ConsoleView.setColorEnabled(!ConsoleView.isColorEnabled());
                    ConsoleView.printSuccess("Couleurs : " + (ConsoleView.isColorEnabled() ? "activees" : "desactivees"));
                    break;
                case "0":
                    inMenu = false;
                    break;
                default:
                    ConsoleView.printInfo("Choix inchange.");
            }
        }
    }

    /** Choix du niveau (profondeur 2, 4 ou 6). La stratégie par défaut du niveau est appliquée. */
    private static void chooseLevel() {
        System.out.println("\n1. Facile      (Profondeur 2)");
        System.out.println("2. Moyen       (Profondeur 4) [Recommande]");
        System.out.println("3. Difficile   (Profondeur 6)");
        System.out.print("Selectionnez le niveau (1-3) > ");
        String input = scanner.nextLine().trim();

        AIDifficulty chosen = null;
        switch (input) {
            case "1": chosen = AIDifficulty.FACILE; break;
            case "2": chosen = AIDifficulty.MOYEN; break;
            case "3": chosen = AIDifficulty.DIFFICILE; break;
            default:
                ConsoleView.printInfo("Choix inchange.");
        }
        if (chosen != null) {
            currentDifficulty = chosen;
            currentStrategy = defaultStrategyOf(chosen);
            ConsoleView.printSuccess("Niveau regle sur : " + chosen.getLabel()
                    + " (strategie par defaut : " + currentStrategy + ", modifiable via l'option 2)");
        }
    }

    /** Choix de la stratégie de recherche, appliquée à la machine et à l'aide au joueur. */
    private static void chooseStrategy() {
        System.out.println("\n1. Elagage Alpha-Beta (Diapo 19) - rapide");
        System.out.println("2. Minimax standard              - sans elagage");
        System.out.print("Selectionnez la strategie (1-2) > ");
        String input = scanner.nextLine().trim();

        switch (input) {
            case "1":
                currentStrategy = GameController.STRATEGY_ALPHA_BETA;
                ConsoleView.printSuccess("Strategie reglee sur : " + currentStrategy);
                break;
            case "2":
                currentStrategy = GameController.STRATEGY_MINIMAX;
                ConsoleView.printSuccess("Strategie reglee sur : " + currentStrategy);
                break;
            default:
                ConsoleView.printInfo("Choix inchange.");
        }
    }
}
