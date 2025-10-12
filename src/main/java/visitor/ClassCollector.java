package visitor;
import org.eclipse.jdt.core.dom.*;
import model.*;



public class ClassCollector extends ASTVisitor {
private final CompilationUnit cu; private final ProjectModel pm;
public ClassCollector(CompilationUnit cu, ProjectModel pm){ this.cu=cu; this.pm=pm; }


@Override public boolean visit(PackageDeclaration node){
 pm.packages.add(node.getName().getFullyQualifiedName()); return super.visit(node);
}
@Override public boolean visit(TypeDeclaration type){
 if(type.isInterface()) return false;
 ClassInfo ci = new ClassInfo();
 ci.name = type.getName().getIdentifier();
 var pkg = cu.getPackage(); ci.packageName = pkg==null? "" : pkg.getName().getFullyQualifiedName();

 // Attributs
 for (FieldDeclaration f : type.getFields()) {
   for (Object frag : f.fragments()) {
     AttributeInfo ai = new AttributeInfo();
     ai.name = ((VariableDeclarationFragment)frag).getName().getIdentifier();
     ai.visibility = f.modifiers().toString(); // simple
     ci.attributes.add(ai);
   }
 }
 // Méthodes
 for (MethodDeclaration m : type.getMethods()) {
   MethodInfo mi = new MethodInfo();
   mi.name = m.getName().getIdentifier();
   mi.paramCount = m.parameters().size();
   int start = cu.getLineNumber(m.getStartPosition());
   int end   = cu.getLineNumber(m.getStartPosition()+m.getLength());
   mi.loc = Math.max(0, end - start + 1);
   ci.methods.add(mi);
 }
 pm.classes.add(ci);
 return false; 
}
}
