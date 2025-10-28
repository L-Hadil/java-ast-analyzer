package tp2_p1.analyzer;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class CouplingGraphExporter {

    public static void exportToDot(Map<String, Map<String, Float>> matrix, Path outputDot) throws IOException {
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(outputDot))) {
            out.println("digraph CouplingGraph {");
            out.println("  rankdir=LR;");
            out.println("  node [shape=box, style=rounded];");

            for (var e1 : matrix.entrySet()) {
                String from = sanitize(e1.getKey());
                for (var e2 : e1.getValue().entrySet()) {
                    float weight = e2.getValue();
                    if (weight > 0 && !Float.isNaN(weight) && !Float.isInfinite(weight)) {
                        String to = sanitize(e2.getKey());
                        out.printf(Locale.US,
                            "  \"%s\" -> \"%s\" [label=\"%.3f\", penwidth=%.2f];%n",
                            from, to, weight, 1 + 4 * weight
                        );
                    }
                }
            }

            out.println("}");
        }
    }

    public static void exportToPNG(Path dotFile, Path pngFile)
            throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(
            "dot", "-Tpng", dotFile.toString(), "-o", pngFile.toString()
        );
        pb.redirectErrorStream(true);
        Process p = pb.start();
        p.waitFor();
    }

    private static String sanitize(String name) {
        return name.replaceAll("[^a-zA-Z0-9_]", "_");
    }
}
