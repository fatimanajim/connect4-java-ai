package connect4.core;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Représente l'état du plateau de Puissance 4 (grille verticale de 7 colonnes et 6 lignes).
 * Hérite de la classe abstraite Position vue en cours.
 * 
 * Valeurs des cases :
 * 0 : Case vide
 * 1 : Pion Rouge ('R' - Joueur 1 / Humain - Commence toujours la partie)
 * 2 : Pion Jaune ('J' - Joueur 2 / Machine)
 * 
 * @author Groupe Devoir 2 - IA
 */
public class Connect4Position extends Position implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int ROWS = 6;
    public static final int COLS = 7;
    public static final int EMPTY = 0;
    public static final int RED = 1;    // Joueur 1 (commence)
    public static final int YELLOW = 2; // Joueur 2 / Machine

    private final int[][] board;

    /**
     * Initialise un plateau vide de 6 lignes par 7 colonnes.
     */
    public Connect4Position() {
        this.board = new int[ROWS][COLS];
    }

    /**
     * Constructeur par copie profonde.
     */
    public Connect4Position(Connect4Position other) {
        this.board = new int[ROWS][COLS];
        for (int r = 0; r < ROWS; r++) {
            System.arraycopy(other.board[r], 0, this.board[r], 0, COLS);
        }
    }

    /**
     * Retourne la valeur de la case aux coordonnées données.
     */
    public int getCell(int row, int col) {
        return board[row][col];
    }

    /**
     * Retourne une copie de la grille de jeu.
     */
    public int[][] getBoardCopy() {
        int[][] copy = new int[ROWS][COLS];
        for (int r = 0; r < ROWS; r++) {
            System.arraycopy(this.board[r], 0, copy[r], 0, COLS);
        }
        return copy;
    }

    /**
     * Vérifie si un coup est autorisé dans la colonne spécifiée.
     * @param col indice de la colonne (0 à 6)
     * @return true si la colonne est valide et non pleine
     */
    public boolean isValidMove(int col) {
        if (col < 0 || col >= COLS) {
            return false;
        }
        return board[0][col] == EMPTY; // Si la case du haut est vide, la colonne a au moins une place
    }

    /**
     * Applique un mouvement avec respect de la gravité (le pion tombe à la position libre la plus basse).
     * Retourne une NOUVELLE instance de Connect4Position (immutabilité pour l'arbre de recherche).
     * 
     * @param col Colonne choisie (0 à 6)
     * @param player Identifiant du joueur (1 ou 2)
     * @return Nouvelle position après le coup
     * @throws IllegalArgumentException si le coup est illégal (colonne pleine ou invalide)
     */
    public Connect4Position playMove(int col, int player) {
        if (!isValidMove(col)) {
            throw new IllegalArgumentException("Coup illégal : La colonne " + (col + 1) + " est pleine ou invalide.");
        }

        Connect4Position nextPos = new Connect4Position(this);
        // Descente du pion (gravité) : du bas (ROWS-1) vers le haut (0)
        for (int r = ROWS - 1; r >= 0; r--) {
            if (nextPos.board[r][col] == EMPTY) {
                nextPos.board[r][col] = player;
                break;
            }
        }
        return nextPos;
    }

    /**
     * Vérifie si le joueur donné a aligné 4 pions consécutifs.
     * 
     * @param player 1 pour Rouge, 2 pour Jaune
     * @return true si le joueur a gagné
     */
    public boolean checkWin(int player) {
        // 1. Vérification horizontale (-)
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c <= COLS - 4; c++) {
                if (board[r][c] == player &&
                    board[r][c + 1] == player &&
                    board[r][c + 2] == player &&
                    board[r][c + 3] == player) {
                    return true;
                }
            }
        }

        // 2. Vérification verticale (|)
        for (int r = 0; r <= ROWS - 4; r++) {
            for (int c = 0; c < COLS; c++) {
                if (board[r][c] == player &&
                    board[r + 1][c] == player &&
                    board[r + 2][c] == player &&
                    board[r + 3][c] == player) {
                    return true;
                }
            }
        }

        // 3. Vérification diagonale descendante (\)
        for (int r = 0; r <= ROWS - 4; r++) {
            for (int c = 0; c <= COLS - 4; c++) {
                if (board[r][c] == player &&
                    board[r + 1][c + 1] == player &&
                    board[r + 2][c + 2] == player &&
                    board[r + 3][c + 3] == player) {
                    return true;
                }
            }
        }

        // 4. Vérification diagonale montante (/)
        for (int r = 3; r < ROWS; r++) {
            for (int c = 0; c <= COLS - 4; c++) {
                if (board[r][c] == player &&
                    board[r - 1][c + 1] == player &&
                    board[r - 2][c + 2] == player &&
                    board[r - 3][c + 3] == player) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Vérifie si le plateau est entièrement rempli (condition de match nul).
     */
    public boolean isFull() {
        for (int c = 0; c < COLS; c++) {
            if (board[0][c] == EMPTY) {
                return false;
            }
        }
        return true;
    }

    /**
     * Compte le nombre de coups déjà joués sur le plateau.
     */
    public int getPieceCount() {
        int count = 0;
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (board[r][c] != EMPTY) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Affiche textuellement la grille avec indices des colonnes.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n  1   2   3   4   5   6   7  \n");
        sb.append("+---+---+---+---+---+---+---+\n");
        for (int r = 0; r < ROWS; r++) {
            sb.append("|");
            for (int c = 0; c < COLS; c++) {
                char symbol = '.';
                if (board[r][c] == RED) symbol = 'R';
                else if (board[r][c] == YELLOW) symbol = 'J';
                sb.append(" ").append(symbol).append(" |");
            }
            sb.append("\n");
        }
        sb.append("+---+---+---+---+---+---+---+\n");
        return sb.toString();
    }
}
