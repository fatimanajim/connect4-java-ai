import connect4.ai.AIDifficulty;
import connect4.storage.GameSaveManager;
import connect4.storage.GameState;
import connect4.ui.ConsoleView;
import connect4.ui.GameController;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Tests de bout en bout de l'interface (Profil 3).
 * <p>
 * Chaque scénario simule les saisies d'un joueur (Scanner alimenté par un texte),
 * exécute réellement {@link GameController} puis analyse l'affichage produit.
 * Lancer depuis la racine du projet (le dossier saves/ est relatif).
 * </p>
 */
public class TestProfil3 {

    private static int ok = 0;
    private static int ko = 0;

    /**
     * Exécute une partie avec des saisies scriptées et renvoie tout ce qui a été affiché.
     *
     * @param mode     mode de jeu
     * @param diff     niveau de complexité
     * @param strategy stratégie, ou null pour celle du niveau
     * @param restored état à restituer, ou null
     * @param lines    saisies successives de l'utilisateur
     * @return le texte affiché pendant la partie
     */
    private static String play(String mode, AIDifficulty diff, String strategy, GameState restored, String... lines) {
        ConsoleView.setColorEnabled(false);
        PrintStream original = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(buffer, true, "UTF-8"));
            Scanner sc = new Scanner(String.join("\n", lines) + "\n");
            GameController controller = new GameController(sc);
            if (strategy == null) {
                controller.start(mode, diff, restored);
            } else {
                controller.start(mode, diff, strategy, restored);
            }
        } catch (NoSuchElementException e) {
            System.out.println("[FIN D'ENTREE : le script de saisies s'est arrete trop tot]");
        } catch (java.io.UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } finally {
            System.setOut(original);
        }
        return new String(buffer.toByteArray(), StandardCharsets.UTF_8);
    }

    private static int count(String text, String part) {
        int n = 0;
        int i = text.indexOf(part);
        while (i >= 0) {
            n++;
            i = text.indexOf(part, i + part.length());
        }
        return n;
    }

    private static void check(String name, boolean condition) {
        if (condition) {
            ok++;
            System.out.println("[OK]     " + name);
        } else {
            ko++;
            System.out.println("[ECHEC]  " + name);
        }
    }

    public static void main(String[] args) throws Exception {
        final String HH = GameController.MODE_HUMAN_VS_HUMAN;
        final String HA = GameController.MODE_HUMAN_VS_AI;

        // U1 : victoire verticale des Rouges (colonne 1)
        String out = play(HH, AIDifficulty.MOYEN, null, null, "1", "2", "1", "2", "1", "2", "1", "");
        check("U1  Victoire verticale des Rouges annoncee", out.contains("ROUGES ('R') remportent"));

        // U2 : victoire en diagonale montante des Rouges
        out = play(HH, AIDifficulty.MOYEN, null, null, "1", "2", "2", "3", "4", "3", "3", "4", "4", "5", "4", "");
        check("U2  Victoire en diagonale annoncee", out.contains("ROUGES ('R') remportent"));
        check("U2b Score de session mis a jour (Rouge 1)", out.contains("Rouge 1 - 0 Jaune"));

        // U3 : erreurs de saisie (hors bornes, texte, vide, colonne pleine) sans plantage
        out = play(HH, AIDifficulty.MOYEN, null, null,
                "9", "abc", "0", "",
                "1", "1", "1", "1", "1", "1",
                "1",
                "Q", "O", "N");
        check("U3a Colonne 9 refusee (message explicite)", count(out, "Colonne invalide") >= 2);
        check("U3b Saisie 'abc' et vide refusees", count(out, "Saisie non reconnue") >= 2);
        check("U3c Colonne pleine refusee", out.contains("La colonne 1 est pleine"));
        check("U3d Aucun plantage (retour au menu apres Q)", !out.contains("FIN D'ENTREE"));

        // U4 : jokers limites a 3
        out = play(HH, AIDifficulty.MOYEN, null, null, "A", "A", "A", "A", "Q", "O", "N");
        check("U4a Trois conseils fournis", count(out, "L'IA vous recommande") == 3);
        check("U4b Quatrieme aide refusee", out.contains("epuise vos 3 aides"));

        // U5 : sauvegarde (avec un joker utilise) puis restitution a l'identique
        out = play(HH, AIDifficulty.MOYEN, null, null, "4", "4", "A", "S", "test_p3_save", "Q", "O", "N");
        check("U5a Sauvegarde confirmee", out.contains("sauvegardee avec succes"));
        GameState state = GameSaveManager.load("test_p3_save");
        check("U5b Grille restituee (2 coups)", state.position.nbMoves == 2);
        check("U5c Jokers restitues (2 restants)", state.hintsLeft == 2);
        check("U5d Mode restitue", HH.equals(state.gameMode));
        out = play(HH, AIDifficulty.MOYEN, null, state, "Q", "O", "N");
        check("U5e Partie restituee et reprise", out.contains("Partie restituee") && out.contains("Aides restantes : 2"));
        new File("saves", "test_p3_save.dat").delete();

        // U6 : mode contre la machine (niveau Facile, profondeur 2)
        out = play(HA, AIDifficulty.FACILE, null, null, "4", "Q", "O", "N");
        check("U6  La machine joue un coup apres le joueur", out.contains("[IA] La Machine joue"));

        // U7 : choix de la strategie independant du niveau
        out = play(HA, AIDifficulty.MOYEN, GameController.STRATEGY_MINIMAX, null, "4", "Q", "O", "N");
        check("U7a Strategie Minimax appliquee", out.contains("Minimax standard") && out.contains("[IA] La Machine joue"));
        out = play(HA, AIDifficulty.FACILE, GameController.STRATEGY_ALPHA_BETA, null, "4", "Q", "O", "N");
        check("U7b Strategie Alpha-Beta appliquee", out.contains("Elagage Alpha-Beta") && out.contains("[IA] La Machine joue"));

        // U8 : affichage de la grille (en-tete des colonnes et pions)
        out = play(HH, AIDifficulty.MOYEN, null, null, "1", "Q", "O", "N");
        check("U8  Grille affichee avec numeros de colonnes et pion R", out.contains("1   2   3   4   5   6   7") && out.contains("| R |"));

        System.out.println();
        System.out.println("Resultat : " + ok + " OK, " + ko + " echec(s)");
        if (ko > 0) {
            System.exit(1);
        }
    }
}
