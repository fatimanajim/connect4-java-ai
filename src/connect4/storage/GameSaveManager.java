package connect4.storage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/** 
 * Sauvegarde et restitution d'une partie dans le dossier "saves/" (sérialisation Java). 
 * Implémenté par le Profil 2.
 */
public class GameSaveManager {
    private static final String DIR = "saves";
    private static final String EXT = ".dat";

    /**
     * Sauvegarde l'état de la partie.
     * @param state état à écrire
     * @param name  nom de la sauvegarde (sans extension)
     * @throws IOException si l'écriture échoue
     */
    public static void save(GameState state, String name) throws IOException {
        File dir = new File(DIR);
        if (!dir.exists() && !dir.mkdirs())
            throw new IOException("Impossible de créer le dossier " + DIR);
        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(new File(dir, clean(name) + EXT)))) {
            out.writeObject(state);
        }
    }

    /**
     * Restitue une partie sauvegardée.
     * @throws IOException si le fichier est absent ou corrompu
     */
    public static GameState load(String name) throws IOException {
        File f = new File(DIR, clean(name) + EXT);
        if (!f.exists()) throw new FileNotFoundException("Sauvegarde introuvable : " + name);
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(f))) {
            return (GameState) in.readObject();
        } catch (ClassNotFoundException | ClassCastException e) {
            throw new IOException("Fichier de sauvegarde invalide.", e);
        }
    }

    /** @return les noms des sauvegardes disponibles (pour le menu "Charger") */
    public static List<String> listSaves() {
        List<String> names = new ArrayList<>();
        File[] files = new File(DIR).listFiles((d, n) -> n.endsWith(EXT));
        if (files != null)
            for (File f : files)
                names.add(f.getName().substring(0, f.getName().length() - EXT.length()));
        return names;
    }

    /** Retire les caractères dangereux d'un nom de fichier. */
    private static String clean(String name) { return name.replaceAll("[^A-Za-z0-9_-]", "_"); }
}
