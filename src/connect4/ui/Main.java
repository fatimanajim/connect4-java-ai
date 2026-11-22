package connect4.ui;

import connect4.ai.AIDifficulty;
import connect4.storage.GameSaveManager;
import connect4.storage.GameState;

import java.util.List;
import java.util.Scanner;

/**
 * Point d'entrÃ©e principal de l'application Puissance 4 (Connect 4).
 * PrÃ©sente le menu principal et gÃ¨re la navigation de l'utilisateur.
 * 
 * @author Groupe Devoir 2 - IA (Enseignant : M'hamed AIT KBIR)
 */
public class Main {

    private static AIDifficulty currentDifficulty = AIDifficulty.MOYEN;
    private static final Scanner scanner = new Scanner(System.in);
    private static final GameSaveManager saveManager = new GameSaveManager();

    public static void main(String[] args) {
        displayBanner();

        boolean running = true;
        while (running) {
            displayMainMenu();
            System.out.print("Entrez votre choix (1-6) > ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    startNewGame(GameState.MODE_HUMAN_VS_HUMAN);
                    break;
                case "2":
                    startNewGame(GameState.MODE_HUMAN_VS_AI);
                    break;
                case "3":
                    loadSavedGame();
                    break;
                case "4":
                    configureSettings();
                    break;
                case "5":
                    displayRules();
                    break;
                case "6":
                    System.out.println("\nMerci d'avoir jouÃ© au Puissance 4 ! Ã€ bientÃ´t.");
                    running = false;
                    break;
                default:
                    System.out.println("âš ï¸ Choix invalide. Veuillez entrer un chiffre entre 1 et 6.\n");
            }
        }
        scanner.close();
    }

    private static void displayBanner() {
        System.out.println("===============================================================");
        System.out.println("        PUISSANCE 4 (CONNECT 4) - INTELLIGENCE ARTIFICIELLE    ");
        System.out.println("           Devoir 2 â€¢ Enseignant : M'hamed AIT KBIR           ");
        System.out.println("           Algorithmes : Minimax & Alpha-BÃªta (Slide 19)       ");
        System.out.println("===============================================================");
    }

    private static void displayMainMenu() {
        System.out.println("\n--- MENU PRINCIPAL ---");
        System.out.println("1. Nouvelle partie : 2 Joueurs (Humain vs Humain)");
        System.out.println("2. Nouvelle partie : Solo (Humain vs Machine)");
        System.out.println("3. Reprendre une partie sauvegardÃ©e");
        System.out.println("4. Configuration de la ComplexitÃ© & StratÃ©gie [" + currentDifficulty.getLabel() + "]");
        System.out.println("5. Consulter les RÃ¨gles du Jeu");
        System.out.println("6. Quitter l'application");
    }

    private static void startNewGame(int mode) {
        GameController controller = new GameController(scanner);
        controller.start(mode, currentDifficulty, null);
    }

    private static void loadSavedGame() {
        List<String> saves = saveManager.listSaves();
        if (saves.isEmpty()) {
            System.out.println("\nâ„¹ï¸ Aucune partie sauvegardÃ©e trouvÃ©e dans le dossier 'saves/'.");
            return;
        }

        System.out.println("\n--- PARTIES SAUVEGARDÃ‰ES DISPONIBLES ---");
        for (int i = 0; i < saves.size(); i++) {
            System.out.println((i + 1) + ". " + saves.get(i));
        }
        System.out.print("Entrez le numÃ©ro du fichier Ã  charger (ou 0 pour annuler) > ");
        String input = scanner.nextLine().trim();

        try {
            int index = Integer.parseInt(input) - 1;
            if (index == -1) return;

            if (index >= 0 && index < saves.size()) {
                GameState state = saveManager.load(saves.get(index));
                if (state != null) {
                    GameController controller = new GameController(scanner);
                    controller.start(state.getGameMode(), state.getDifficulty(), state);
                }
            } else {
                System.out.println("âš ï¸ NumÃ©ro de sauvegarde invalide.");
            }
        } catch (NumberFormatException e) {
            System.out.println("âš ï¸ Saisie invalide.");
        }
    }

    private static void configureSettings() {
        System.out.println("\n--- CONFIGURATION DE LA COMPLEXITÃ‰ ET STRATÃ‰GIE ---");
        System.out.println("1. Facile      (Profondeur 2 - Minimax)");
        System.out.println("2. Moyen       (Profondeur 4 - Alpha-BÃªta) [RecommandÃ©]");
        System.out.println("3. Difficile   (Profondeur 6 - Alpha-BÃªta)");
        System.out.print("SÃ©lectionnez le niveau (1-3) > ");
        String input = scanner.nextLine().trim();

        switch (input) {
            case "1":
                currentDifficulty = AIDifficulty.FACILE;
                System.out.println("âœ“ Niveau rÃ©glÃ© sur : " + currentDifficulty.getLabel());
                break;
            case "2":
                currentDifficulty = AIDifficulty.MOYEN;
                System.out.println("âœ“ Niveau rÃ©glÃ© sur : " + currentDifficulty.getLabel());
                break;
            case "3":
                currentDifficulty = AIDifficulty.DIFFICILE;
                System.out.println("âœ“ Niveau rÃ©glÃ© sur : " + currentDifficulty.getLabel());
                break;
            default:
                System.out.println("âš ï¸ Choix inchangÃ©.");
        }
    }

    private static void displayRules() {
        System.out.println("\n====================== RÃˆGLES DU PUISSANCE 4 ======================");
        System.out.println("1. Le jeu se dÃ©roule sur une grille verticale de 7 colonnes et 6 lignes.");
        System.out.println("2. Deux joueurs s'affrontent avec des pions distincts :");
        System.out.println("   - Les Rouges ('R') commencent TOUJOURS la partie.");
        System.out.println("   - Les Jaunes ('J') jouent en second.");
        System.out.println("3. GravitÃ© : Lorsqu'un pion est insÃ©rÃ© dans une colonne, il tombe jusqu'Ã ");
        System.out.println("   la position libre la plus basse.");
        System.out.println("4. Victoire : Le premier joueur Ã  aligner consÃ©cutivement 4 pions de sa");
        System.out.println("   couleur (horizontalement, verticalement ou en diagonale) gagne la partie.");
        System.out.println("5. Match nul : Si la grille est pleine sans alignement de 4, c'est Ã©galitÃ©.");
        System.out.println("6. Aide : Vous disposez d'un nombre limitÃ© de jokers d'aide pour demander");
        System.out.println("   le meilleur coup Ã  l'IA.");
        System.out.println("===================================================================\n");
    }
}
