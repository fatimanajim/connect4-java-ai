# Puissance 4 (Connect 4) - Intelligence Artificielle en Java

[![Java](https://img.shields.io/badge/Java-17%2B-orange.svg)](https://www.oracle.com/java/)
[![Module](https://img.shields.io/badge/Module-Intelligence%20Artificielle-blue.svg)]()
[![Enseignant](https://img.shields.io/badge/Enseignant-M'hamed%20AIT%20KBIR-green.svg)]()

> **Devoir 2 : Premier élément de module**  
> Implémentation du jeu Puissance 4 avec stratégies de recherche (Minimax, élagage Alpha-Bêta, fonction heuristique).

---

## 📌 Fonctionnalités implémentées

- **Règles du Puissance 4 :** Grille verticale de 7 colonnes et 6 lignes, respect strict de la gravité, alternance des pions (Rouge 'R' commence, Jaune 'J' ensuite), détection automatique de victoire (horizontal, vertical, diagonal) et de match nul.
- **Moteur IA & Algorithmes (Diapositive 19) :**
  - Refonte formelle de `GameSearch` : remplacement de `alphaBetaHelper` par les méthodes mutuellement récursives `max_Value(...)` et `min_Value(...)`.
  - Élagage Alpha-Bêta complet avec gestion de la profondeur limite et test d'arrêt.
  - Fonction d'évaluation heuristique avancée (pondération de la colonne centrale, détection des fenêtres de 4 cases, blocage prioritaire des menaces adverses).
- **Système d'Aide au joueur :** Possibilité de solliciter l'IA un nombre limité de fois (3 jokers) pour suggérer le coup optimal.
- **Modes de jeu :**
  1. *2 Joueurs Humains* (en local).
  2. *Solo (Humain vs Machine)*.
- **Gestion des erreurs :** Rejet des positionnements non autorisés (colonnes pleines, numéros hors bornes, saisies erronées).
- **Sauvegarde & Restitution :** Sauvegarde complète de la partie à tout moment sur fichier (.dat) et reprise ultérieure.

---

## 🏗️ Architecture du Projet

```text
connect4-java-ai/
├── src/connect4/
│   ├── core/                  # [Profil 2] Classes du cours & Moteur métier
│   │   ├── Position.java      # Classe abstraite de base
│   │   ├── Move.java          # Classe abstraite de base
│   │   ├── GameSearch.java    # Recherche de jeu (max_Value & min_Value)
│   │   ├── Connect4Position.java # Grille 7x6, gravité, alignements
│   │   └── Connect4Move.java  # Colonne choisie (0..6)
│   ├── ai/                    # [Profil 1] Moteur IA & Heuristiques
│   │   ├── Connect4Search.java # Extension de GameSearch pour Connect 4
│   │   ├── EvaluationFunction.java # Calcul des scores heuristiques
│   │   └── AIDifficulty.java  # Niveaux (Facile: 2, Moyen: 4, Difficile: 6)
│   ├── storage/               # [Profil 2] Persistance
│   │   ├── GameState.java     # Objet sérialisable de la partie
│   │   └── GameSaveManager.java # Sauvegarde/chargement sur disque
│   └── ui/                    # [Profil 3] Interface & Contrôleur
│       ├── Main.java          # Point d'entrée & Menus
│       ├── GameController.java # Boucle de jeu, saisies et messages
│       └── HintManager.java   # Compteur de jokers d'aide
├── docs/                      # Rapport et documentation
├── saves/                     # Dossier des parties enregistrées
└── bin/                       # Fichiers compilés .class
```

---

## 🚀 Compilation & Exécution

### 1. Compilation
```bash
# Depuis la racine du projet
javac -encoding UTF-8 -d bin src/connect4/core/*.java src/connect4/ai/*.java src/connect4/storage/*.java src/connect4/ui/*.java
```

### 2. Exécution
```bash
java -cp bin connect4.ui.Main
```

---

## 👥 Équipe de Travail (3 Profils)

- **Profil 1 (Lead IA) :** Refonte de `GameSearch` (`max_Value`, `min_Value`), heuristique d'évaluation, moteur d'aide, niveaux de complexité.
- **Profil 2 (Lead Métier & Persistance) :** Grille 7x6, gravité, détection de victoire, respect de la hiérarchie de classes, sauvegarde/restitution.
- **Profil 3 (Lead IHM, Intégration & Docs) :** Menus, interface utilisateur, gestion des jokers, tests croisés, rédaction du rapport.

---

## 📤 Comment pousser ce dépôt sur votre GitHub

1. Créez un nouveau dépôt vide sur votre compte GitHub (ex: `connect4-java-ai`).
2. Ouvrez un terminal dans ce dossier et exécutez :
```bash
git remote add origin https://github.com/VOTRE_NOM_UTILISATEUR/connect4-java-ai.git
git branch -M main
git push -u origin main
```
