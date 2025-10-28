# Java AST & Coupling Analyzer

## Description

Java AST & Coupling Analyzer est une application d’analyse statique et de couplage du code source Java.  
Elle permet d’extraire des informations structurelles et comportementales à partir des fichiers `.java` d’un projet donné, et d’analyser les dépendances entre classes.  

L’application repose sur :
- **TP1 :** l’API **Eclipse JDT (Java Development Tools)** pour la construction et la traversée de l’arbre syntaxique abstrait (AST).  
- **TP2 :** une extension pour l’analyse de **couplage entre classes** et le **clustering hiérarchique** (modules fonctionnels), disponible en deux versions :
  - **Version JDT (AST classique)**  
  - **Version Spoon (analyse par métamodélisation)**  

Elle propose deux modes d’exécution :
- **Interface Console (CLI)** : interaction textuelle depuis le terminal.  
- **Interface Graphique (GUI)** : interface Swing moderne et intuitive.

---

## Installation et Exécution

### Prérequis
- Java 17 ou supérieur  
- Maven 3.6+ installé et configuré  
- Graphviz installé et accessible dans le PATH (pour l’export des graphes en PNG)

### Clonage du projet
```bash
git clone https://github.com/L-Hadil/java-ast-analyzer.git
cd java-ast-analyzer
````

### Compilation

```bash
mvn clean install
```

---

## Exécution

### TP1 – Analyse Statique (JDT)

Pour exécuter le programme principal du TP1 :

```bash
mvn exec:java -Dexec.mainClass="app.Main"
```

Le programme démarre et affiche dans la console :

```
=== Analyseur de Code Java ===
Choisissez le mode d’exécution :
1. Interface Console (CLI)
2. Interface Graphique (GUI)
Votre choix :
```

* Tapez **1** pour utiliser la version **CLI (console)**.
* Tapez **2** pour lancer la **GUI (interface graphique Swing)**.

---

### TP2 – Analyse de Couplage et Clustering (JDT)

```bash
mvn exec:java -Dexec.mainClass="tp2_p1.ui.CLI_TP2"
```

ou en interface graphique :

```bash
mvn exec:java -Dexec.mainClass="tp2_p1.ui.GUI_TP2"
```

---

### TP2 – Version Spoon (analyse métamodèle)

Pour exécuter la version Spoon du TP2 :

```bash
mvn exec:java -Dexec.mainClass="tp2_p1.spoon_version.CLI_SPOON"
```

---

## Utilisation

### Mode Interface Graphique (GUI)

1. Au lancement, la fenêtre d’accueil décrit le but de l’application et ses fonctionnalités.
2. Cliquez sur **« Commencer l’analyse »**.
3. Sélectionnez le dossier contenant vos fichiers `.java`.
4. Choisissez entre :

   * **Analyse Statistique (TP1)** : sélection d’une métrique parmi les 13 disponibles.
   * **Analyse de Couplage / Clustering (TP2)** : génération automatique des graphes de couplage et des dendrogrammes de modules.

---

### Mode Interface Console (CLI)

#### TP1

1. Sélectionnez le dossier à analyser lorsque l’application vous le demande.
2. Choisissez le type d’analyse (statistique ou graphe).
3. Suivez les instructions affichées dans la console.
4. Les résultats s’affichent directement dans le terminal.

#### TP2

1. Sélectionnez le dossier source.
2. Choisissez :

   * **Exercice 1** : Graphe de couplage entre classes

     * Affichage du graphe d’appel
     * Calcul du couplage entre deux classes
     * Génération du graphe pondéré `.dot` + `.png`
   * **Exercice 2** : Clustering hiérarchique

     * Génération des modules (M/2 max, couplage moyen > CP)
     * Export `.dot` et `.png` du dendrogramme
3. Les fichiers sont générés automatiquement :

   * `coupling_graph.dot`, `coupling_graph.png`
   * `clusters.dot`, `clusters.png`
   * (Version Spoon) `spoon_coupling_graph.dot`, `spoon_clusters.dot`

---

## Fonctionnalités

### TP1 – Analyse Statique

Calcul automatique de plusieurs métriques :

1. Nombre de classes dans le projet
2. Nombre total de lignes de code
3. Nombre total de méthodes
4. Nombre de packages
5. Moyenne de méthodes par classe
6. Moyenne de lignes de code par méthode
7. Moyenne d’attributs par classe
8. 10 % des classes avec le plus de méthodes
9. 10 % des classes avec le plus d’attributs
10. Classes présentes dans les deux catégories précédentes
11. Classes ayant plus de X méthodes (X choisi par l’utilisateur)
12. 10 % des méthodes les plus longues (par classe)
13. Nombre maximal de paramètres parmi toutes les méthodes

### Graphe d’appel inter-classes (TP1)

* Analyse des appels entre méthodes à l’aide de l’AST.
* Construction d’un graphe orienté décrivant les relations d’appel.
* Export automatique au format `.dot`, puis conversion en image `.png` via Graphviz.

---

### TP2 – Analyse de Couplage et Clustering

**Exercice 1 – Couplage entre classes**

* Construction d’une **matrice de couplage pondérée**.
* Graphe de dépendances orienté (pondéré).
* Export `.dot` et `.png`.

**Exercice 2 – Clustering hiérarchique**

* Algorithme de regroupement automatique des classes couplées.
* Génération d’un **dendrogramme** illustrant les modules.
* Contraintes :

  * Application ≤ M/2 modules
  * Moyenne du couplage > CP (paramètre utilisateur)

**Version Spoon**

* Reprise des mêmes calculs avec l’analyseur **Spoon** (métamodèle Java).
* Export séparé : `spoon_coupling_graph.png`, `spoon_clusters.png`.

```
```
