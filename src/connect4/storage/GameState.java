package connect4.storage;

import connect4.ai.AIDifficulty;
import connect4.core.Connect4Position;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Objet de transfert de données (DTO) encapsulant l'état complet d'une partie pour la sauvegarde.
 * Permet de restituer fidèlement la partie sans perte d'information.
 * 
 * @author Groupe Devoir 2 - IA
 */
public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int MODE_HUMAN_VS_HUMAN = 1;
    public static final int MODE_HUMAN_VS_AI = 2;

    private final Connect4Position position;
    private final int currentPlayer;
    private final int gameMode;
    private final AIDifficulty difficulty;
    private final int hintsRemaining;
    private final long saveTimestamp;

    public GameState(Connect4Position position, int currentPlayer, int gameMode, AIDifficulty difficulty, int hintsRemaining) {
        this.position = position;
        this.currentPlayer = currentPlayer;
        this.gameMode = gameMode;
        this.difficulty = difficulty;
        this.hintsRemaining = hintsRemaining;
        this.saveTimestamp = System.currentTimeMillis();
    }

    public Connect4Position getPosition() { return position; }
    public int getCurrentPlayer() { return currentPlayer; }
    public int getGameMode() { return gameMode; }
    public AIDifficulty getDifficulty() { return difficulty; }
    public int getHintsRemaining() { return hintsRemaining; }
    public long getSaveTimestamp() { return saveTimestamp; }

    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return sdf.format(new Date(saveTimestamp));
    }
}
