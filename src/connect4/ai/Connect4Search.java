package connect4.ai;

import connect4.core.Connect4Move;
import connect4.core.Connect4Position;
import connect4.core.GameSearch;
import connect4.core.Move;
import connect4.core.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe concrete Connect4Search : fait le pont entre le framework abstrait GameSearch 
 * et la logique specifique du Puissance 4.
 * 
 * Responsabilites du Profil 1 :
 * 1. Implementer les methodes abstraites heritees de GameSearch.
 * 2. Ordonnancer l'exploration des colonnes (centre d'abord : 3, 2, 4, 1, 5, 0, 6) 
 *    pour maximiser l'efficacite des coupures Alpha et Beta.
 * 3. Fournir la methode de calcul du meilleur coup tant pour la Machine que pour l'Aide au joueur.
 * 
 * @author Profil 1 - Lead IA & Algorithmes de Recherche
 */
public class Connect4Search extends GameSearch {

    /** Profondeur maximale de recherche dynamique */
    private int searchMaxDepth = 4;

    /** Ordre d'exploration optimise : du centre vers les extremites */
    private static final int[] OPTIMAL_COLUMN_ORDER = {3, 2, 4, 1, 5, 0, 6};

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
        // Exploration par ordre de colonnes optimise (favorise le centre pour les coupures alpha-beta)
        for (int col : OPTIMAL_COLUMN_ORDER) {
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
     * Calcule le meilleur coup a la racine de l'arbre de recherche.
     * 
     * Cette methode sert a la fois pour :
     * 1. Le tour de la MACHINE (forPlayer == PROGRAM, false) : recherche du coup maximisant l'utilite IA.
     * 2. La demande d'AIDE AU JOUEUR (forPlayer == HUMAN, true) : recherche du coup optimal pour le joueur humain.
     * 
     * @param pos Position courante de la grille
     * @param forPlayer PROGRAM (false) pour la machine, HUMAN (true) pour le joueur
     * @param depth Profondeur maximale d'anticipation
     * @param useAlphaBeta true pour activer l'elagage Alpha-Beta (Slide 19), false pour Minimax classique
     * @return Le Connect4Move le plus pertinent calcule
     */
    public Connect4Move computeBestMove(Connect4Position pos, boolean forPlayer, int depth, boolean useAlphaBeta) {
        this.searchMaxDepth = depth;
        int playerPiece = (forPlayer == HUMAN) ? Connect4Position.RED : Connect4Position.YELLOW;
        int opponentPiece = (playerPiece == Connect4Position.RED) ? Connect4Position.YELLOW : Connect4Position.RED;

        Connect4Move bestMove = null;
        float bestVal = (forPlayer == PROGRAM) ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY;

        // Etape 1 : Verification prioritaire d'un coup gagnant immediat en 1 coup
        for (int col : OPTIMAL_COLUMN_ORDER) {
            if (pos.isValidMove(col)) {
                Connect4Position simulated = pos.playMove(col, playerPiece);
                if (simulated.checkWin(playerPiece)) {
                    return new Connect4Move(col); // Coup gagnant direct trouve
                }
            }
        }

        // Etape 2 : Verification prioritaire de blocage d'une victoire immediate adverse en 1 coup
        for (int col : OPTIMAL_COLUMN_ORDER) {
            if (pos.isValidMove(col)) {
                Connect4Position opponentWinning = pos.playMove(col, opponentPiece);
                if (opponentWinning.checkWin(opponentPiece)) {
                    return new Connect4Move(col); // Blocage vital immediat
                }
            }
        }

        // Etape 3 : Exploration arborescente (Alpha-Beta Slide 19 ou Minimax)
        for (int col : OPTIMAL_COLUMN_ORDER) {
            if (pos.isValidMove(col)) {
                Connect4Position succ = pos.playMove(col, playerPiece);
                float val;

                if (forPlayer == PROGRAM) {
                    // La machine cherche a maximiser ; on appelle min_Value au niveau suivant
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
                    // L'humain (demande d'aide) cherche a maximiser ses chances
                    if (useAlphaBeta) {
                        val = max_Value(succ, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, 1);
                    } else {
                        val = minimax(succ, PROGRAM, 1, true);
                    }

                    if (val < bestVal || bestMove == null) {
                        bestVal = val;
                        bestMove = new Connect4Move(col);
                    }
                }
            }
        }

        // Repli de secours : retourner la premiere colonne valide disponible
        if (bestMove == null) {
            for (int col = 0; col < Connect4Position.COLS; col++) {
                if (pos.isValidMove(col)) {
                    return new Connect4Move(col);
                }
            }
        }

        return bestMove;
    }
}
