package tp2_p1.clustering;

import tp2_p1.analyzer.CouplingMatrix;
import java.util.*;

/**
 * Réalise le clustering hiérarchique complet entre classes
 * en fusionnant à chaque itération les deux clusters les plus couplés.
 */
public class HierarchicalClusterer {

    private final CouplingMatrix matrix;
    private final List<Set<String>> clusters = new ArrayList<>();

    public HierarchicalClusterer(CouplingMatrix matrix) {
        this.matrix = matrix;
        initializeClusters();
    }

    // Initialise chaque classe comme cluster unique
    private void initializeClusters() {
        for (String c : matrix.getClasses()) {
            Set<String> s = new HashSet<>();
            s.add(c);
            clusters.add(s);
        }
    }

    /** Lance le clustering complet jusqu’à 1 seul cluster. */
    public void run() {
        while (clusters.size() > 1) {
            Pair best = findMostCoupledPair();
            if (best == null) break;
            Set<String> merged = new HashSet<>(best.a);
            merged.addAll(best.b);
            clusters.remove(best.a);
            clusters.remove(best.b);
            clusters.add(merged);
            System.out.printf("[Fusion] %s + %s%n", best.a, best.b);
        }
    }

    /** Affiche les clusters restants */
    public void printClusters() {
        System.out.println("=== Clusters finaux ===");
        for (Set<String> c : clusters) {
            System.out.println(c);
        }
    }

    /** Recherche le couple de clusters ayant le plus fort couplage moyen */
    private Pair findMostCoupledPair() {
        double max = -1;
        Set<String> bestA = null, bestB = null;
        for (int i = 0; i < clusters.size(); i++) {
            for (int j = i + 1; j < clusters.size(); j++) {
                double c = averageCoupling(clusters.get(i), clusters.get(j));
                if (c > max) {
                    max = c;
                    bestA = clusters.get(i);
                    bestB = clusters.get(j);
                }
            }
        }
        return bestA != null ? new Pair(bestA, bestB) : null;
    }

    private double averageCoupling(Set<String> A, Set<String> B) {
        double sum = 0;
        int n = 0;
        for (String a : A)
            for (String b : B) {
                sum += matrix.getCoupling(a, b);
                n++;
            }
        return n == 0 ? 0 : sum / n;
    }

    /** Pair interne simple */
    private static class Pair {
        final Set<String> a, b;
        Pair(Set<String> a, Set<String> b) { this.a = a; this.b = b; }
    }
}
