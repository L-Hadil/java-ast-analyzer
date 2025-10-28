package tp2_p1.analyzer;

import processor.CallGraphBuilder;
import java.nio.file.Path;
import java.util.*;


public class CouplingCalculator {

    protected final Map<String, Set<String>> callGraph;
    private final Map<String, String> classOfMethod = new HashMap<>();

    public CouplingCalculator(Path src) throws Exception {
        this.callGraph = CallGraphBuilder.buildCallGraph(src);
        buildMethodToClassMap();
    }

    protected void buildMethodToClassMap() {
        for (String caller : callGraph.keySet()) {
            // caller = "Class.method"
            if (caller.contains(".")) {
                String cls = caller.substring(0, caller.indexOf('.'));
                classOfMethod.put(caller, cls);
            }
            for (String callee : callGraph.get(caller)) {
                if (callee.contains(".")) {
                    String cls = callee.substring(0, callee.indexOf('.'));
                    classOfMethod.put(callee, cls);
                }
            }
        }
    }


    public float coupling(String classA, String classB) {
        int relAB = 0;
        int total = 0;

        for (var entry : callGraph.entrySet()) {
            String caller = entry.getKey();
            String callerClass = classOfMethod.getOrDefault(caller, "");

            for (String callee : entry.getValue()) {
                String calleeClass = classOfMethod.getOrDefault(callee, "");
                if (callerClass.isEmpty() || calleeClass.isEmpty()) continue;

                total++;

                boolean cross = (callerClass.equals(classA) && calleeClass.equals(classB))
                             || (callerClass.equals(classB) && calleeClass.equals(classA));

                if (cross) relAB++;
            }
        }

        return total == 0 ? 0 : (float) relAB / total;
    }
    public Map<String, Map<String, Float>> getCouplingMatrix() {
        Map<String, Map<String, Float>> matrix = new HashMap<>();
        Set<String> classes = new HashSet<>(classOfMethod.values());
        for (String a : classes) {
            Map<String, Float> row = new HashMap<>();
            for (String b : classes) {
                if (!a.equals(b))
                    row.put(b, coupling(a, b));
            }
            matrix.put(a, row);
        }
        return matrix;
    }


    public Map<String, Set<String>> getCallGraph() {
        return callGraph;
    }

    public void printCallGraph() {
        System.out.println("=== Graphe d'appel inter-classes ===");
        for (var e : callGraph.entrySet()) {
            System.out.println(e.getKey() + " -> " + e.getValue());
        }
    }
    
}
