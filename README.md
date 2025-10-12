# Java AST Analyzer

## Description

Java AST Analyzer est une application d’analyse statique  du code source Java.  
Elle permet d’extraire des informations structurelles et comportementales à partir des fichiers `.java` d’un projet donné.  
L’application repose sur l’API **Eclipse JDT (Java Development Tools)** pour la construction et la traversée de l’arbre syntaxique abstrait (AST).

Elle propose deux modes d’exécution :
- **Interface Console (CLI)** : interaction textuelle depuis le terminal.
- **Interface Graphique (GUI)** : interface Swing moderne et intuitive.
---
## Installation et Exécution

### Prérequis
- Java 11 ou supérieur  
- Maven 3.6+ installé et configuré  
- Graphviz installé et accessible dans le PATH (pour l’export du graphe d’appel en PNG)

### Clonage du projet
```bash
git clone https://github.com/L-Hadil/java-ast-analyzer.git
cd java-ast-analyzer
````

### Compilation

```bash
mvn clean install
```

### Exécution

Pour exécuter le programme principal :

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

## Utilisation

### Mode Interface Graphique (GUI)

1. Au lancement, la fenêtre d’accueil décrit le but de l’application et ses fonctionnalités.
2. Cliquez sur **« Commencer l’analyse »**.
3. Sélectionnez le dossier contenant vos fichiers `.java`.
4. Choisissez entre :

   * **Analyse Statistique** : sélection d’une métrique parmi les 13 disponibles.
   * **Graphe d’appel** : génération du graphe d’appel inter-classes (en affichage texte ou en image PNG).

### Mode Interface Console (CLI)

1. Sélectionnez le dossier à analyser lorsque l’application vous le demande.
2. Choisissez le type d’analyse (statistique ou graphe).
3. Suivez les instructions affichées dans la console.
4. Les résultats s’affichent directement dans le terminal.

## Fonctionnalités

### Analyse Statique

Calcul automatique de plusieurs métriques :

1. Nombre de classes dans le projet.
2. Nombre total de lignes de code.
3. Nombre total de méthodes.
4. Nombre de packages.
5. Moyenne de méthodes par classe.
6. Moyenne de lignes de code par méthode.
7. Moyenne d’attributs par classe.
8. Les 10 % des classes avec le plus de méthodes.
9. Les 10 % des classes avec le plus d’attributs.
10. Les classes présentes dans les deux catégories précédentes.
11. Les classes ayant plus de X méthodes (X choisi par l’utilisateur).
12. Les 10 % des méthodes les plus longues (par classe).
13. Le nombre maximal de paramètres parmi toutes les méthodes.

### Graphe d’appel inter-classes

* Analyse des appels entre méthodes à l’aide de l’AST.
* Construction d’un graphe orienté décrivant les relations d’appel.
* Export automatique au format `.dot` puis conversion en image `.png` via Graphviz.

Les fichiers générés se trouvent dans le répertoire du projet :

* `call_graph.dot`
* `call_graph.png`
