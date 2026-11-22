package connect4.ui;

import connect4.ai.Connect4Search;
import connect4.core.Connect4Move;
import connect4.core.Connect4Position;

/**
 * Gère le système d'aide au joueur avec limitation du nombre de jokers.
 * Répond à l'exigence : "Permettre au joueur de demander de l'aide un certain nombre de fois".
 * 
 * @author Profil 3 & Profil 1
 */
public class HintManager {

    public static final int DEFAULT_MAX_HINTS = 3;
    private int hintsRemaining;
    private final Connect4Search searchEngine;

    public HintManager() {
        this(DEFAULT_MAX_HINTS);
    }

    public HintManager(int maxHints) {
        this.hintsRemaining = maxHints;
        this.searchEngine = new Connect4Search();
    }

    public int getHintsRemaining() {
        return hintsRemaining;
    }

    public void setHintsRemaining(int hints) {
        this.hintsRemaining = hints;
    }

    public boolean hasHints() {
        return hintsRemaining > 0;
    }

    /**
     * Sollicite l'IA pour calculer le meilleur coup à conseiller au joueur humain.
     */
    public Connect4Move requestHint(Connect4Position position, int depth, boolean useAlphaBeta) {
        if (!hasHints()) {
            return null;
        }

        Connect4Move best = searchEngine.computeBestMove(position, depth, useAlphaBeta);
        if (best != null) {
            hintsRemaining--;
        }
        return best;
    }
}
