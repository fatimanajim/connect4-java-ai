import connect4.core.*;

/** Tests complémentaires du moteur : victoires dans les 4 directions, nul, copie, ordre des coups. */
public class Test4 {
    static int ok = 0, ko = 0;

    static void check(String nom, boolean condition) {
        System.out.println((condition ? "[OK]     " : "[ECHEC]  ") + nom);
        if (condition) ok++; else ko++;
    }

    public static void main(String[] args) {
        final int R = Connect4Position.RED, Y = Connect4Position.YELLOW;

        // 1. Victoire horizontale : R sur la ligne du bas (colonnes 0 à 3), J empilé au-dessus
        Connect4Position p = new Connect4Position();
        for (int c : new int[]{0, 0, 1, 1, 2, 2, 3}) p.drop(c);
        check("Victoire horizontale (rouge)", p.hasWon(R) && p.winner() == R);

        // 2. Victoire verticale
        p = new Connect4Position();
        for (int c : new int[]{0, 1, 0, 1, 0, 1, 0}) p.drop(c);
        check("Victoire verticale (rouge)", p.hasWon(R));

        // 3. Diagonale montante (bas-gauche vers haut-droite)
        p = new Connect4Position();
        p.board[5][0] = R; p.board[4][1] = R; p.board[3][2] = R; p.board[2][3] = R;
        check("Victoire diagonale montante", p.hasWon(R));

        // 4. Diagonale descendante (haut-gauche vers bas-droite)
        p = new Connect4Position();
        p.board[2][0] = Y; p.board[3][1] = Y; p.board[4][2] = Y; p.board[5][3] = Y;
        check("Victoire diagonale descendante (jaune)", p.hasWon(Y) && !p.hasWon(R));

        // 5. Trois pions alignés ne suffisent pas
        p = new Connect4Position();
        for (int c : new int[]{0, 1, 0, 1, 0}) p.drop(c);
        check("3 alignes : pas de victoire", !p.hasWon(R) && !p.isGameOver());

        // 6. Le jeu s'arrête après une victoire
        p = new Connect4Position();
        for (int c : new int[]{0, 0, 1, 1, 2, 2, 3}) p.drop(c);
        boolean refuse = false;
        try { p.drop(4); } catch (IllegalMoveException e) { refuse = true; }
        check("Coup refuse apres la fin de partie", refuse && p.isGameOver());

        // 7. Match nul : grille pleine sans alignement
        p = new Connect4Position();
        for (int r = 0; r < 6; r++)
            for (int c = 0; c < 7; c++) {
                boolean aBlock = (c <= 2 || c == 6);
                p.board[r][c] = ((r % 2 == 0) == aBlock) ? R : Y;
            }
        p.nbMoves = 42;
        check("Match nul", p.isFull() && p.winner() == Connect4Position.EMPTY && p.isGameOver());

        // 8. Copie profonde : modifier la copie ne change pas l'original
        p = new Connect4Position();
        p.drop(3);
        Connect4Position q = p.copy();
        q.drop(2);
        check("Copie profonde independante", p.nbMoves == 1 && q.nbMoves == 2 && p.board[5][2] == 0);

        // 9. Ordre des coups : le centre d'abord ; colonne pleine retiree de la liste
        p = new Connect4Position();
        boolean centre = p.legalMoves().get(0) == 3 && p.legalMoves().size() == 7;
        for (int i = 0; i < 6; i++) p.drop(3);
        check("legalMoves : centre d'abord, colonne pleine exclue",
              centre && p.legalMoves().size() == 6 && !p.legalMoves().contains(3));

        System.out.println("\nResultat : " + ok + " OK, " + ko + " echec(s)");
    }
}