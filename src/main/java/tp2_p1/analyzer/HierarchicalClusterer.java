package tp2_p1.analyzer;

import java.io.*;
import java.nio.file.*;
import java.util.*;


public class HierarchicalClusterer {

    public static class Cluster {
        String name;
        Cluster left, right;
        double coupling;

        public Cluster(String name) { this.name = name; }
        public Cluster(Cluster left, Cluster right, double coupling) {
            this.left = left; this.right = right;
            this.name = "(" + left.name + "+" + right.name + ")";
            this.coupling = coupling;
        }
        public boolean isLeaf() { return left == null && right == null; }
    }

    private final CouplingMatrix matrix;

    public HierarchicalClusterer(CouplingMatrix matrix) {
        this.matrix = matrix;
    }

    public Cluster run() {
        List<Cluster> clusters = new ArrayList<>();
        for (String cls : matrix.getClasses()) clusters.add(new Cluster(cls));

        Map<String, Map<String, Double>> dist = initDistances(clusters);

        while (clusters.size() > 1) {
            
            double best = -1;
            int iBest = -1, jBest = -1;
            for (int i = 0; i < clusters.size(); i++) {
                for (int j = i + 1; j < clusters.size(); j++) {
                    double c = getDist(dist, clusters.get(i).name, clusters.get(j).name);
                    if (c > best) { best = c; iBest = i; jBest = j; }
                }
            }

            Cluster A = clusters.get(iBest);
            Cluster B = clusters.get(jBest);
            Cluster merged = new Cluster(A, B, best);

           
            updateDistances(dist, clusters, A, B, merged);

            
            List<Cluster> newList = new ArrayList<>();
            for (int k = 0; k < clusters.size(); k++)
                if (k != iBest && k != jBest) newList.add(clusters.get(k));
            newList.add(merged);
            clusters = newList;
        }

        return clusters.get(0);
    }

  
    private Map<String, Map<String, Double>> initDistances(List<Cluster> clusters) {
        Map<String, Map<String, Double>> dist = new HashMap<>();
        for (Cluster a : clusters) {
            Map<String, Double> row = new HashMap<>();
            for (Cluster b : clusters) {
                if (a == b) continue;
                double val = (matrix.getCoupling(a.name, b.name) +
                              matrix.getCoupling(b.name, a.name)) / 2.0;
                row.put(b.name, val);
            }
            dist.put(a.name, row);
        }
        return dist;
    }

    private double getDist(Map<String, Map<String, Double>> d, String a, String b) {
        return d.getOrDefault(a, Map.of()).getOrDefault(b, 0.0);
    }

    /** Met à jour les distances après fusion de A et B */
    private void updateDistances(Map<String, Map<String, Double>> dist,
                                 List<Cluster> clusters,
                                 Cluster A, Cluster B, Cluster merged) {
        dist.remove(A.name);
        dist.remove(B.name);
        for (Map<String, Double> m : dist.values()) {
            m.remove(A.name);
            m.remove(B.name);
        }

        Map<String, Double> newRow = new HashMap<>();
        for (Cluster C : clusters) {
            if (C == A || C == B) continue;
            double ac = getDist(dist, A.name, C.name);
            double bc = getDist(dist, B.name, C.name);
            // liaison complète : moyenne ou max selon besoin
            double newVal = (ac + bc) / 2.0;
            newRow.put(C.name, newVal);
            dist.computeIfAbsent(C.name, k -> new HashMap<>()).put(merged.name, newVal);
        }
        dist.put(merged.name, newRow);
    }

   
    public static void exportToDot(Cluster root, Path file) throws IOException {
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(file))) {
            out.println("digraph Clusters {");
            out.println("  rankdir=TB;");
            out.println("  node [shape=ellipse, style=filled, fillcolor=lightyellow];");
            write(out, root);
            out.println("}");
        }
        System.out.println("Fichier DOT généré : " + file);
    }

    private static void write(PrintWriter out, Cluster c) {
        if (c.isLeaf()) return;
        String parent = sanitize(c.name);
        String left = sanitize(c.left.name);
        String right = sanitize(c.right.name);
        out.printf("  \"%s\" -> \"%s\" [label=\"%.3f\"];\n", parent, left, c.coupling);
        out.printf("  \"%s\" -> \"%s\" [label=\"%.3f\"];\n", parent, right, c.coupling);
        write(out, c.left);
        write(out, c.right);
    }

    public static void exportToPNG(Path dot, Path png) throws IOException, InterruptedException {
        new ProcessBuilder("dot", "-Tpng", dot.toString(), "-o", png.toString())
                .inheritIO().start().waitFor();
        System.out.println("Image PNG générée : " + png);
    }

    private static String sanitize(String s) {
        return s.replaceAll("[^a-zA-Z0-9_+]", "_");
    }
    public void runClustering(Path dotFile, Path pngFile) throws Exception {
        Cluster root = run();
        exportToDot(root, dotFile);
        exportToPNG(dotFile, pngFile);
    }


}
