package tp2_p1.spoon_version;

import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.*;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.reflect.code.CtInvocation;
import java.nio.file.Path;
import java.util.*;

public class SpoonCallGraphBuilder {
    public static Map<String, Set<String>> buildCallGraph(Path srcPath) {
        Map<String, Set<String>> graph = new HashMap<>();

        Launcher launcher = new Launcher();
        launcher.addInputResource(srcPath.toString());
        launcher.getEnvironment().setNoClasspath(true);
        launcher.buildModel();
        CtModel model = launcher.getModel();

        for (CtType<?> type : model.getAllTypes()) {
            String className = type.getSimpleName();
            for (CtMethod<?> method : type.getMethods()) {
                String caller = className + "." + method.getSimpleName();
                graph.putIfAbsent(caller, new HashSet<>());

                for (CtInvocation<?> inv : method.getElements(new TypeFilter<>(CtInvocation.class))) {
                    CtExecutableReference<?> exec = inv.getExecutable();
                    CtTypeReference<?> target = exec.getDeclaringType();
                    if (target != null) {
                        String callee = target.getSimpleName() + "." + exec.getSimpleName();
                        graph.get(caller).add(callee);
                    }
                }
            }
        }
        return graph;
    }
}
