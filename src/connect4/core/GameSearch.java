package connect4.core;

import java.util.Vector;

/**
 * Classe abstraite GameSearch fournissant le cadre générique pour les jeux à deux joueurs à somme nulle.
 * 
 * NOTE IMPORTANTE (Devoir 2 - Enseignant M'hamed AIT KBIR) :
 * Conformément aux consignes et à la diapositive 19 du cours, la méthode initiale
 * 'alphaBetaHelper' a été supprimée et remplacée par les deux méthodes mutuellement récursives :
 * - max_Value(Position p, float alpha, float beta, int depth)
 * - min_Value(Position p, float alpha, float beta, int depth)
 * 
 * @author Groupe Devoir 2 - IA
 */
public abstract class GameSearch {

    public static final boolean PROGRAM = false; // Machine (Maximiseur)
    public static final boolean HUMAN = true;     // Humain (Minimiseur)

    /**
     * Vérifie si la position actuelle est une position de match nul.
     */
    public abstract boolean drawnPosition(Position p);

    /**
     * Vérifie si le joueur spécifié a gagné la partie dans la position donnée.
     */
    public abstract boolean wonPosition(Position p, boolean player);

    /**
     * Calcule l'évaluation heuristique de la position pour le joueur spécifié.
     */
    public abstract float positionEvaluation(Position p, boolean player);

    /**
     * Affiche l'état courant de la position.
     */
    public abstract void printPosition(Position p);

    /**
     * Génère l'ensemble des positions successeurs possibles à partir de la position courante.
     */
    public abstract Position[] possibleMoves(Position p, boolean player);

    /**
     * Applique un mouvement à la position courante et retourne la nouvelle position.
     */
    public abstract Position makeMove(Position p, boolean player, Move move);

    /**
     * Détermine si la profondeur limite de recherche a été atteinte.
     */
    public abstract boolean reachedMaxDepth(Position p, int depth);

    // =========================================================================
    // MODIFICATION DEVOIR 2 : Diapositive 19 (Remplacement de alphaBetaHelper)
    // =========================================================================

    /**
     * Fonction max_Value de l'algorithme d'élagage Alpha-Bêta (Diapositive 19).
     * Maximise l'utilité pour le joueur PROGRAM en effectuant les coupures Bêta.
     * 
     * @param p La position actuelle
     * @param alpha Borne inférieure actuelle
     * @param beta Borne supérieure actuelle
     * @param depth Profondeur courante dans l'arbre de recherche
     * @return La meilleure valeur maximale estimée
     */
    public float max_Value(Position p, float alpha, float beta, int depth) {
        // Test d'arrêt : nœud terminal ou profondeur maximale atteinte
        if (reachedMaxDepth(p, depth) || wonPosition(p, PROGRAM) || wonPosition(p, HUMAN) || drawnPosition(p)) {
            return positionEvaluation(p, PROGRAM);
        }

        float v = Float.NEGATIVE_INFINITY;
        Position[] moves = possibleMoves(p, PROGRAM);

        for (Position succ : moves) {
            v = Math.max(v, min_Value(succ, alpha, beta, depth + 1));
            if (v >= beta) {
                return v; // Élagage Bêta (Coupure Bêta)
            }
            alpha = Math.max(alpha, v);
        }
        return v;
    }

    /**
     * Fonction min_Value de l'algorithme d'élagage Alpha-Bêta (Diapositive 19).
     * Minimise l'utilité pour le joueur adverse (HUMAN) en effectuant les coupures Alpha.
     * 
     * @param p La position actuelle
     * @param alpha Borne inférieure actuelle
     * @param beta Borne supérieure actuelle
     * @param depth Profondeur courante dans l'arbre de recherche
     * @return La valeur minimale estimée
     */
    public float min_Value(Position p, float alpha, float beta, int depth) {
        // Test d'arrêt : nœud terminal ou profondeur maximale atteinte
        if (reachedMaxDepth(p, depth) || wonPosition(p, PROGRAM) || wonPosition(p, HUMAN) || drawnPosition(p)) {
            return positionEvaluation(p, PROGRAM);
        }

        float v = Float.POSITIVE_INFINITY;
        Position[] moves = possibleMoves(p, HUMAN);

        for (Position succ : moves) {
            v = Math.min(v, max_Value(succ, alpha, beta, depth + 1));
            if (v <= alpha) {
                return v; // Élagage Alpha (Coupure Alpha)
            }
            beta = Math.min(beta, v);
        }
        return v;
    }

    /**
     * Algorithme Minimax classique sans élagage (pour comparaison de stratégies).
     */
    public float minimax(Position p, boolean player, int depth, boolean isMax) {
        if (reachedMaxDepth(p, depth) || wonPosition(p, PROGRAM) || wonPosition(p, HUMAN) || drawnPosition(p)) {
            return positionEvaluation(p, PROGRAM);
        }

        if (isMax) {
            float maxEval = Float.NEGATIVE_INFINITY;
            Position[] moves = possibleMoves(p, PROGRAM);
            for (Position succ : moves) {
                float eval = minimax(succ, player, depth + 1, false);
                maxEval = Math.max(maxEval, eval);
            }
            return maxEval;
        } else {
            float minEval = Float.POSITIVE_INFINITY;
            Position[] moves = possibleMoves(p, HUMAN);
            for (Position succ : moves) {
                float eval = minimax(succ, player, depth + 1, true);
                minEval = Math.min(minEval, eval);
            }
            return minEval;
        }
    }
}
