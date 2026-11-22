package connect4.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestionnaire de persistance pour sauvegarder et restituer l'état d'une partie.
 * Gère l'écriture et la lecture de fichiers binaires sérialisés (.dat).
 * 
 * @author Groupe Devoir 2 - IA
 */
public class GameSaveManager {

    private static final String DEFAULT_SAVE_DIR = "saves";

    public GameSaveManager() {
        File dir = new File(DEFAULT_SAVE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * Sauvegarde une partie sous le nom de fichier spécifié.
     */
    public boolean save(GameState state, String filename) {
        if (!filename.endsWith(".dat")) {
            filename += ".dat";
        }
        File targetFile = new File(DEFAULT_SAVE_DIR, filename);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(targetFile))) {
            oos.writeObject(state);
            return true;
        } catch (Exception e) {
            System.err.println("Erreur lors de la sauvegarde : " + e.getMessage());
            return false;
        }
    }

    /**
     * Restitue une partie sauvegardée.
     */
    public GameState load(String filename) {
        if (!filename.endsWith(".dat")) {
            filename += ".dat";
        }
        File targetFile = new File(DEFAULT_SAVE_DIR, filename);

        if (!targetFile.exists()) {
            System.err.println("Fichier de sauvegarde introuvable : " + targetFile.getAbsolutePath());
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(targetFile))) {
            return (GameState) ois.readObject();
        } catch (Exception e) {
            System.err.println("Erreur lors de la restitution de la partie : " + e.getMessage());
            return null;
        }
    }

    /**
     * Liste l'ensemble des fichiers de sauvegarde disponibles.
     */
    public List<String> listSaves() {
        List<String> saveFiles = new ArrayList<>();
        File dir = new File(DEFAULT_SAVE_DIR);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".dat"));

        if (files != null) {
            for (File file : files) {
                saveFiles.add(file.getName());
            }
        }
        return saveFiles;
    }
}
