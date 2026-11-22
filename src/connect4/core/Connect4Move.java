package connect4.core;

/** 
 * Un coup au Puissance 4 : le choix d'une colonne (indice de 0 à 6). 
 * Implémenté par le Profil 2.
 */
public class Connect4Move extends Move {
    private static final long serialVersionUID = 1L;

    /** Colonne jouée, de 0 (gauche) à 6 (droite). */
    public final int column;

    /** @param column indice de colonne (0..6) */
    public Connect4Move(int column) { this.column = column; }

    /** Accesseur pour compatibilité */
    public int getColumn() { return column; }

    /** Les colonnes sont numérotées de 1 à 7 pour le joueur. */
    @Override
    public String toString() { return "Colonne " + (column + 1); }
}
