import connect4.core.*;
import connect4.storage.*;

public class Test2 {
    public static void main(String[] args) throws Exception {
        // 1. Gravité + alternance
        Connect4Position p = new Connect4Position();
        System.out.println("Ligne du 1er pion : " + p.drop(0) + " (attendu 5)");
        System.out.println("Joueur courant : " + p.currentPlayer() + " (attendu 2 = jaune)");

        // 2. Victoire verticale : R en col 0, J en col 1
        p = new Connect4Position();
        for (int i = 0; i < 3; i++) { p.drop(0); p.drop(1); }
        p.drop(0);
        System.out.println("Rouge gagne (vertical) : " + p.hasWon(Connect4Position.RED) + " (attendu true)");

        // 3. Erreurs
        try { p.drop(3); } catch (IllegalMoveException e) { System.out.println("OK : " + e.getMessage()); }
        p = new Connect4Position();
        try { p.drop(9); } catch (IllegalMoveException e) { System.out.println("OK : " + e.getMessage()); }
        for (int i = 0; i < 6; i++) p.drop(0);
        try { p.drop(0); } catch (IllegalMoveException e) { System.out.println("OK : " + e.getMessage()); }

        // 4. Match nul (motif sans alignement de 4)
        p = new Connect4Position();
        for (int r = 0; r < 6; r++)
            for (int c = 0; c < 7; c++) {
                boolean aBlock = (c <= 2 || c == 6);
                p.board[r][c] = ((r % 2 == 0) == aBlock) ? Connect4Position.RED : Connect4Position.YELLOW;
            }
        p.nbMoves = 42;
        System.out.println("Nul : plein=" + p.isFull() + ", gagnant=" + p.winner() + " (attendu 0)");

        // 5. Sauvegarde / restitution
        p = new Connect4Position();
        p.drop(3); p.drop(4);
        GameState s = new GameState();
        s.position = p; s.gameMode = "HUMAIN_VS_IA"; s.difficulty = "MOYEN";
        s.strategy = "ALPHA_BETA"; s.hintsLeft = 2; s.humanColor = Connect4Position.RED;
        GameSaveManager.save(s, "test");
        GameState l = GameSaveManager.load("test");
        System.out.println(l.position);
        System.out.println("Aides restantes : " + l.hintsLeft + ", coups : " + l.position.nbMoves);

        // 6. Signatures du Profil 1
        Connect4Position q = new Connect4Position();
        Connect4Position q2 = q.playMove(3, Connect4Position.RED);
        System.out.println("q inchangée : " + q.nbMoves + " (attendu 0), q2 : " + q2.nbMoves + " (attendu 1)");
        System.out.println("isValidMove(7) : " + q.isValidMove(7) + " (attendu false)");
        try { q.playMove(3, Connect4Position.YELLOW); }
        catch (IllegalMoveException e) { System.out.println("OK : " + e.getMessage()); }
    }
}