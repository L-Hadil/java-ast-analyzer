// parser/Parser.java
package parser;
import org.eclipse.jdt.core.dom.*;
import java.nio.file.*; import java.io.*; import java.util.*;

public class Parser {
  public static List<Path> listJava(Path root) throws IOException {
    List<Path> out = new ArrayList<>();
    try (var s = Files.walk(root)) { s.filter(p->p.toString().endsWith(".java")).forEach(out::add); }
    return out;
  }
  public static CompilationUnit parse(String src) {
    ASTParser p = ASTParser.newParser(AST.JLS11);
    p.setKind(ASTParser.K_COMPILATION_UNIT);
    p.setSource(src.toCharArray());
    p.setBindingsRecovery(false); p.setResolveBindings(false);
    return (CompilationUnit) p.createAST(null);
  }
}
