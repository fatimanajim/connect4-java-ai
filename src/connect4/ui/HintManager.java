package connect4.ui;

import connect4.ai.AIDifficulty;
import connect4.ai.Connect4Search;
import connect4.core.Connect4Move;
import connect4.core.Connect4Position;
import connect4.core.GameSearch;

/**
 * Gère le système d'aide au joueur avec limitation du nombre d'utilisations (jokers).
 * Exigence du projet : "Permettre au joueur de demander de l'aide un certain nombre de fois".
 * 
 * @author Groupe Devoir 2 - IA
 */
public class HintManager {

    public static final int DEFAULT_MAX_HINTS = 3; // 3 aides par partie par défaut
    private int remainingHints;
    private final Connect4Search searchEngine;

    public HintManager() {
        this(DEFAULT_MAX_HINTS);
    }

    public HintManager(int maxHints) {
        this.remainingHints = maxHints;
        this.searchEngine = new Connect4Search();
    }

    public int getRemainingHints() {
        return remainingHints;
    }

    public void setRemainingHints(int count) {
        this.remainingHints = count;
    }

    public boolean hasHints() {
        return remainingHints > 0;
    }

    /**
     * Sollicite l'IA pour calculer le meilleur coup à conseiller au joueur humain.
     * Consomme un joker d'aide en cas de succès.
     * 
     * @param position La position actuelle du plateau
     * @param difficulty Niveau de difficulté/stratégie pour l'analyse
     * @return Le coup recommandé ou null si plus d'aide disponible
     */
    public Connect4Move requestHint(Connect4Position position, AIDifficulty difficulty) {
        if (!hasHints()) {
            return null;
        }

        boolean useAlphaBeta = (difficulty.getStrategy() == AIDifficulty.Strategy.ALPHABETA);
        Connect4Move recommended = searchEngine.computeBestMove(position, GameSearch.HUMAN, difficulty.getDepth(), useAlphaBeta);

        if (recommended != null) {
            remainingHints--;
        }
        return recommended;
    }
}
