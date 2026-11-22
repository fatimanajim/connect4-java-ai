package connect4.core;

/**
 * Classe abstraite GameSearch : Cadre algorithmique pour les jeux a deux joueurs a somme nulle.
 * Conforme a la structure donnee en cours d'Intelligence Artificielle.
 * 
 * ==============================================================================================
 * MODIFICATION EXPLICITE DEVOIR 2 (Enseignant : M'hamed AIT KBIR - Diapositive 19) :
 * Remplacement de l'ancienne methode 'alphaBetaHelper' par les deux methodes mutuellement 
 * recursives standard de l'algorithme d'elagage Alpha-Beta :
 *   1. max_Value(Position p, float alpha, float beta, int depth)
 *   2. min_Value(Position p, float alpha, float beta, int depth)
 * ==============================================================================================
 * 
 * @author Profil 1 - Lead IA & Algorithmes de Recherche
 */
public abstract class GameSearch {

    /** Constante representant la Machine (Maximiseur) */
    public static final boolean PROGRAM = false;

    /** Constante representant le Joueur Humain (Minimiseur) */
    public static final boolean HUMAN = true;

    // =========================================================================
    // METHODES ABSTRAITES DU CONTRAT METIER
    // =========================================================================

    /**
     * Verifie si la position actuelle est un match nul (grille pleine sans vainqueur).
     * @param p Position courante
     * @return true si match nul
     */
    public abstract boolean drawnPosition(Position p);

    /**
     * Verifie si le joueur specifie a remporte la victoire dans la position donnee.
     * @param p Position courante
     * @param player PROGRAM (false) ou HUMAN (true)
     * @return true si le joueur a gagne
     */
    public abstract boolean wonPosition(Position p, boolean player);

    /**
     * Calcule l'estimation heuristique de la position pour le joueur donne.
     * @param p Position courante
     * @param player Joueur a evaluer
     * @return Score numerique (positif = avantage, negatif = desavantage)
     */
    public abstract float positionEvaluation(Position p, boolean player);

    /**
     * Affiche textuellement la position.
     * @param p Position a afficher
     */
    public abstract void printPosition(Position p);

    /**
     * Genere tous les etats successeurs valides a partir de la position courante.
     * @param p Position courante
     * @param player Joueur actif pour ce coup
     * @return Tableau des positions atteignables
     */
    public abstract Position[] possibleMoves(Position p, boolean player);

    /**
     * Applique un coup et retourne la nouvelle position resultante.
     * @param p Position courante
     * @param player Joueur effectuant le coup
     * @param move Coup a appliquer
     * @return Nouvelle position
     */
    public abstract Position makeMove(Position p, boolean player, Move move);

    /**
     * Test d'arret en profondeur : verifie si la profondeur maximale de recherche est atteinte.
     * @param p Position courante
     * @param depth Profondeur actuelle dans l'arbre
     * @return true si depth >= maxDepth
     */
    public abstract boolean reachedMaxDepth(Position p, int depth);


    // =========================================================================
    // IMPLEMENTATION DE L'ELAGAGE ALPHA-BETA (DIAPOSITIVE 19)
    // =========================================================================

    /**
     * Fonction max_Value (Diapositive 19 du cours) :
     * Maximise l'utilite pour le joueur PROGRAM en appliquant les coupures Beta.
     * 
     * Principe :
     * A chaque etage MAX, on cherche la plus grande valeur 'v' parmi tous les fils.
     * Si cette valeur depasse ou egale 'beta' (le pire score que MIN au-dessus est pret a accepter),
     * MIN ne choisira jamais cette branche : on effectue un elagage Beta (coupure immediate).
     * 
     * @param p Position courante
     * @param alpha Meilleur choix trouve jusqu'ici sur le chemin pour MAX (borne inferieure)
     * @param beta Meilleur choix trouve jusqu'ici sur le chemin pour MIN (borne superieure)
     * @param depth Profondeur courante dans l'arbre
     * @return Valeur maximale estimee
     */
    public float max_Value(Position p, float alpha, float beta, int depth) {
        // 1. Test d'arret : feuille terminale (victoire/nul) ou limite de profondeur
        if (reachedMaxDepth(p, depth) || wonPosition(p, PROGRAM) || wonPosition(p, HUMAN) || drawnPosition(p)) {
            return positionEvaluation(p, PROGRAM);
        }

        float v = Float.NEGATIVE_INFINITY;
        Position[] successors = possibleMoves(p, PROGRAM);

        // 2. Exploration des etats successeurs
        for (Position succ : successors) {
            // Appel mutuellement recursif a min_Value pour simuler la reponse optimale adverse
            v = Math.max(v, min_Value(succ, alpha, beta, depth + 1));

            // 3. Coupure Beta : inutile d'explorer les autres freres
            if (v >= beta) {
                return v;
            }

            // Mise a jour de la borne alpha
            alpha = Math.max(alpha, v);
        }
        return v;
    }

    /**
     * Fonction min_Value (Diapositive 19 du cours) :
     * Minimise l'utilite pour contrer le joueur HUMAN en appliquant les coupures Alpha.
     * 
     * Principe :
     * A chaque etage MIN, l'adversaire cherche a minimiser notre score.
     * Si la valeur 'v' devient inferieure ou egale a 'alpha' (le score garanti pour MAX au-dessus),
     * MAX ne permettra jamais d'atteindre ce noeud : on effectue un elagage Alpha (coupure immediate).
     * 
     * @param p Position courante
     * @param alpha Borne inferieure actuelle
     * @param beta Borne superieure actuelle
     * @param depth Profondeur courante dans l'arbre
     * @return Valeur minimale estimee
     */
    public float min_Value(Position p, float alpha, float beta, int depth) {
        // 1. Test d'arret : feuille terminale ou limite de profondeur
        if (reachedMaxDepth(p, depth) || wonPosition(p, PROGRAM) || wonPosition(p, HUMAN) || drawnPosition(p)) {
            return positionEvaluation(p, PROGRAM);
        }

        float v = Float.POSITIVE_INFINITY;
        Position[] successors = possibleMoves(p, HUMAN);

        // 2. Exploration des etats successeurs
        for (Position succ : successors) {
            // Appel mutuellement recursif a max_Value
            v = Math.min(v, max_Value(succ, alpha, beta, depth + 1));

            // 3. Coupure Alpha : inutile d'explorer les autres freres
            if (v <= alpha) {
                return v;
            }

            // Mise a jour de la borne beta
            beta = Math.min(beta, v);
        }
        return v;
    }

    /**
     * Algorithme Minimax naif (sans coupures alpha-beta).
     * Permet de comparer experimentalement les performances et le nombre de noeuds visites.
     * 
     * @param p Position a evaluer
     * @param player Joueur courant
     * @param depth Profondeur actuelle
     * @param isMax true si etage MAX, false si etage MIN
     * @return Score evalue
     */
    public float minimax(Position p, boolean player, int depth, boolean isMax) {
        if (reachedMaxDepth(p, depth) || wonPosition(p, PROGRAM) || wonPosition(p, HUMAN) || drawnPosition(p)) {
            return positionEvaluation(p, PROGRAM);
        }

        if (isMax) {
            float maxEval = Float.NEGATIVE_INFINITY;
            for (Position succ : possibleMoves(p, PROGRAM)) {
                float eval = minimax(succ, player, depth + 1, false);
                maxEval = Math.max(maxEval, eval);
            }
            return maxEval;
        } else {
            float minEval = Float.POSITIVE_INFINITY;
            for (Position succ : possibleMoves(p, HUMAN)) {
                float eval = minimax(succ, player, depth + 1, true);
                minEval = Math.min(minEval, eval);
            }
            return minEval;
        }
    }
}
