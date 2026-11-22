package connect4.ai;

import java.io.Serializable;

/**
 * Enumeration definissant les niveaux de complexite et les strategies de recherche.
 * Repond directement a l'exigence du sujet :
 *   - "Fixer la complexite du jeu" (Profondeur de l'arbre de recherche)
 *   - "Fixer la strategie du jeu pour la machine et lors de la demande d'aide" (Alpha-Beta vs Minimax)
 * 
 * ==============================================================================================
 * ANALYSE DE COMPLEXITE THEORIQUE :
 * Le facteur de branchement moyen b du Puissance 4 est ~ 7 (7 colonnes).
 * Pour une profondeur d, le nombre maximal d'etats generes est de l'ordre de b^d :
 *   - Profondeur 2 (Facile)    : 7^2 = 49 noeuds (Temps de reponse : < 5 ms)
 *   - Profondeur 4 (Moyen)     : 7^4 = 2 401 noeuds (Temps de reponse : ~ 20-50 ms)
 *   - Profondeur 6 (Difficile) : 7^6 = 117 649 noeuds (Temps de reponse : ~ 200-600 ms avec elagage)
 * Grace a l'elagage Alpha-Beta (Slide 19), ce nombre est reduit d'environ 50 a 80%.
 * ==============================================================================================
 * 
 * @author Profil 1 - Lead IA & Algorithmes de Recherche
 */
public enum AIDifficulty implements Serializable {

    FACILE("Facile (Profondeur 2)", 2, Strategy.MINIMAX),
    MOYEN("Moyen (Profondeur 4)", 4, Strategy.ALPHABETA),
    DIFFICILE("Difficile (Profondeur 6)", 6, Strategy.ALPHABETA);

    /**
     * Enumeration interne des strategies algorithmiques disponibles.
     */
    public enum Strategy {
        ALPHABETA("Elagage Alpha-Beta (Diapo 19)"),
        MINIMAX("Minimax Standard (Sans elagage)");

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
        return label + " - " + strategy.getLabel();
    }
}
