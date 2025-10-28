package tp2_p1.spoon_version;

import tp2_p1.analyzer.*;
import java.nio.file.*;
import java.util.*;

public class CLI_SPOON {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== TP3 - Analyse via Spoon ===");
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

        System.out.println("Fin du programme Spoon.");
    }

    private static void printMainMenu() {
        System.out.println("\n--- MENU PRINCIPAL (SPOON) ---");
        System.out.println("1. Exercice 1 : Graphe de couplage entre classes (Spoon)");
        System.out.println("2. Exercice 2 : Clustering hiérarchique des classes (Spoon)");
        System.out.println("0. Quitter");
    }

    // ------------------------------------------------------------
    // EXERCICE 1 : Graphe de couplage pondéré via Spoon
    // ------------------------------------------------------------
    private static void runExercise1(Path src) throws Exception {
        SpoonCouplingCalculator calc = new SpoonCouplingCalculator(src);

        boolean subRunning = true;
        while (subRunning) {
            printEx1Menu();
            System.out.print("Votre choix : ");
            int choice = readInt();

            switch (choice) {
                case 1 -> {
                    System.out.println("=== Graphe d'appel inter-classes (Spoon) ===");
                    for (var e : calc.getCallGraph().entrySet()) {
                        System.out.println(e.getKey() + " -> " + e.getValue());
                    }
                }

                case 2 -> {
                    System.out.print("Nom de la première classe (A) : ");
                    String a = scanner.nextLine().trim();
                    System.out.print("Nom de la deuxième classe (B) : ");
                    String b = scanner.nextLine().trim();
                    float c = calc.coupling(a, b);
                    System.out.printf("Couplage(%s,%s) [Spoon] = %.4f%n", a, b, c);
                }

                case 3 -> {
                    System.out.println("\n--- Génération du graphe de couplage pondéré (Spoon) ---");
                    Map<String, Map<String, Float>> m = calc.getCouplingMatrix();
                    Path dot = Path.of("spoon_coupling_graph.dot");
                    Path png = Path.of("spoon_coupling_graph.png");
                    CouplingGraphExporter.exportToDot(m, dot);
                    CouplingGraphExporter.exportToPNG(dot, png);
                    System.out.println("Graphe de couplage généré : spoon_coupling_graph.png");
                    openImage(Path.of("spoon_coupling_graph.png"));
                }

                case 0 -> subRunning = false;
                default -> System.out.println("Choix invalide.");
            }
        }
    }

    private static void printEx1Menu() {
        System.out.println("\n--- EXERCICE 1 (SPOON) : COUPLAGE ENTRE CLASSES ---");
        System.out.println("1. Afficher le graphe d'appel inter-classes (Spoon)");
        System.out.println("2. Calculer le couplage entre deux classes (Spoon)");
        System.out.println("3. Générer le graphe de couplage pondéré (.dot + .png) (Spoon)");
        System.out.println("0. Retour au menu principal");
    }

    // ------------------------------------------------------------
    // EXERCICE 2 : Clustering hiérarchique via Spoon
    // ------------------------------------------------------------
    private static void runExercise2(Path src) throws Exception {
        SpoonCouplingCalculator calc = new SpoonCouplingCalculator(src);
        CouplingMatrix matrix = new CouplingMatrix(calc.getCouplingMatrix());
        HierarchicalClusterer clusterer = new HierarchicalClusterer(matrix);

        boolean subRunning = true;
        while (subRunning) {
            printEx2Menu();
            System.out.print("Votre choix : ");
            int choice = readInt();

            switch (choice) {
                case 1 -> {
                    System.out.println("--- Exécution du clustering hiérarchique (Spoon) ---");
                    HierarchicalClusterer.Cluster root = clusterer.run();
                    Path dot = Path.of("spoon_clusters.dot");
                    Path png = Path.of("spoon_clusters.png");
                    HierarchicalClusterer.exportToDot(root, dot);
                    HierarchicalClusterer.exportToPNG(dot, png);
                    System.out.println("Hiérarchie de clusters générée : spoon_clusters.png");
                    openImage(Path.of("spoon_clusters.png"));
                }

                case 0 -> subRunning = false;
                default -> System.out.println("Choix invalide.");
            }
        }
    }

    private static void printEx2Menu() {
        System.out.println("\n--- EXERCICE 2 (SPOON) : CLUSTERING HIÉRARCHIQUE ---");
        System.out.println("1. Exécuter le clustering hiérarchique et générer les fichiers (.dot + .png) (Spoon)");
        System.out.println("0. Retour au menu principal");
    }


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
