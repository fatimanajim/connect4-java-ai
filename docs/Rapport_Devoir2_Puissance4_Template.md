# RAPPORT DE PROJET - DEVOIR 2 : PUISSANCE 4 (CONNECT 4)
**Module :** Intelligence Artificielle  
**Enseignant :** M'hamed AIT KBIR  
**Date limite :** 26 nov. 23:59  
**Membres du groupe :**
- [Nom Prénom 1] - Profil 1 : Moteur IA & Stratégies de Recherche
- [Nom Prénom 2] - Profil 2 : Architecte Logiciel, Moteur Métier & Persistance
- [Nom Prénom 3] - Profil 3 : Interface Utilisateur, Intégration & Assurance Qualité

---

## 1. Introduction & Cahier des Charges
- Présentation du jeu Puissance 4 (grille verticale 7x6, gravité, 4 pions consécutifs pour gagner, match nul si grille pleine).
- Objectifs pédagogiques : application des algorithmes de recherche dans les jeux à deux joueurs à somme nulle (Minimax et élagage Alpha-Bêta).
- Exigences satisfaites :
  - Modification de `GameSearch` (Slide 19 : `max_Value` et `min_Value`).
  - Système d'aide au joueur avec limitation d'utilisation (jokers).
  - Gestion de la complexité (profondeur) et choix de stratégie.
  - Modes 2 joueurs humains et humain vs machine.
  - Sauvegarde et restitution.
  - Rejet des coups non autorisés (colonnes pleines).

---

## 2. Architecture Logicielle & Hiérarchie des Classes (Profil 2)
- Conformité avec les classes vues en cours :
  - `Position` (abstraite) $\rightarrow$ `Connect4Position` (concrète)
  - `Move` (abstraite) $\rightarrow$ `Connect4Move` (concrète)
  - `GameSearch` (abstraite) $\rightarrow$ `Connect4Search` (concrète)
- Gestion de la grille et de la gravité : descente des pions et détection des victoires (horizontale, verticale, diagonales).
- Persistance : Sérialisation Java (`GameState` et `GameSaveManager`).

---

## 3. Moteur IA & Algorithmes de Recherche (Profil 1)
### 3.1. Refonte de `GameSearch` (Diapositive 19)
- Explication du remplacement de `alphaBetaHelper` par les deux fonctions récursives :
  - `max_Value(Position p, float alpha, float beta, int depth)` : maximisation du joueur PROGRAM avec coupures $\beta$.
  - `min_Value(Position p, float alpha, float beta, int depth)` : minimisation du joueur HUMAN avec coupures $\alpha$.
### 3.2. Fonction d'évaluation Heuristique (`EvaluationFunction`)
- Valorisation de la colonne centrale (colonne 3) : +6 points par pion.
- Évaluation des fenêtres de 4 cases :
  - 3 pions alliés + 1 vide : +50
  - 2 pions alliés + 2 vides : +10
  - 3 pions adverses + 1 vide : -80 (blocage d'urgence)
### 3.3. Analyse de Complexité & Niveaux de Difficulté
- Étude de l'impact de la profondeur de recherche sur le temps de décision :
  - Facile : profondeur 2
  - Moyen : profondeur 4
  - Difficile : profondeur 6

---

## 4. Interface Utilisateur & Expérience Joueur (Profil 3)
- Menus interactifs et navigation.
- Système de jokers d'aide (décompte et suggestion visuelle du meilleur coup calculé par l'IA).
- Messages d'erreur explicites lors de saisies invalides.

---

## 5. Jeux d'Essais & Validation
- Tableau des tests effectués :
  - Détection de victoire en ligne, colonne, diagonale montante et descendante.
  - Test de grille pleine (match nul).
  - Test de tentative d'insertion dans une colonne déjà saturée (rejet propre).
  - Test de sauvegarde au tour 5 et reprise au tour 5 sans altération.

---

## 6. Conclusion
- Bilan du travail en équipe et respect des délais.
