package ui;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.stream.file.FileSinkDOT;
import java.io.File;
import java.util.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GraphVisualizer {

    public static void exportGraphToPNG(Map<String, Set<String>> callGraph, String outputFileName) throws IOException {
        System.setProperty("org.graphstream.ui", "swing");

    
        Graph graph = new SingleGraph("Call Graph");

        for (var entry : callGraph.entrySet()) {
            String caller = entry.getKey();
            if (graph.getNode(caller) == null)
                graph.addNode(caller).setAttribute("ui.label", caller);

            for (String callee : entry.getValue()) {
                if (graph.getNode(callee) == null)
                    graph.addNode(callee).setAttribute("ui.label", callee);

                String edgeId = caller + "->" + callee;
                if (graph.getEdge(edgeId) == null)
                    graph.addEdge(edgeId, caller, callee, true);
            }
        }

    
        FileSinkDOT sink = new FileSinkDOT();
        String dotPath = outputFileName + ".dot";
        sink.writeAll(graph, dotPath);
        System.out.println(" Fichier DOT généré : " + dotPath);

        
        generatePNGFromDOT(dotPath, outputFileName + ".png");
    }
    private static void generatePNGFromDOT(String dotFile, String pngFile) {
        try {
        	List<String> lines = Files.readAllLines(Paths.get(dotFile));
        	if (lines.get(0).startsWith("graph")) {
        	    lines.set(0, "digraph {");
        	    for (int i = 0; i < lines.size(); i++) {
        	        lines.set(i, lines.get(i).replace("--", "->"));
        	    }
        	    lines.add(1, "rankdir=LR;");
        	    Files.write(Paths.get(dotFile), lines);
        	}

           
            Thread.sleep(100);

            Process process = new ProcessBuilder(
                "dot", "-Kdot", "-Tpng", dotFile, "-o", pngFile
            ).inheritIO().start();

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println(" Image PNG générée : " + pngFile);
                File file = new File(pngFile);
                if (file.exists()) java.awt.Desktop.getDesktop().open(file);
            } else {
                System.err.println("Erreur Graphviz : code de sortie " + exitCode);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
