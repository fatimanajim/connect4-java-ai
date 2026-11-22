package connect4.storage;

import connect4.core.Connect4Position;
import java.io.Serializable;

/** Objet sérialisable contenant tout ce qu'il faut pour reprendre une partie à l'identique. */
public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Position de la grille (le joueur actif s'en déduit via nbMoves). */
    public Connect4Position position;
    /** "HUMAIN_VS_HUMAIN" ou "HUMAIN_VS_IA". */
    public String gameMode;
    /** "FACILE", "MOYEN" ou "DIFFICILE". */
    public String difficulty;
    /** "MINIMAX" ou "ALPHA_BETA" (stratégie de l'IA et de l'aide). */
    public String strategy;
    /** Nombre d'aides (jokers) encore disponibles. */
    public int hintsLeft;
    /** Couleur du joueur humain en mode contre machine (RED ou YELLOW). */
    public int humanColor;
    /** Scores cumulés des deux joueurs. */
    public int scoreRed, scoreYellow;
}