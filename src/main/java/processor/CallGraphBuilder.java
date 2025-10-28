package processor;

import model.*;
import org.eclipse.jdt.core.dom.*;
import parser.Parser;
import visitor.MethodInvocationVisitor;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class CallGraphBuilder {

    public static Map<String, Set<String>> buildCallGraph(Path srcPath) throws Exception {
        Map<String, Set<String>> callGraph = new HashMap<>();

        for (Path p : Parser.listJava(srcPath)) {
            String code = Files.readString(p, StandardCharsets.UTF_8);
            CompilationUnit cu = Parser.parse(code, srcPath);


            // Pour chaque classe
            cu.accept(new ASTVisitor() {
                @Override
                public boolean visit(TypeDeclaration type) {
                    String className = type.getName().getIdentifier();

                    for (MethodDeclaration method : type.getMethods()) {
                        String methodName = method.getName().getIdentifier();

                        // visite les invocations à l’intérieur de cette méthode
                        MethodInvocationVisitor visitor =
                                new MethodInvocationVisitor(callGraph, className, methodName);
                        method.accept(visitor);
                    }
                    return false;
                }
            });
        }

        return callGraph;
    }
}
