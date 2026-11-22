package connect4.ai;

import connect4.core.Connect4Move;
import connect4.core.Connect4Position;
import connect4.core.GameSearch;
import connect4.core.Move;
import connect4.core.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Spécialisation de GameSearch pour le jeu du Puissance 4.
 * Fait le lien entre le cadre de recherche abstrait et les règles concrètes du jeu.
 * Implémenté par le Profil 1 (Lead IA).
 * 
 * @author Profil 1 - Lead IA & Algorithmes
 */
public class Connect4Search extends GameSearch {

    /** Couleur du joueur pour qui on maximise l'utilité (PROGRAM) */
    private int maxPlayerColor = Connect4Position.YELLOW;

    public void setMaxPlayerColor(int color) {
        this.maxPlayerColor = color;
    }

    public int getMaxPlayerColor() {
        return maxPlayerColor;
    }

    @Override
    public boolean drawnPosition(Position p) {
        if (!(p instanceof Connect4Position)) return false;
        Connect4Position cp = (Connect4Position) p;
        return cp.isFull() && !cp.hasWon(Connect4Position.RED) && !cp.hasWon(Connect4Position.YELLOW);
    }

    @Override
    public boolean wonPosition(Position p, boolean player) {
        if (!(p instanceof Connect4Position)) return false;
        Connect4Position cp = (Connect4Position) p;
        int color = (player == PROGRAM) ? maxPlayerColor : Connect4Position.opponent(maxPlayerColor);
        return cp.hasWon(color);
    }

    @Override
    public float positionEvaluation(Position p, boolean player) {
        if (!(p instanceof Connect4Position)) return 0;
        Connect4Position cp = (Connect4Position) p;
        return EvaluationFunction.evaluate(cp, maxPlayerColor);
    }

    @Override
    public List<Move> possibleMoves(Position p, boolean player) {
        if (!(p instanceof Connect4Position)) return new ArrayList<>();
        Connect4Position cp = (Connect4Position) p;
        List<Move> moves = new ArrayList<>();
        // legalMoves() renvoie déjà les colonnes triées {3, 2, 4, 1, 5, 0, 6} pour optimiser l'élagage
        for (int col : cp.legalMoves()) {
            moves.add(new Connect4Move(col));
        }
        return moves;
    }

    @Override
    public Position makeMove(Position p, boolean player, Move move) {
        if (!(p instanceof Connect4Position) || !(move instanceof Connect4Move)) return p;
        Connect4Position cp = (Connect4Position) p;
        Connect4Move cm = (Connect4Move) move;
        return cp.play(cm.column);
    }

    /**
     * Recherche le meilleur coup à la racine de l'arbre.
     * Utilisé pour la Machine (tour IA) et pour le système d'Aide (Joker).
     * 
     * @param p Position courante
     * @param depth Profondeur de recherche
     * @param useAlphaBeta true pour Alpha-Bêta, false pour Minimax
     * @return Le meilleur Connect4Move
     */
    public Connect4Move computeBestMove(Connect4Position p, int depth, boolean useAlphaBeta) {
        this.maxPlayerColor = p.currentPlayer();
        this.setPruning(useAlphaBeta);
        this.setMaxDepth(depth);

        // 1. Coup gagnant immédiat en 1 coup
        for (int col : p.legalMoves()) {
            Connect4Position sim = p.play(col);
            if (sim.hasWon(maxPlayerColor)) {
                return new Connect4Move(col);
            }
        }

        // 2. Blocage immédiat d'une menace de victoire adverse au prochain coup
        int oppColor = Connect4Position.opponent(maxPlayerColor);
        for (int col : p.legalMoves()) {
            Connect4Position simOpp = p.copy();
            for (int r = Connect4Position.ROWS - 1; r >= 0; r--) {
                if (simOpp.board[r][col] == Connect4Position.EMPTY) {
                    simOpp.board[r][col] = oppColor;
                    break;
                }
            }
            if (simOpp.hasWon(oppColor)) {
                return new Connect4Move(col); // Blocage vital
            }
        }

        // 3. Recherche arborescente (Alpha-Bêta Slide 19 ou Minimax)
        Move m = bestMove(p, depth);
        if (m instanceof Connect4Move) {
            return (Connect4Move) m;
        }

        // Repli sur le premier coup légal
        List<Integer> legal = p.legalMoves();
        return legal.isEmpty() ? null : new Connect4Move(legal.get(0));
    }
}
