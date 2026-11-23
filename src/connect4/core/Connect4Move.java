package connect4.core;

/**
 * Un coup au Puissance 4 : le choix d'une colonne (indice de 0 à 6).
 * Implémenté par le Profil 2.
 */
public class Connect4Move extends Move {
    /** Identifiant de version pour la sérialisation (sauvegarde des parties). */
    private static final long serialVersionUID = 1L;

    /** Colonne jouée, de 0 (gauche) à 6 (droite). */
    public final int column;

    /**
     * Crée un coup dans la colonne donnée.
     *
     * @param column indice de colonne (0..6)
     */
    public Connect4Move(int column) { this.column = column; }

    /**
     * Accesseur de la colonne, conservé pour compatibilité avec les autres modules.
     *
     * @return l'indice de colonne (0..6)
     */
    public int getColumn() { return column; }

    /**
     * Affichage convivial : les colonnes sont numérotées de 1 à 7 pour le joueur.
     *
     * @return par exemple « Colonne 4 » pour la colonne d'indice 3
     */
    @Override
    public String toString() { return "Colonne " + (column + 1); }
}