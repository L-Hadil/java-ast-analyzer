package tp2_p1.ui;

import tp2_p1.analyzer.CouplingMatrix;
import java.io.*;
import java.util.*;

public class CouplingGraphVisualizer {

    public static void exportWeightedGraph(CouplingMatrix matrix, String outputFileName) {
        String dotFile = outputFileName + ".dot";
        String pngFile = outputFileName + ".png";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dotFile))) {
            writer.write("digraph CouplingGraph {\n");
            writer.write("    rankdir=LR;\n"); // orientation horizontale
            writer.write("    node [shape=box, style=filled, fillcolor=lightyellow];\n");

            Set<String> seenEdges = new HashSet<>();

            for (String classA : matrix.getClasses()) {
                for (String classB : matrix.getClasses()) {
                    if (classA.equals(classB)) continue;

                    double coupling = matrix.getCoupling(classA, classB);
                    if (coupling > 0.0) {
                        String key = classA + "->" + classB;
                        if (!seenEdges.contains(key)) {
                            writer.write(String.format(
                                "    \"%s\" -> \"%s\" [label=\"%.3f\", color=gray50, fontcolor=blue];%n",
                                classA, classB, coupling
                            ));
                            seenEdges.add(key);
                        }
                    }
                }
            }

            writer.write("}\n");
            System.out.println("Fichier DOT généré : " + dotFile);
        } catch (IOException e) {
            System.err.println("Erreur lors de l’écriture du fichier DOT : " + e.getMessage());
            return;
        }

        generatePNG(dotFile, pngFile);
    }

    private static void generatePNG(String dotFile, String pngFile) {
        try {
            ProcessBuilder pb = new ProcessBuilder("dot", "-Tpng", dotFile, "-o", pngFile);
            pb.inheritIO();
            Process p = pb.start();
            int code = p.waitFor();
            if (code == 0) {
                System.out.println("Image PNG générée : " + pngFile);
                File f = new File(pngFile);
                if (f.exists()) java.awt.Desktop.getDesktop().open(f);
            } else {
                System.err.println("Erreur Graphviz (code " + code + ")");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
