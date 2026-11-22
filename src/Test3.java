import connect4.core.*;
import java.util.*;

public class Test3 {
    static class TreePos extends Position { final int id; TreePos(int id) { this.id = id; } }
    static class TreeMove extends Move { final int idx; TreeMove(int i) { idx = i; } }

    /** Arbre de la diapo : racine MAX (id 0), 3 nœuds MIN (id 1..3), 9 feuilles (id 4..12). */
    static final int[] LEAVES = {3, 12, 8, 2, 4, 6, 14, 5, 2};

    static class TreeGame extends GameSearch {
        public boolean drawnPosition(Position p) { return false; }
        public boolean wonPosition(Position p, boolean player) { return false; }
        public float positionEvaluation(Position p, boolean player) { return LEAVES[((TreePos) p).id - 4]; }
        public List<Move> possibleMoves(Position p, boolean player) {
            List<Move> l = new ArrayList<>();
            if (((TreePos) p).id <= 3) for (int i = 0; i < 3; i++) l.add(new TreeMove(i));
            return l;
        }
        public Position makeMove(Position p, boolean player, Move m) {
            int id = ((TreePos) p).id, i = ((TreeMove) m).idx;
            return new TreePos(id == 0 ? 1 + i : 4 + 3 * (id - 1) + i);
        }
    }

    public static void main(String[] args) {
        TreeGame g = new TreeGame();
        for (boolean pruning : new boolean[]{false, true}) {
            g.setPruning(pruning);
            TreeMove m = (TreeMove) g.bestMove(new TreePos(0), 2);
            System.out.println((pruning ? "Alpha-beta" : "Minimax   ")
                    + " : action A" + (m.idx + 1) + ", valeur = " + g.getLastValue()
                    + ", noeuds visites = " + g.getNodesVisited());
        }
    }
}