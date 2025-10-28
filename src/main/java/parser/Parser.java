package parser;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.JavaCore;
import java.nio.file.*;
import java.io.*;
import java.util.*;
import java.util.Map;

public class Parser {
  public static List<Path> listJava(Path root) throws IOException {
    List<Path> out = new ArrayList<>();
    try (var s = Files.walk(root)) { s.filter(p->p.toString().endsWith(".java")).forEach(out::add); }
    return out;
  }

  // nouveau: parser avec bindings activés
  public static CompilationUnit parse(String src, Path srcRoot) {
    ASTParser p = ASTParser.newParser(AST.JLS11);
    p.setKind(ASTParser.K_COMPILATION_UNIT);
    p.setResolveBindings(true);
    p.setBindingsRecovery(true);
    p.setCompilerOptions(JavaCore.getOptions());
    p.setEnvironment(new String[0], new String[]{ srcRoot.toString() }, new String[]{"UTF-8"}, true);
    p.setUnitName("Dummy.java");
    p.setSource(src.toCharArray());
    return (CompilationUnit) p.createAST(null);
  }
}
