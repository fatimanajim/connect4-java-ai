package connect4.core;

import java.io.Serializable;

/**
 * Représente un coup spécifique dans le jeu du Puissance 4 (choix d'une colonne).
 * Hérite de la classe abstraite Move vue en classe.
 * 
 * @author Groupe Devoir 2 - IA
 */
public class Connect4Move extends Move implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final int column; // Colonne comprise entre 0 et 6

    /**
     * Constructeur d'un coup de Puissance 4.
     * @param column L'indice de la colonne (0 à 6)
     */
    public Connect4Move(int column) {
        this.column = column;
    }

    /**
     * Retourne l'indice de la colonne jouée.
     * @return entier entre 0 et 6
     */
    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return "Colonne " + (column + 1); // Affichage 1-indexé pour l'utilisateur
    }
}
