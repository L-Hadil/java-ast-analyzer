package tp2_p1.ui;

import tp2_p1.analyzer.*;
import java.nio.file.*;
import java.io.*;
import java.util.*;

public class CLI_TP2 {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== TP2 - Analyse de Couplage et Clustering ===");
        System.out.print("Chemin du code source : ");
        String pathStr = scanner.nextLine().trim();
        Path src = Paths.get(pathStr);

        try {
            boolean running = true;
            while (running) {
                printMainMenu();
                System.out.print("Votre choix : ");
                int choice = readInt();

                switch (choice) {
                    case 1 -> runExercise1(src);
                    case 2 -> runExercise2(src);
                    case 0 -> running = false;
                    default -> System.out.println("Choix invalide.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Fin du programme.");
    }

    private static void printMainMenu() {
        System.out.println("\n--- MENU PRINCIPAL ---");
        System.out.println("1. Exercice 1 : Graphe de couplage entre classes");
        System.out.println("2. Exercice 2 : Clustering hiérarchique des classes");
        System.out.println("0. Quitter");
    }

    // ------------------------------------------------------------
    // EXERCICE 1 : Graphe de couplage pondéré
    // ------------------------------------------------------------
    private static void runExercise1(Path src) throws Exception {
        CouplingCalculator calc = new CouplingCalculator(src);

        boolean subRunning = true;
        while (subRunning) {
            printEx1Menu();
            System.out.print("Votre choix : ");
            int choice = readInt();

            switch (choice) {
                case 1 -> calc.printCallGraph();

                case 2 -> {
                    System.out.print("Nom de la première classe (A) : ");
                    String a = scanner.nextLine().trim();
                    System.out.print("Nom de la deuxième classe (B) : ");
                    String b = scanner.nextLine().trim();
                    float c = calc.coupling(a, b);
                    System.out.printf("Couplage(%s,%s) = %.4f%n", a, b, c);
                }

                case 3 -> {
                    System.out.println("\n--- Génération du graphe de couplage pondéré ---");
                    Map<String, Map<String, Float>> m = calc.getCouplingMatrix();
                    CouplingGraphExporter.exportToDot(m, Path.of("coupling_graph.dot"));
                    CouplingGraphExporter.exportToPNG(Path.of("coupling_graph.dot"), Path.of("coupling_graph.png"));
                    System.out.println("Graphe de couplage généré : coupling_graph.png");
                    openImage(Path.of("coupling_graph.png"));
                    
                }

                case 0 -> subRunning = false;
                default -> System.out.println("Choix invalide.");
            }
        }
    }

    private static void printEx1Menu() {
        System.out.println("\n--- EXERCICE 1 : COUPLAGE ENTRE CLASSES ---");
        System.out.println("1. Afficher le graphe d'appel inter-classes");
        System.out.println("2. Calculer le couplage entre deux classes");
        System.out.println("3. Générer le graphe de couplage pondéré (.dot + .png)");
        System.out.println("0. Retour au menu principal");
    }

    // ------------------------------------------------------------
    // EXERCICE 2 : Clustering hiérarchique
    // ------------------------------------------------------------
    private static void runExercise2(Path src) throws Exception {
        CouplingCalculator calc = new CouplingCalculator(src);
        CouplingMatrix matrix = new CouplingMatrix(calc.getCouplingMatrix());
        HierarchicalClusterer clusterer = new HierarchicalClusterer(matrix);

        boolean subRunning = true;
        while (subRunning) {
            printEx2Menu();
            System.out.print("Votre choix : ");
            int choice = readInt();

            switch (choice) {
            case 1 -> {
                System.out.println("--- Exécution du clustering hiérarchique ---");
                HierarchicalClusterer.Cluster root = clusterer.run();
                Path dot = Path.of("clusters.dot");
                Path png = Path.of("clusters.png");
                HierarchicalClusterer.exportToDot(root, dot);
                HierarchicalClusterer.exportToPNG(dot, png);
                System.out.println("Hiérarchie de clusters générée : clusters.png");
                openImage(Path.of("clusters.png"));
            }

            case 0 -> subRunning = false;
            default -> System.out.println("Choix invalide.");
        }

        }
    }

    private static void printEx2Menu() {
        System.out.println("\n--- EXERCICE 2 : CLUSTERING HIÉRARCHIQUE ---");
        System.out.println("1. Exécuter le clustering hiérarchique et générer les fichiers (.dot + .png)");
        System.out.println("0. Retour au menu principal");
    }

    // ------------------------------------------------------------
    private static int readInt() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    private static void openImage(Path pngFile) {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder pb;
            if (os.contains("mac")) {
                pb = new ProcessBuilder("open", pngFile.toString());
            } else if (os.contains("win")) {
                pb = new ProcessBuilder("cmd", "/c", "start", pngFile.toString());
            } else {
                pb = new ProcessBuilder("xdg-open", pngFile.toString());
            }
            pb.start();
        } catch (Exception ex) {
            System.err.println("Impossible d’ouvrir l’image : " + ex.getMessage());
        }
    }

}
