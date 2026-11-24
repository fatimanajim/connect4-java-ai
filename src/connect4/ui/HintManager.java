package connect4.ui;

import connect4.ai.Connect4Search;
import connect4.core.Connect4Move;
import connect4.core.Connect4Position;

/**
 * Gère le système d'aide au joueur avec limitation du nombre de jokers.
 * <p>
 * Répond à l'exigence du sujet : « Permettre au joueur de demander de l'aide
 * un certain nombre de fois ». Chaque demande réussie consomme un joker ;
 * lorsqu'il n'y en a plus, {@link #requestHint} refuse de répondre.
 * Le conseil est calculé par l'IA ({@link Connect4Search}) avec la même
 * stratégie (Minimax ou Alpha-Bêta) et la même profondeur que la machine.
 * </p>
 *
 * @author Profil 3 & Profil 1
 */
public class HintManager {

    /** Nombre de jokers accordés au début d'une partie. */
    public static final int DEFAULT_MAX_HINTS = 3;

    /** Nombre de jokers encore disponibles. */
    private int hintsRemaining;

    /** Moteur de recherche utilisé pour calculer le coup conseillé. */
    private final Connect4Search searchEngine;

    /** Crée un gestionnaire avec le nombre de jokers par défaut (3). */
    public HintManager() {
        this(DEFAULT_MAX_HINTS);
    }

    /**
     * Crée un gestionnaire avec un nombre de jokers donné
     * (utilisé aussi pour restaurer une partie sauvegardée).
     *
     * @param maxHints nombre de jokers disponibles (ramené à 0 s'il est négatif)
     */
    public HintManager(int maxHints) {
        this.hintsRemaining = Math.max(0, maxHints);
        this.searchEngine = new Connect4Search();
    }

    /**
     * @return le nombre de jokers encore disponibles
     */
    public int getHintsRemaining() {
        return hintsRemaining;
    }

    /**
     * Modifie le nombre de jokers restants.
     *
     * @param hints nouvelle valeur (ramenée à 0 si elle est négative)
     */
    public void setHintsRemaining(int hints) {
        this.hintsRemaining = Math.max(0, hints);
    }

    /**
     * @return {@code true} s'il reste au moins un joker
     */
    public boolean hasHints() {
        return hintsRemaining > 0;
    }

    /**
     * Sollicite l'IA pour calculer le meilleur coup à conseiller au joueur dont c'est le tour.
     * Le joker n'est décompté que si un conseil a effectivement été produit.
     *
     * @param position   position courante (non modifiée par le calcul)
     * @param depth      profondeur de recherche
     * @param useAlphaBeta {@code true} pour l'élagage Alpha-Bêta, {@code false} pour Minimax
     * @return le coup conseillé, ou {@code null} s'il ne reste aucun joker ou aucun coup possible
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
