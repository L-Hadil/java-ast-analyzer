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

### TP2 – Version Spoon 

Pour exécuter la version Spoon du TP2 :

```bash
mvn exec:java -Dexec.mainClass="tp2_p1.spoon_version.CLI_SPOON"
```

---
