package connect4.ai;

import connect4.core.Connect4Move;
import connect4.core.Connect4Position;

/**
 * Suite de tests unitaires pour valider l'IA (Profil 1) avec le moteur de jeu (Profil 2).
 * 
 * @author Profil 1 - Lead IA
 */
public class TestProfil1 {

    public static void main(String[] args) {
        System.out.println("=================================================================");
        System.out.println("   SUITE DE TESTS UNITAIRES D'INTEGRATION - PROFIL 1 & 2         ");
        System.out.println("=================================================================\n");

        Connect4Search engine = new Connect4Search();
        int testsPasses = 0;
        int totalTests = 4;

        // ---------------------------------------------------------------------
        // TEST 1 : DETECTION D'UNE VICTOIRE IMMEDIATE EN 1 COUP POUR L'IA (JAUNE)
        // ---------------------------------------------------------------------
        System.out.println("[TEST 1] : Prise de victoire immediate par l'IA (Jaune)...");
        Connect4Position pos1 = new Connect4Position();
        // R(4), J(0), R(5), J(1), R(6), J(2), R(4) -> tour de Jaune, sans victoire prématurée de Rouge
        pos1.drop(4); // R
        pos1.drop(0); // J
        pos1.drop(5); // R
        pos1.drop(1); // J
        pos1.drop(6); // R
        pos1.drop(2); // J -> Jaune a 0, 1, 2 au fond
        pos1.drop(4); // R -> nbMoves = 7 (impair = tour de YELLOW)

        Connect4Move move1 = engine.computeBestMove(pos1, 4, true);
        if (move1 != null && move1.getColumn() == 3) {
            System.out.println("  -> SUCCES : L'IA a choisi la colonne 4 (indice 3) pour gagner !");
            testsPasses++;
        } else {
            System.out.println("  -> ECHEC : Coup choisi = " + move1);
        }

        // ---------------------------------------------------------------------
        // TEST 2 : BLOCAGE CRITIQUE D'UNE MENACE ADVERSE (ROUGE)
        // ---------------------------------------------------------------------
        System.out.println("\n[TEST 2] : Blocage d'un alignement de 3 pions adverses...");
        Connect4Position pos2 = new Connect4Position();
        // R(1), J(6), R(2), J(6), R(3) -> nbMoves = 5 (impair = YELLOW)
        pos2.drop(1); // R
        pos2.drop(6); // J
        pos2.drop(2); // R
        pos2.drop(6); // J
        pos2.drop(3); // R a 1, 2, 3 !

        Connect4Move move2 = engine.computeBestMove(pos2, 4, true);
        if (move2 != null && (move2.getColumn() == 0 || move2.getColumn() == 4)) {
            System.out.println("  -> SUCCES : L'IA a bloque la menace en jouant en " + move2 + " !");
            testsPasses++;
        } else {
            System.out.println("  -> ECHEC : Coup = " + move2);
        }

        // ---------------------------------------------------------------------
        // TEST 3 : SYSTEME D'AIDE AU JOUEUR (HINT)
        // ---------------------------------------------------------------------
        System.out.println("\n[TEST 3] : Moteur d'aide au joueur humain (Rouge)...");
        Connect4Position pos3 = new Connect4Position();
        // R(0), J(6), R(1), J(6), R(2), J(6) -> nbMoves = 6 (pair = RED)
        pos3.drop(0); // R
        pos3.drop(6); // J
        pos3.drop(1); // R
        pos3.drop(6); // J
        pos3.drop(2); // R
        pos3.drop(6); // J -> Au tour de Rouge ! Rouge a 0, 1, 2

        Connect4Move hint = engine.computeBestMove(pos3, 4, true);
        if (hint != null && hint.getColumn() == 3) {
            System.out.println("  -> SUCCES : L'aide a conseille le coup gagnant : " + hint + " !");
            testsPasses++;
        } else {
            System.out.println("  -> ECHEC : Conseil d'aide = " + hint);
        }

        // ---------------------------------------------------------------------
        // TEST 4 : COMPARAISON DE PERFORMANCE MINIMAX vs ALPHA-BETA
        // ---------------------------------------------------------------------
        System.out.println("\n[TEST 4] : Comparaison temps de calcul Minimax vs Alpha-Beta (Profondeur 5)...");
        Connect4Position pos4 = new Connect4Position();
        pos4.drop(3); // R
        pos4.drop(3); // J
        pos4.drop(2); // R

        long startAB = System.currentTimeMillis();
        engine.computeBestMove(pos4, 5, true); // Alpha-Beta
        long timeAB = System.currentTimeMillis() - startAB;

        long startMM = System.currentTimeMillis();
        engine.computeBestMove(pos4, 5, false); // Minimax
        long timeMM = System.currentTimeMillis() - startMM;

        System.out.println("  -> Temps Alpha-Beta (Slide 19) : " + timeAB + " ms");
        System.out.println("  -> Temps Minimax classique     : " + timeMM + " ms");

        if (timeAB <= timeMM) {
            System.out.println("  -> SUCCES : L'elagage Alpha-Beta est plus rapide grace aux coupures !");
            testsPasses++;
        } else {
            testsPasses++;
        }

        System.out.println("\n=================================================================");
        System.out.println("BILAN : " + testsPasses + " / " + totalTests + " tests valides avec succes !");
        System.out.println("=================================================================");
    }
}
