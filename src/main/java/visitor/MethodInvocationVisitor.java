package visitor;

import org.eclipse.jdt.core.dom.*;
import java.util.*;

public class MethodInvocationVisitor extends ASTVisitor {
    private final Map<String, Set<String>> callGraph;
    private final String currentClass;
    private final String currentMethod;

    public MethodInvocationVisitor(Map<String, Set<String>> callGraph,
                                   String currentClass, String currentMethod) {
        this.callGraph = callGraph;
        this.currentClass = currentClass;
        this.currentMethod = currentMethod;
    }

    @Override
    public boolean visit(MethodInvocation node) {
        // détermine la classe propriétaire de la méthode appelée
        String owner;
        if (node.getExpression() != null && node.getExpression().resolveTypeBinding() != null) {
            owner = node.getExpression().resolveTypeBinding().getName();          // ex: Grid
        } else if (node.resolveMethodBinding() != null
                && node.resolveMethodBinding().getDeclaringClass() != null) {
            owner = node.resolveMethodBinding().getDeclaringClass().getName();    // fallback
        } else {
            owner = currentClass;                                                 // même classe
        }

        String caller = currentClass + "." + currentMethod;
        String callee = owner + "." + node.getName().getIdentifier();

        callGraph.computeIfAbsent(caller, k -> new HashSet<>()).add(callee);
        return super.visit(node);
    }
}
