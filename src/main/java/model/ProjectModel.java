package model;
import java.util.*;



public class ProjectModel {
  public List<ClassInfo> classes = new ArrayList<>();
  public Set<String> packages = new HashSet<>();
  public int totalFileLOC = 0;
}