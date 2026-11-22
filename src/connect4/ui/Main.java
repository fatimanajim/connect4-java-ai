package connect4.ui;

import connect4.ai.AIDifficulty;
import connect4.storage.GameSaveManager;
import connect4.storage.GameState;

import java.util.List;
import java.util.Scanner;

/**
 * Point d'entrée principal de l'application Puissance 4 (Connect 4).
 * Présente le menu, la sélection des modes, la configuration et le chargement.
 * 
 * @author Groupe Devoir 2 - IA (Enseignant : M'hamed AIT KBIR)
 */
public class Main {

    private static AIDifficulty currentDifficulty = AIDifficulty.MOYEN;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        ConsoleView.printBanner();

        boolean running = true;
        while (running) {
            ConsoleView.printMainMenu(currentDifficulty.name(), currentDifficulty.getStrategy().name());
            System.out.print("Entrez votre choix (1-6) > ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    startNewGame("HUMAIN_VS_HUMAIN");
                    break;
                case "2":
                    startNewGame("HUMAIN_VS_IA");
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
                    System.out.println("[!] Choix invalide. Veuillez entrer un chiffre entre 1 et 6.\n");
            }
        }
        scanner.close();
    }

    private static void startNewGame(String mode) {
        GameController controller = new GameController(scanner);
        controller.start(mode, currentDifficulty, null);
    }

    private static void loadSavedGame() {
        List<String> saves = GameSaveManager.listSaves();
        if (saves.isEmpty()) {
            System.out.println("\n[i] Aucune partie sauvegardee trouvee dans le dossier 'saves/'.");
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
            if (index == -1) return;

            if (index >= 0 && index < saves.size()) {
                GameState state = GameSaveManager.load(saves.get(index));
                if (state != null) {
                    GameController controller = new GameController(scanner);
                    controller.start(state.gameMode, currentDifficulty, state);
                }
            } else {
                System.out.println("[!] Numero invalide.");
            }
        } catch (Exception e) {
            System.out.println("[!] Erreur lors du chargement : " + e.getMessage());
        }
    }

    private static void configureSettings() {
        System.out.println("\n--- CONFIGURATION DE LA COMPLEXITE ET STRATEGIE ---");
        System.out.println("1. Facile      (Profondeur 2 - Minimax)");
        System.out.println("2. Moyen       (Profondeur 4 - Alpha-Beta) [Recommande]");
        System.out.println("3. Difficile   (Profondeur 6 - Alpha-Beta)");
        System.out.print("Selectionnez le niveau (1-3) > ");
        String input = scanner.nextLine().trim();

        switch (input) {
            case "1":
                currentDifficulty = AIDifficulty.FACILE;
                System.out.println("[OK] Niveau regle sur : " + currentDifficulty.getLabel());
                break;
            case "2":
                currentDifficulty = AIDifficulty.MOYEN;
                System.out.println("[OK] Niveau regle sur : " + currentDifficulty.getLabel());
                break;
            case "3":
                currentDifficulty = AIDifficulty.DIFFICILE;
                System.out.println("[OK] Niveau regle sur : " + currentDifficulty.getLabel());
                break;
            default:
                System.out.println("[!] Choix inchange.");
        }
    }
}
