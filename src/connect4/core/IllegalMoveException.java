package connect4.core;

/**
 * Exception levée quand un coup interdit est demandé
 * (colonne pleine, indice hors bornes, partie déjà terminée, mauvais tour).
 */
public class IllegalMoveException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /** @param message explication affichable telle quelle à l'utilisateur */
    public IllegalMoveException(String message) { super(message); }
}