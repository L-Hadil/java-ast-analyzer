package visitor;

import org.eclipse.jdt.core.dom.*;
import java.util.*;

public class MethodInvocationVisitor extends ASTVisitor {
    private final Map<String, Set<String>> callGraph;
    private final String currentMethod;
    private final String currentClass;

    public MethodInvocationVisitor(Map<String, Set<String>> callGraph,
                                   String currentClass, String currentMethod) {
        this.callGraph = callGraph;
        this.currentClass = currentClass;
        this.currentMethod = currentMethod;
    }

    @Override
    public boolean visit(MethodInvocation node) {
        
        String callee = node.getName().getIdentifier();

        
        String caller = currentClass + "." + currentMethod;
        callGraph.computeIfAbsent(caller, k -> new HashSet<>()).add(callee);

        return super.visit(node);
    }
}
