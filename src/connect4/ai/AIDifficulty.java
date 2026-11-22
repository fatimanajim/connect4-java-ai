package connect4.ai;

import java.io.Serializable;

/**
 * Niveaux de complexité et stratégies de recherche.
 * 
 * @author Profil 1 - Lead IA & Algorithmes
 */
public enum AIDifficulty implements Serializable {

    FACILE("Facile (Profondeur 2)", 2, Strategy.MINIMAX),
    MOYEN("Moyen (Profondeur 4)", 4, Strategy.ALPHABETA),
    DIFFICILE("Difficile (Profondeur 6)", 6, Strategy.ALPHABETA);

    public enum Strategy {
        ALPHABETA("Élagage Alpha-Bêta (Diapo 19)"),
        MINIMAX("Minimax standard");

        private final String label;
        Strategy(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    private final String label;
    private final int depth;
    private final Strategy strategy;

    AIDifficulty(String label, int depth, Strategy strategy) {
        this.label = label;
        this.depth = depth;
        this.strategy = strategy;
    }

    public String getLabel() { return label; }
    public int getDepth() { return depth; }
    public Strategy getStrategy() { return strategy; }

    @Override
    public String toString() {
        return label;
    }
}
