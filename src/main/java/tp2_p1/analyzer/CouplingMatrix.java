package tp2_p1.analyzer;

import java.util.*;


public class CouplingMatrix {

    private final Map<String, Map<String, Float>> matrix;

    public CouplingMatrix(Map<String, Map<String, Float>> matrix) {
        this.matrix = matrix;
    }

    public Set<String> getClasses() {
        return matrix.keySet();
    }

    public double getCoupling(String a, String b) {
        if (matrix.containsKey(a) && matrix.get(a).containsKey(b))
            return matrix.get(a).get(b);
        return 0.0;
    }
}
