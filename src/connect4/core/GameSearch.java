package connect4.core;

import java.util.List;

/**
 * Recherche adversiale à deux joueurs (Minimax / Alpha-Bêta), d'après les diapositives du cours.
 * <p>PROGRAM = joueur MAX (celui pour qui on cherche le meilleur coup), HUMAN = joueur MIN.
 * La méthode {@code alphaBetaHelper} est remplacée par {@link #max_Value} et {@link #min_Value} (Diapo 19).</p>
 * 
 * @author Profil 1 & Profil 2
 */
public abstract class GameSearch {
    /** Joueur MAX (la machine, ou le joueur qui demande de l'aide). */
    public static final boolean PROGRAM = true;
    /** Joueur MIN (l'adversaire). */
    public static final boolean HUMAN = false;
    /** Valeur d'une victoire (on retranche la profondeur pour préférer les victoires rapides). */
    public static final float WIN = 100000f;

    /** true = élagage alpha-bêta ; false = Minimax pur. */
    protected boolean pruning = true;
    /** Profondeur maximale de recherche (complexité du jeu). */
    protected int maxDepth = 4;
    private int nodesVisited;
    private float lastValue;

    // ---- À implémenter par le jeu concret (Connect4Search) ----

    /** @return true si la position est un match nul */
    public abstract boolean drawnPosition(Position p);

    /** @return true si {@code player} (PROGRAM ou HUMAN) a gagné */
    public abstract boolean wonPosition(Position p, boolean player);

    /** Fonction d'évaluation heuristique, du point de vue de MAX (grande valeur = bon pour MAX). */
    public abstract float positionEvaluation(Position p, boolean player);

    /** @return la liste des coups que {@code player} peut jouer depuis p */
    public abstract List<Move> possibleMoves(Position p, boolean player);

    /** @return la NOUVELLE position obtenue en jouant {@code move} (p n'est pas modifiée) */
    public abstract Position makeMove(Position p, boolean player, Move move);

    // ---- Paramétrage ----

    /** @param pruning true = alpha-bêta, false = Minimax pur */
    public void setPruning(boolean pruning) { this.pruning = pruning; }

    public boolean isPruning() { return pruning; }

    /** @param maxDepth profondeur maximale de recherche */
    public void setMaxDepth(int maxDepth) { this.maxDepth = maxDepth; }

    public int getMaxDepth() { return maxDepth; }

    /** @return nombre de nœuds visités lors de la dernière recherche */
    public int getNodesVisited() { return nodesVisited; }

    /** @return valeur du meilleur coup trouvé lors de la dernière recherche */
    public float getLastValue() { return lastValue; }

    /** Test de profondeur limite (recherche à profondeur fixe, cf. diapo "Minimax : amélioration"). */
    public boolean reachedMaxDepth(Position p, int depth) { return depth >= maxDepth; }

    // ---- Algorithme (Slide 19) ----

    /**
     * Racine de la recherche : choisit, pour MAX, le coup menant à l'enfant de plus haute valeur.
     *
     * @param p     position de départ (MAX doit jouer)
     * @param depth profondeur maximale de la recherche
     * @return le meilleur coup, ou null s'il n'y a aucun coup
     */
    public Move bestMove(Position p, int depth) {
        this.maxDepth = depth;
        nodesVisited = 0;
        Move best = null;
        float v = Float.NEGATIVE_INFINITY;
        float alpha = Float.NEGATIVE_INFINITY, beta = Float.POSITIVE_INFINITY;
        for (Move m : possibleMoves(p, PROGRAM)) {
            float s = min_Value(makeMove(p, PROGRAM, m), 1, alpha, beta);
            if (best == null || s > v) { v = s; best = m; }
            if (pruning) alpha = Math.max(alpha, v);
        }
        lastValue = v;
        return best;
    }

    /**
     * Valeur d'un nœud MAX (diapo 19 : MAX-VALUE avec α, β).
     *
     * @param p     position du nœud
     * @param depth profondeur courante
     * @param alpha meilleure valeur déjà garantie à MAX sur le chemin
     * @param beta  meilleure valeur déjà garantie à MIN sur le chemin
     * @return la valeur utilité du nœud
     */
    public float max_Value(Position p, int depth, float alpha, float beta) {
        nodesVisited++;
        if (isTerminal(p, depth)) return utility(p, depth);
        float v = Float.NEGATIVE_INFINITY;
        for (Move m : possibleMoves(p, PROGRAM)) {
            v = Math.max(v, min_Value(makeMove(p, PROGRAM, m), depth + 1, alpha, beta));
            if (pruning) {
                if (v >= beta) return v;      // coupe β
                alpha = Math.max(alpha, v);
            }
        }
        return v;
    }

    /**
     * Valeur d'un nœud MIN (diapo 19 : MIN-VALUE avec α, β).
     * Mêmes paramètres que {@link #max_Value}.
     */
    public float min_Value(Position p, int depth, float alpha, float beta) {
        nodesVisited++;
        if (isTerminal(p, depth)) return utility(p, depth);
        float v = Float.POSITIVE_INFINITY;
        for (Move m : possibleMoves(p, HUMAN)) {
            v = Math.min(v, max_Value(makeMove(p, HUMAN, m), depth + 1, alpha, beta));
            if (pruning) {
                if (v <= alpha) return v;     // coupe α
                beta = Math.min(beta, v);
            }
        }
        return v;
    }

    /** TERMINAL-TEST : victoire, nul ou profondeur limite atteinte. */
    protected boolean isTerminal(Position p, int depth) {
        return wonPosition(p, PROGRAM) || wonPosition(p, HUMAN)
                || drawnPosition(p) || reachedMaxDepth(p, depth);
    }

    /** UTILITY : valeur exacte si la partie est finie, sinon heuristique. */
    protected float utility(Position p, int depth) {
        if (wonPosition(p, PROGRAM)) return WIN - depth;
        if (wonPosition(p, HUMAN)) return -WIN + depth;
        if (drawnPosition(p)) return 0;
        return positionEvaluation(p, PROGRAM);
    }
}
