package tp2_p1.spoon_version;

import java.nio.file.Path;
import java.util.*;

public class SpoonCouplingCalculator {
    private final Map<String, Set<String>> callGraph;

    public SpoonCouplingCalculator(Path src) throws Exception {
        this.callGraph = SpoonCallGraphBuilder.buildCallGraph(src);
    }

    public Map<String, Set<String>> getCallGraph() {
        return callGraph;
    }

    public float coupling(String classA, String classB) {
        int relAB = 0;
        int total = 0;
        Map<String, String> classOfMethod = new HashMap<>();

        for (String caller : callGraph.keySet()) {
            if (caller.contains(".")) {
                classOfMethod.put(caller, caller.substring(0, caller.indexOf('.')));
            }
            for (String callee : callGraph.get(caller)) {
                if (callee.contains(".")) {
                    classOfMethod.put(callee, callee.substring(0, callee.indexOf('.')));
                }
            }
        }

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
        Set<String> classes = new HashSet<>();

        for (String method : callGraph.keySet()) {
            if (method.contains(".")) {
                classes.add(method.substring(0, method.indexOf('.')));
            }
            for (String callee : callGraph.get(method)) {
                if (callee.contains(".")) {
                    classes.add(callee.substring(0, callee.indexOf('.')));
                }
            }
        }

        for (String a : classes) {
            Map<String, Float> row = new HashMap<>();
            for (String b : classes) {
                if (!a.equals(b)) row.put(b, coupling(a, b));
            }
            matrix.put(a, row);
        }

        return matrix;
    }
}
