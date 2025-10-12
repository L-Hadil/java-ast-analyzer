package processor;
import parser.Parser; 
import visitor.ClassCollector;

import model.*; import org.eclipse.jdt.core.dom.*; import java.nio.file.*; import java.nio.charset.*; 
public class ASTProcessor {
  public static ProjectModel process(Path srcRoot) throws Exception {
    ProjectModel pm = new ProjectModel();
    for (var p : Parser.listJava(srcRoot)) {
      String code = Files.readString(p, StandardCharsets.UTF_8);
      pm.totalFileLOC += (int)code.lines().filter(s->!s.isBlank()).count();
      CompilationUnit cu = Parser.parse(code);
      cu.accept(new ClassCollector(cu, pm));
    }
    return pm;
  }
}
