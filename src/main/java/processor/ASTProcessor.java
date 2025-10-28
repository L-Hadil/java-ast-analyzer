package processor;

import parser.Parser;
import visitor.ClassCollector;
import model.*;

import org.eclipse.jdt.core.dom.CompilationUnit;
import java.nio.file.*;
import java.nio.charset.*;

/**
 * Parcourt récursivement le code source et alimente le ProjectModel
 * avec les classes, attributs et méthodes détectés.
 */
public class ASTProcessor {

    public static ProjectModel process(Path srcRoot) throws Exception {
        ProjectModel pm = new ProjectModel();

        for (Path p : Parser.listJava(srcRoot)) {
            String code = Files.readString(p, StandardCharsets.UTF_8);

            // Compte les lignes non vides (LOC globales)
            pm.totalFileLOC += (int) code.lines().filter(s -> !s.isBlank()).count();

            // Utilise la version de Parser avec bindings activés
            CompilationUnit cu = Parser.parse(code, srcRoot);

            // Collecte les infos sur classes / attributs / méthodes
            cu.accept(new ClassCollector(cu, pm));
        }

        return pm;
    }
}
