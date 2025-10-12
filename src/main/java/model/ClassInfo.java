package model;
import java.util.*;


public class ClassInfo {
  public String name, packageName;
  public List<MethodInfo> methods = new ArrayList<>();
  public List<AttributeInfo> attributes = new ArrayList<>();
}