package connect4.ai;

import connect4.core.Connect4Position;

/**
 * Fonction d'évaluation heuristique pour le jeu du Puissance 4.
 * Évalue la force d'une position donnée lorsque la profondeur maximale de recherche est atteinte.
 * 
 * Critères pris en compte :
 * 1. Contrôle de la colonne centrale (statistiquement la plus stratégique).
 * 2. Détection des fenêtres de 4 cases (alignements potentiels) :
 *    - 3 pions de notre couleur + 1 case vide : bonus élevé (+50).
 *    - 2 pions de notre couleur + 2 cases vides : bonus moyen (+10).
 *    - 3 pions adverses + 1 case vide : pénalité sévère (-80 pour forcer le blocage).
 *    - 2 pions adverses + 2 cases vides : pénalité modérée (-15).
 * 
 * @author Groupe Devoir 2 - IA
 */
public class EvaluationFunction {

    private static final int WIN_SCORE = 100000;

    /**
     * Calcule le score heuristique de la grille du point de vue d'un joueur.
     * 
     * @param pos La position courante
     * @param player Identifiant du joueur évalué (1 pour Rouge, 2 pour Jaune)
     * @return Score flottant (positif = favorable, négatif = défavorable)
     */
    public static float evaluate(Connect4Position pos, int player) {
        int opponent = (player == Connect4Position.RED) ? Connect4Position.YELLOW : Connect4Position.RED;

        // Conditions terminales
        if (pos.checkWin(player)) return WIN_SCORE;
        if (pos.checkWin(opponent)) return -WIN_SCORE;
        if (pos.isFull()) return 0;

        float score = 0;

        // 1. Bonus pour la colonne centrale (colonne index 3)
        int centerCount = 0;
        for (int r = 0; r < Connect4Position.ROWS; r++) {
            if (pos.getCell(r, 3) == player) {
                centerCount++;
            }
        }
        score += centerCount * 6; // +6 par pion au centre

        // 2. Évaluation des fenêtres de 4 cases dans toutes les directions
        int[][] board = pos.getBoardCopy();

        // Horizontal
        for (int r = 0; r < Connect4Position.ROWS; r++) {
            for (int c = 0; c <= Connect4Position.COLS - 4; c++) {
                score += evaluateWindow(board[r][c], board[r][c+1], board[r][c+2], board[r][c+3], player, opponent);
            }
        }

        // Vertical
        for (int r = 0; r <= Connect4Position.ROWS - 4; r++) {
            for (int c = 0; c < Connect4Position.COLS; c++) {
                score += evaluateWindow(board[r][c], board[r+1][c], board[r+2][c], board[r+3][c], player, opponent);
            }
        }

        // Diagonale descendante (\)
        for (int r = 0; r <= Connect4Position.ROWS - 4; r++) {
            for (int c = 0; c <= Connect4Position.COLS - 4; c++) {
                score += evaluateWindow(board[r][c], board[r+1][c+1], board[r+2][c+2], board[r+3][c+3], player, opponent);
            }
        }

        // Diagonale montante (/)
        for (int r = 3; r < Connect4Position.ROWS; r++) {
            for (int c = 0; c <= Connect4Position.COLS - 4; c++) {
                score += evaluateWindow(board[r][c], board[r-1][c+1], board[r-2][c+2], board[r-3][c+3], player, opponent);
            }
        }

        return score;
    }

    /**
     * Attribue des points à une fenêtre de 4 cases selon la répartition des pions.
     */
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

        if (playerCount == 4) return WIN_SCORE;
        if (playerCount == 3 && emptyCount == 1) return 50f;
        if (playerCount == 2 && emptyCount == 2) return 10f;

        // Blocage des menaces adverses
        if (oppCount == 3 && emptyCount == 1) return -80f;
        if (oppCount == 2 && emptyCount == 2) return -15f;

        return 0f;
    }
}
