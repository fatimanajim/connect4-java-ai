package connect4.ai;

import connect4.core.Connect4Position;

/**
 * Fonction d'Evaluation Heuristique pour le Puissance 4.
 * Conçue et developpee par le Profil 1 (Lead IA).
 * 
 * ==============================================================================================
 * PRINCIPE MATHEMATIQUE & JUSTIFICATION THEORIQUE :
 * Le jeu du Puissance 4 possede un espace d'etats gigantesque (~ 4,5 x 10^12 positions possibles).
 * Il est impossible en temps raisonnable d'explorer l'arbre jusqu'aux etats terminaux.
 * Lorsque la profondeur limite est atteinte (reachedMaxDepth), cette fonction estime la qualite 
 * de la grille pour un joueur donne selon 3 axes majeurs :
 * 
 * 1. PONDERATION DE LA COLONNE CENTRALE :
 *    En Connect 4, la colonne 3 (centrale) est l'axe geometrique le plus strategique :
 *    elle peut participer a un maximum de combinaisons gagnantes (jusqu'a 7 alignements possibles
 *    contre seulement 3 pour les colonnes des extremites). Chaque pion au centre rapporte un bonus.
 * 
 * 2. ANALYSE PAR FENETRE GLISSANTE DE 4 CASES :
 *    On decompose la grille en toutes ses quadruplets contigus possibles (Horizontaux, Verticaux,
 *    Diagonales Montantes et Diagonales Descendantes) :
 *    - 4 pions alignes : VICTOIRE IMMEDIATE (+100 000 pts).
 *    - 3 pions de notre couleur + 1 case vide : AVANTAGE MAJEUR (+50 pts).
 *    - 2 pions de notre couleur + 2 cases vides : CONSTRUCTION (+10 pts).
 * 
 * 3. BLOCAGE PRIORITAIRE DES MENACES ADVERSES :
 *    - 3 pions adverses + 1 case vide : MENACE CRITIQUE (-80 pts).
 *      La penalite (-80) est superieure au bonus d'attaque (+50) pour forcer l'IA a parer
 *      prioritairement la victoire imminente de l'adversaire.
 * ==============================================================================================
 * 
 * @author Profil 1 - Lead IA & Algorithmes de Recherche
 */
public class EvaluationFunction {

    public static final int SCORE_VICTOIRE = 100000;
    public static final int BONUS_CENTRE = 6;
    public static final int SCORE_3_PIONS = 50;
    public static final int SCORE_2_PIONS = 10;
    public static final int PENALITE_MENACE_3_ADVERSE = -80;
    public static final int PENALITE_MENACE_2_ADVERSE = -15;

    /**
     * Evalue la position globale de la grille du point de vue du joueur specifie.
     * 
     * @param pos La position courante de la grille
     * @param player L'identifiant du joueur evalue (Connect4Position.RED ou YELLOW)
     * @return Score d'evaluation numerique (positif = avantage, negatif = desavantage)
     */
    public static float evaluate(Connect4Position pos, int player) {
        int opponent = (player == Connect4Position.RED) ? Connect4Position.YELLOW : Connect4Position.RED;

        // 1. Tests des etats terminaux decisifs
        if (pos.checkWin(player)) return SCORE_VICTOIRE;
        if (pos.checkWin(opponent)) return -SCORE_VICTOIRE;
        if (pos.isFull()) return 0; // Match nul

        float totalScore = 0;

        // 2. Bonus strategique pour le controle de la colonne centrale (colonne 3)
        int centerCount = 0;
        for (int r = 0; r < Connect4Position.ROWS; r++) {
            if (pos.getCell(r, 3) == player) {
                centerCount++;
            }
        }
        totalScore += (centerCount * BONUS_CENTRE);

        // 3. Extraction de la matrice pour analyse des fenetres de 4 cases
        int[][] board = pos.getBoardCopy();

        // 3.1. Analyse Horizontale (-)
        for (int r = 0; r < Connect4Position.ROWS; r++) {
            for (int c = 0; c <= Connect4Position.COLS - 4; c++) {
                totalScore += evaluateWindow(board[r][c], board[r][c+1], board[r][c+2], board[r][c+3], player, opponent);
            }
        }

        // 3.2. Analyse Verticale (|)
        for (int r = 0; r <= Connect4Position.ROWS - 4; r++) {
            for (int c = 0; c < Connect4Position.COLS; c++) {
                totalScore += evaluateWindow(board[r][c], board[r+1][c], board[r+2][c], board[r+3][c], player, opponent);
            }
        }

        // 3.3. Analyse Diagonale Descendante (\)
        for (int r = 0; r <= Connect4Position.ROWS - 4; r++) {
            for (int c = 0; c <= Connect4Position.COLS - 4; c++) {
                totalScore += evaluateWindow(board[r][c], board[r+1][c+1], board[r+2][c+2], board[r+3][c+3], player, opponent);
            }
        }

        // 3.4. Analyse Diagonale Montante (/)
        for (int r = 3; r < Connect4Position.ROWS; r++) {
            for (int c = 0; c <= Connect4Position.COLS - 4; c++) {
                totalScore += evaluateWindow(board[r][c], board[r-1][c+1], board[r-2][c+2], board[r-3][c+3], player, opponent);
            }
        }

        return totalScore;
    }

    /**
     * Evalue une fenetre particuliere composee de 4 cases contigues.
     * 
     * @param c1 Premiere case de la fenetre
     * @param c2 Deuxieme case
     * @param c3 Troisieme case
     * @param c4 Quatrieme case
     * @param player Identifiant du joueur ami
     * @param opponent Identifiant du joueur adverse
     * @return Score attribue a cette fenetre
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

        // Victoire detectee dans la fenetre
        if (playerCount == 4) return SCORE_VICTOIRE;

        // Potentiel offensif ami
        if (playerCount == 3 && emptyCount == 1) return SCORE_3_PIONS;
        if (playerCount == 2 && emptyCount == 2) return SCORE_2_PIONS;

        // Menaces defensives adverses (blocage imperatif)
        if (oppCount == 3 && emptyCount == 1) return PENALITE_MENACE_3_ADVERSE;
        if (oppCount == 2 && emptyCount == 2) return PENALITE_MENACE_2_ADVERSE;

        return 0f;
    }
}
