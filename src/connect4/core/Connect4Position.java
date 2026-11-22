package connect4.core;

import java.util.ArrayList;
import java.util.List;

/**
 * État complet d'une partie de Puissance 4 : grille de 6 lignes x 7 colonnes.
 * <p>Convention : {@code board[0]} est la ligne du HAUT, {@code board[5]} la ligne du BAS.
 * Les rouges jouent en premier : le joueur courant se déduit de {@link #nbMoves}.</p>
 */
public class Connect4Position extends Position {
    private static final long serialVersionUID = 1L;

    public static final int ROWS = 6, COLS = 7;
    public static final int EMPTY = 0, RED = 1, YELLOW = 2;

    /** Grille du jeu (EMPTY, RED ou YELLOW dans chaque case). */
    public final int[][] board = new int[ROWS][COLS];

    /** Nombre de pions déjà posés (0..42). */
    public int nbMoves = 0;

    /** @return le joueur qui doit jouer (RED si nbMoves est pair, sinon YELLOW) */
    public int currentPlayer() { return (nbMoves % 2 == 0) ? RED : YELLOW; }

    /** @return l'adversaire de {@code player} */
    public static int opponent(int player) { return player == RED ? YELLOW : RED; }

    /** @return contenu de la case (ligne r depuis le haut, colonne c) */
    public int get(int r, int c) { return board[r][c]; }

    /** @return true si la colonne existe et n'est pas pleine */
    public boolean isLegal(int col) {
        return col >= 0 && col < COLS && board[0][col] == EMPTY;
    }

    /**
     * Joue un pion pour le joueur courant ; il tombe jusqu'à la case libre la plus basse (gravité).
     *
     * @param col colonne choisie (0..6)
     * @return la ligne où le pion est tombé
     * @throws IllegalMoveException si la partie est finie, la colonne hors bornes ou pleine
     */
    public int drop(int col) {
        if (isGameOver())
            throw new IllegalMoveException("La partie est terminée.");
        if (col < 0 || col >= COLS)
            throw new IllegalMoveException("Colonne invalide : choisissez un nombre entre 1 et 7.");
        if (board[0][col] != EMPTY)
            throw new IllegalMoveException("La colonne " + (col + 1) + " est pleine.");
        int player = currentPlayer();
        for (int r = ROWS - 1; r >= 0; r--) {
            if (board[r][col] == EMPTY) {
                board[r][col] = player;
                nbMoves++;
                return r;
            }
        }
        throw new IllegalMoveException("La colonne " + (col + 1) + " est pleine."); // inatteignable
    }

    /**
     * Version non destructive pour l'IA : renvoie une NOUVELLE position après le coup.
     * @throws IllegalMoveException si le coup est interdit
     */
    public Connect4Position play(int col) {
        Connect4Position p = copy();
        p.drop(col);
        return p;
    }

    /** Copie profonde (indispensable pour explorer l'arbre de recherche). */
    public Connect4Position copy() {
        Connect4Position p = new Connect4Position();
        for (int r = 0; r < ROWS; r++)
            System.arraycopy(board[r], 0, p.board[r], 0, COLS);
        p.nbMoves = nbMoves;
        return p;
    }

    /**
     * @return les colonnes jouables, triées du centre vers les bords
     *         (ce tri améliore l'élagage alpha-bêta).
     */
    public List<Integer> legalMoves() {
        int[] order = {3, 2, 4, 1, 5, 0, 6};
        List<Integer> moves = new ArrayList<>();
        for (int c : order) if (isLegal(c)) moves.add(c);
        return moves;
    }

    /** @return true si la grille est pleine (42 pions) */
    public boolean isFull() { return nbMoves == ROWS * COLS; }

    /** @return true si {@code player} a aligné 4 pions (horizontal, vertical, 2 diagonales) */
    public boolean hasWon(int player) {
        int[][] dirs = {{0, 1}, {1, 0}, {1, 1}, {1, -1}};
        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c < COLS; c++)
                if (board[r][c] == player)
                    for (int[] d : dirs) {
                        int k = 1;
                        while (k < 4) {
                            int rr = r + d[0] * k, cc = c + d[1] * k;
                            if (rr < 0 || rr >= ROWS || cc < 0 || cc >= COLS
                                    || board[rr][cc] != player) break;
                            k++;
                        }
                        if (k == 4) return true;
                    }
        return false;
    }

    /** @return true si quelqu'un a gagné ou si la grille est pleine */
    public boolean isGameOver() { return hasWon(RED) || hasWon(YELLOW) || isFull(); }

    /** @return RED, YELLOW, ou EMPTY s'il n'y a pas (encore) de gagnant */
    public int winner() { return hasWon(RED) ? RED : hasWon(YELLOW) ? YELLOW : EMPTY; }

    // ===== Signatures demandées par le Profil 1 (IA) =====

    /**
     * @param col colonne testée (0..6)
     * @return true si la colonne existe et n'est pas pleine
     */
    public boolean isValidMove(int col) { return isLegal(col); }

    /**
     * Joue un pion pour {@code player} et renvoie une NOUVELLE position
     * (la position courante n'est pas modifiée).
     *
     * @param col    colonne choisie (0..6)
     * @param player RED ou YELLOW ; doit être le joueur dont c'est le tour
     * @return la nouvelle position après application de la gravité
     * @throws IllegalMoveException si le coup est interdit ou si ce n'est pas le tour de {@code player}
     */
    public Connect4Position playMove(int col, int player) {
        if (player != currentPlayer())
            throw new IllegalMoveException("Ce n'est pas le tour du joueur " + player + ".");
        return play(col);
    }

    /**
     * @param player RED ou YELLOW
     * @return true si {@code player} a aligné 4 pions
     */
    public boolean checkWin(int player) { return hasWon(player); }

    /** Affichage console de la grille (R = rouge, J = jaune). */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(" 1 2 3 4 5 6 7\n");
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++)
                sb.append('|').append(board[r][c] == RED ? 'R'
                        : board[r][c] == YELLOW ? 'J' : ' ');
            sb.append("|\n");
        }
        return sb.toString();
    }
}