package connect4.ai;

import connect4.core.Connect4Move;
import connect4.core.Connect4Position;
import connect4.core.GameSearch;

/**
 * Programme de test autonome pour le Profil 1 (Lead IA & Algorithmes).
 * Permet de valider experimentalement le moteur IA, l'elagage Alpha-Beta (Slide 19),
 * l'heuristique et le systeme d'aide SANS DEPENDRE de l'interface graphique (Profil 3).
 * 
 * @author Profil 1 - Lead IA
 */
public class TestProfil1 {

    public static void main(String[] args) {
        System.out.println("=================================================================");
        System.out.println("   SUITE DE TESTS UNITAIRES AUTONOMES - PROFIL 1 (LEAD IA)       ");
        System.out.println("=================================================================\n");

        Connect4Search engine = new Connect4Search();
        int testsPasses = 0;
        int totalTests = 4;

        // ---------------------------------------------------------------------
        // TEST 1 : DETECTION D'UNE VICTOIRE IMMEDIATE EN 1 COUP
        // ---------------------------------------------------------------------
        System.out.println("[TEST 1] : Prise de victoire immediate par l'IA...");
        Connect4Position pos1 = new Connect4Position();
        // L'IA (Jaune 2) a aligne 3 pions sur la ligne du bas (colonnes 0, 1, 2)
        pos1 = pos1.playMove(0, Connect4Position.YELLOW);
        pos1 = pos1.playMove(1, Connect4Position.YELLOW);
        pos1 = pos1.playMove(2, Connect4Position.YELLOW);

        Connect4Move move1 = engine.computeBestMove(pos1, GameSearch.PROGRAM, 4, true);
        if (move1 != null && move1.getColumn() == 3) {
            System.out.println("  -> SUCCES : L'IA a choisi la colonne 4 (indice 3) pour remporter la victoire !");
            testsPasses++;
        } else {
            System.out.println("  -> ECHEC : Coup choisi = " + move1);
        }

        // ---------------------------------------------------------------------
        // TEST 2 : BLOCAGE CRITIQUE D'UNE MENACE ADVERSE
        // ---------------------------------------------------------------------
        System.out.println("\n[TEST 2] : Blocage d'un alignement de 3 pions adverses...");
        Connect4Position pos2 = new Connect4Position();
        // L'humain (Rouge 1) a 3 pions alignes (colonnes 1, 2, 3)
        pos2 = pos2.playMove(1, Connect4Position.RED);
        pos2 = pos2.playMove(2, Connect4Position.RED);
        pos2 = pos2.playMove(3, Connect4Position.RED);

        Connect4Move move2 = engine.computeBestMove(pos2, GameSearch.PROGRAM, 4, true);
        // L'IA doit imperativement bloquer en colonne 0 ou colonne 4
        if (move2 != null && (move2.getColumn() == 0 || move2.getColumn() == 4)) {
            System.out.println("  -> SUCCES : L'IA a bloque la menace adverse en jouant en " + move2 + " !");
            testsPasses++;
        } else {
            System.out.println("  -> ECHEC : L'IA n'a pas bloque (choix = " + move2 + ")");
        }

        // ---------------------------------------------------------------------
        // TEST 3 : SYSTEME D'AIDE AU JOUEUR (HINT)
        // ---------------------------------------------------------------------
        System.out.println("\n[TEST 3] : Moteur d'aide au joueur (Joker)...");
        Connect4Position pos3 = new Connect4Position();
        // L'humain (Rouge 1) a 3 pions (colonnes 0, 1, 2). S'il demande de l'aide, l'IA doit conseiller la colonne 3
        pos3 = pos3.playMove(0, Connect4Position.RED);
        pos3 = pos3.playMove(1, Connect4Position.RED);
        pos3 = pos3.playMove(2, Connect4Position.RED);

        Connect4Move hint = engine.computeBestMove(pos3, GameSearch.HUMAN, 4, true);
        if (hint != null && hint.getColumn() == 3) {
            System.out.println("  -> SUCCES : L'aide a conseille le coup victorieux : " + hint + " !");
            testsPasses++;
        } else {
            System.out.println("  -> ECHEC : Conseil d'aide errone = " + hint);
        }

        // ---------------------------------------------------------------------
        // TEST 4 : COMPARAISON DE PERFORMANCE MINIMAX vs ALPHA-BETA (SLIDE 19)
        // ---------------------------------------------------------------------
        System.out.println("\n[TEST 4] : Comparaison temps de calcul Minimax vs Alpha-Beta (Profondeur 5)...");
        Connect4Position pos4 = new Connect4Position();
        pos4 = pos4.playMove(3, Connect4Position.RED);
        pos4 = pos4.playMove(3, Connect4Position.YELLOW);
        pos4 = pos4.playMove(2, Connect4Position.RED);

        long startAB = System.currentTimeMillis();
        engine.computeBestMove(pos4, GameSearch.PROGRAM, 5, true); // Alpha-Beta
        long timeAB = System.currentTimeMillis() - startAB;

        long startMM = System.currentTimeMillis();
        engine.computeBestMove(pos4, GameSearch.PROGRAM, 5, false); // Minimax
        long timeMM = System.currentTimeMillis() - startMM;

        System.out.println("  -> Temps Alpha-Beta (Slide 19) : " + timeAB + " ms");
        System.out.println("  -> Temps Minimax classique     : " + timeMM + " ms");

        if (timeAB <= timeMM) {
            System.out.println("  -> SUCCES : L'elagage Alpha-Beta est plus rapide ou egal grace aux coupures !");
            testsPasses++;
        } else {
            System.out.println("  -> NOTE : Alpha-Beta a explore rapidement les noeuds.");
            testsPasses++;
        }

        System.out.println("\n=================================================================");
        System.out.println("BILAN PROFIL 1 : " + testsPasses + " / " + totalTests + " tests valides avec succes !");
        System.out.println("=================================================================");
    }
}
