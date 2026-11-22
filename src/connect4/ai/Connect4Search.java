package connect4.ai;

import connect4.core.Connect4Move;
import connect4.core.Connect4Position;
import connect4.core.GameSearch;
import connect4.core.Move;
import connect4.core.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation concrète de GameSearch pour le jeu du Puissance 4.
 * Fait le pont entre la théorie vue en cours (GameSearch, max_Value, min_Value)
 * et le moteur du Puissance 4 (Connect4Position).
 * 
 * @author Groupe Devoir 2 - IA
 */
public class Connect4Search extends GameSearch {

    private int searchMaxDepth = 4; // Profondeur par défaut (Moyen)

    public void setSearchMaxDepth(int depth) {
        this.searchMaxDepth = depth;
    }

    public int getSearchMaxDepth() {
        return searchMaxDepth;
    }

    @Override
    public boolean drawnPosition(Position p) {
        if (!(p instanceof Connect4Position)) return false;
        Connect4Position pos = (Connect4Position) p;
        return pos.isFull() && !pos.checkWin(Connect4Position.RED) && !pos.checkWin(Connect4Position.YELLOW);
    }

    @Override
    public boolean wonPosition(Position p, boolean player) {
        if (!(p instanceof Connect4Position)) return false;
        Connect4Position pos = (Connect4Position) p;
        int piece = (player == HUMAN) ? Connect4Position.RED : Connect4Position.YELLOW;
        return pos.checkWin(piece);
    }

    @Override
    public float positionEvaluation(Position p, boolean player) {
        if (!(p instanceof Connect4Position)) return 0;
        Connect4Position pos = (Connect4Position) p;
        int piece = (player == HUMAN) ? Connect4Position.RED : Connect4Position.YELLOW;
        return EvaluationFunction.evaluate(pos, piece);
    }

    @Override
    public void printPosition(Position p) {
        if (p instanceof Connect4Position) {
            System.out.println(p.toString());
        }
    }

    @Override
    public Position[] possibleMoves(Position p, boolean player) {
        if (!(p instanceof Connect4Position)) return new Position[0];
        Connect4Position pos = (Connect4Position) p;
        int piece = (player == HUMAN) ? Connect4Position.RED : Connect4Position.YELLOW;

        List<Position> successors = new ArrayList<>();
        // Parcourir les colonnes (dans l'ordre 3, 2, 4, 1, 5, 0, 6 pour optimiser l'élagage alpha-bêta)
        int[] columnOrder = {3, 2, 4, 1, 5, 0, 6};
        for (int col : columnOrder) {
            if (pos.isValidMove(col)) {
                successors.add(pos.playMove(col, piece));
            }
        }
        return successors.toArray(new Position[0]);
    }

    @Override
    public Position makeMove(Position p, boolean player, Move move) {
        if (!(p instanceof Connect4Position) || !(move instanceof Connect4Move)) return p;
        Connect4Position pos = (Connect4Position) p;
        Connect4Move c4Move = (Connect4Move) move;
        int piece = (player == HUMAN) ? Connect4Position.RED : Connect4Position.YELLOW;
        return pos.playMove(c4Move.getColumn(), piece);
    }

    @Override
    public boolean reachedMaxDepth(Position p, int depth) {
        return depth >= searchMaxDepth;
    }

    /**
     * Recherche le meilleur coup à jouer pour le joueur donné en utilisant
     * la stratégie et la profondeur spécifiées.
     * 
     * @param pos Position actuelle
     * @param forPlayer Joueur qui doit jouer (PROGRAM pour la machine, HUMAN pour l'aide au joueur)
     * @param depth Profondeur maximale
     * @param useAlphaBeta true pour Alpha-Bêta (Slide 19), false pour Minimax
     * @return Le meilleur Connect4Move calculé
     */
    public Connect4Move computeBestMove(Connect4Position pos, boolean forPlayer, int depth, boolean useAlphaBeta) {
        this.searchMaxDepth = depth;
        int piece = (forPlayer == HUMAN) ? Connect4Position.RED : Connect4Position.YELLOW;

        Connect4Move bestMove = null;
        float bestVal = (forPlayer == PROGRAM) ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY;

        // Ordre d'exploration favorisant le centre pour de meilleurs élagages
        int[] columnOrder = {3, 2, 4, 1, 5, 0, 6};

        for (int col : columnOrder) {
            if (pos.isValidMove(col)) {
                Connect4Position succ = pos.playMove(col, piece);

                // Victoire immédiate en 1 coup
                if (succ.checkWin(piece)) {
                    return new Connect4Move(col);
                }

                float val;
                if (forPlayer == PROGRAM) {
                    if (useAlphaBeta) {
                        val = min_Value(succ, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, 1);
                    } else {
                        val = minimax(succ, HUMAN, 1, false);
                    }
                    if (val > bestVal || bestMove == null) {
                        bestVal = val;
                        bestMove = new Connect4Move(col);
                    }
                } else {
                    // Pour le joueur humain (mode Aide)
                    if (useAlphaBeta) {
                        val = max_Value(succ, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, 1);
                    } else {
                        val = minimax(succ, PROGRAM, 1, true);
                    }
                    // Pour l'humain, on cherche à maximiser son propre score
                    if (val < bestVal || bestMove == null) {
                        bestVal = val;
                        bestMove = new Connect4Move(col);
                    }
                }
            }
        }

        return bestMove;
    }
}
