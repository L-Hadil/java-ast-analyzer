package ui;

import processor.*;
import model.*;
import java.nio.file.*;
import java.util.*;

public class CLI {

    private static final Scanner sc = new Scanner(System.in);

    public static void start() throws Exception {
        System.out.println("=== Bienvenue dans l'analyseur de code Java ===");
        System.out.println("Veuillez entrer le chemin complet vers le dossier contenant les fichiers .java à analyser :");
        System.out.println("Exemples :");
        System.out.println("  - Windows : C:\\Users\\MonNom\\eclipse-workspace\\MonProjet\\src\\main\\java");
        System.out.println("  - macOS/Linux : /Users/monnom/eclipse-workspace/MonProjet/src/main/java");
        System.out.print("Chemin : ");
        Path src = Path.of(sc.nextLine().trim());

        while (true) {
            System.out.println("\n====== MENU PRINCIPAL ======");
            System.out.println("1. Analyse Statistique (Q1)");
            System.out.println("2. Graphe d'appel (Q2)");
            System.out.println("0. Quitter");
            System.out.print("Choix : ");
            String choix = sc.nextLine();

            switch (choix) {
                case "1":
                    analyseStatistique(src);
                    break;
                case "2":
                    afficherGraphe(src);
                    break;
                case "0":
                    System.out.println("Au revoir !");
                    return;
                default:
                    System.out.println("Choix invalide.");
            }
        }
    }

    private static void analyseStatistique(Path src) throws Exception {
        ProjectModel pm = ASTProcessor.process(src);
        int X = 5;
        Map<String, Object> stats = StatisticsCalculator.compute(pm, X);

        String[] metricNames = {
            "1. Nombre de classes de l’application",
            "2. Nombre de lignes de code de l’application",
            "3. Nombre total de méthodes de l’application",
            "4. Nombre total de packages de l’application",
            "5. Nombre moyen de méthodes par classe",
            "6. Nombre moyen de lignes de code par méthode",
            "7. Nombre moyen d’attributs par classe",
            "8. Les 10% des classes avec le plus grand nombre de méthodes",
            "9. Les 10% des classes avec le plus grand nombre d’attributs",
            "10. Les classes appartenant aux deux catégories précédentes",
            "11. Les classes qui possèdent plus de X méthodes (X à définir)",
            "12. Les 10% des méthodes les plus longues (par classe)",
            "13. Nombre maximal de paramètres parmi toutes les méthodes"
        };

        while (true) {
            System.out.println("\n----- ANALYSE STATISTIQUE -----");
            for (String m : metricNames) System.out.println(m);
            System.out.println("0. Retour");
            System.out.print("Choix : ");
            String c = sc.nextLine().trim();

            if (c.equals("0")) return;

            try {
                int n = Integer.parseInt(c);

                if (n >= 1 && n <= 13) {
                    if (n == 11) {
                        System.out.println("\nCette métrique calcule les classes ayant plus de X méthodes.");
                        System.out.print("Entrez la valeur de X : ");
                        X = Integer.parseInt(sc.nextLine().trim());
                        stats = StatisticsCalculator.compute(pm, X);
                    }

                    Object key = stats.keySet().toArray()[n - 1];
                    Object value = stats.values().toArray()[n - 1];
                    System.out.println("\nRésultat (" + metricNames[n - 1] + ") :");
                    System.out.println(value);
                } else {
                    System.out.println("Numéro invalide. Choisissez entre 1 et 13.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Entrée invalide. Veuillez saisir un nombre.");
            }
        }
    }

    private static void afficherGraphe(Path src) throws Exception {
        System.out.println("\n--- Construction du graphe d'appel ---");
        Map<String, Set<String>> graph = CallGraphBuilder.buildCallGraph(src);

        System.out.println("\n1. Afficher sous forme textuelle");
        System.out.println("2. Exporter le graphe en image PNG");
        System.out.print("Choix : ");
        String choix = sc.nextLine();

        if (choix.equals("2")) {
            ui.GraphVisualizer.exportGraphToPNG(graph, "call_graph");
        } else {
            System.out.println("\n--- Graphe d'appel inter-classes ---");
            for (var entry : graph.entrySet()) {
                if (!entry.getValue().isEmpty())
                    System.out.println(entry.getKey() + " --> " + entry.getValue());
            }
        }
    }
}
