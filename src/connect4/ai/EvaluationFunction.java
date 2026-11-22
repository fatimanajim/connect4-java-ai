package connect4.ai;

import connect4.core.Connect4Position;

/**
 * Fonction d'évaluation heuristique pour le Puissance 4.
 * Évalue la force d'une position lorsque la profondeur maximale de recherche est atteinte.
 * 
 * Critères :
 * 1. Bonus pour le contrôle de la colonne centrale (colonne 3) qui offre le plus de combinaisons.
 * 2. Analyse par fenêtre glissante de 4 cases (lignes, colonnes, 2 diagonales).
 * 3. Pénalité de blocage supérieure au bonus d'attaque.
 * 
 * @author Profil 1 - Lead IA & Algorithmes
 */
public class EvaluationFunction {

    public static final int SCORE_VICTOIRE = 100000;
    public static final int BONUS_CENTRE = 6;
    public static final int SCORE_3_PIONS = 50;
    public static final int SCORE_2_PIONS = 10;
    public static final int PENALITE_MENACE_3_ADVERSE = -80;
    public static final int PENALITE_MENACE_2_ADVERSE = -15;

    public static float evaluate(Connect4Position pos, int player) {
        int opponent = Connect4Position.opponent(player);

        // Conditions terminales
        if (pos.hasWon(player)) return SCORE_VICTOIRE;
        if (pos.hasWon(opponent)) return -SCORE_VICTOIRE;
        if (pos.isFull()) return 0;

        float totalScore = 0;

        // 1. Bonus colonne centrale (colonne 3)
        int centerCount = 0;
        for (int r = 0; r < Connect4Position.ROWS; r++) {
            if (pos.get(r, 3) == player) {
                centerCount++;
            }
        }
        totalScore += (centerCount * BONUS_CENTRE);

        // 2. Évaluation des fenêtres de 4 cases
        int[][] board = pos.board;

        // Horizontale (-)
        for (int r = 0; r < Connect4Position.ROWS; r++) {
            for (int c = 0; c <= Connect4Position.COLS - 4; c++) {
                totalScore += evaluateWindow(board[r][c], board[r][c+1], board[r][c+2], board[r][c+3], player, opponent);
            }
        }

        // Verticale (|)
        for (int r = 0; r <= Connect4Position.ROWS - 4; r++) {
            for (int c = 0; c < Connect4Position.COLS; c++) {
                totalScore += evaluateWindow(board[r][c], board[r+1][c], board[r+2][c], board[r+3][c], player, opponent);
            }
        }

        // Diagonale descendante (\)
        for (int r = 0; r <= Connect4Position.ROWS - 4; r++) {
            for (int c = 0; c <= Connect4Position.COLS - 4; c++) {
                totalScore += evaluateWindow(board[r][c], board[r+1][c+1], board[r+2][c+2], board[r+3][c+3], player, opponent);
            }
        }

        // Diagonale montante (/)
        for (int r = 3; r < Connect4Position.ROWS; r++) {
            for (int c = 0; c <= Connect4Position.COLS - 4; c++) {
                totalScore += evaluateWindow(board[r][c], board[r-1][c+1], board[r-2][c+2], board[r-3][c+3], player, opponent);
            }
        }

        return totalScore;
    }

    private static float evaluateWindow(int c1, int c2, int c3, int c4, int player, int opponent) {
        int playerCount = 0;
        int emptyCount = 0;
        int oppCount = 0;

        int[] cells = {c1, c2, c3, c4};
        for (int cell : cells) {
            if (cell == player) playerCount++;
            else if (cell == Connect4Position.EMPTY) emptyCount++;
            else if (cell == opponent) oppCount++;
        }

        if (playerCount == 4) return SCORE_VICTOIRE;
        if (playerCount == 3 && emptyCount == 1) return SCORE_3_PIONS;
        if (playerCount == 2 && emptyCount == 2) return SCORE_2_PIONS;
        if (oppCount == 3 && emptyCount == 1) return PENALITE_MENACE_3_ADVERSE;
        if (oppCount == 2 && emptyCount == 2) return PENALITE_MENACE_2_ADVERSE;

        return 0f;
    }
}
