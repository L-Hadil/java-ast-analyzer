package processor;

import model.*;
import java.util.*;
import java.util.stream.*;

public class StatisticsCalculator {

    public static Map<String, Object> compute(ProjectModel pm, int X) {
        Map<String, Object> r = new LinkedHashMap<>();

        // ---- 1 à 4 ----
        int nClasses = pm.classes.size();
        int nMethods = pm.classes.stream().mapToInt(c -> c.methods.size()).sum();
        int nAttrs   = pm.classes.stream().mapToInt(c -> c.attributes.size()).sum();
        int locMethods = pm.classes.stream()
                                   .flatMap(c -> c.methods.stream())
                                   .mapToInt(m -> m.loc)
                                   .sum();

        r.put("1.nbClasses", nClasses);
        r.put("2.nbLOC_app", pm.totalFileLOC);
        r.put("3.nbMethods", nMethods);
        r.put("4.nbPackages", pm.packages.size());

        // ---- 5 à 7 ----
        r.put("5.avgMethodsPerClass", nClasses == 0 ? 0 : nMethods / (double) nClasses);
        r.put("6.avgLOCPerMethod", nMethods == 0 ? 0 : locMethods / (double) nMethods);
        r.put("7.avgAttrsPerClass", nClasses == 0 ? 0 : nAttrs / (double) nClasses);

        // ---- 8, 9, 10 ----
        int k = (int) Math.ceil(0.10 * Math.max(1, nClasses));
        Comparator<ClassInfo> byMeth = Comparator.comparingInt(c -> c.methods.size());
        Comparator<ClassInfo> byAttr = Comparator.comparingInt(c -> c.attributes.size());

        List<ClassInfo> topMeth = pm.classes.stream()
                .sorted(byMeth.reversed())
                .limit(k)
                .collect(Collectors.toList());

        List<ClassInfo> topAttr = pm.classes.stream()
                .sorted(byAttr.reversed())
                .limit(k)
                .collect(Collectors.toList());

        Set<String> inter = topMeth.stream()
                .map(c -> c.name)
                .collect(Collectors.toSet());
        inter.retainAll(
                topAttr.stream().map(c -> c.name).collect(Collectors.toSet())
        );

        r.put("8.top10pctByMethods",
                topMeth.stream().map(c -> c.name).collect(Collectors.toList()));
        r.put("9.top10pctByAttrs",
                topAttr.stream().map(c -> c.name).collect(Collectors.toList()));
        r.put("10.intersection", new ArrayList<>(inter));

        // ---- 11 ----
        r.put("11.classesWithMoreThanXMethods",
                pm.classes.stream()
                        .filter(c -> c.methods.size() > X)
                        .map(c -> c.name)
                        .collect(Collectors.toList()));

        // ---- 12 ----
        Map<String, List<String>> topMethByLOCPerClass = new LinkedHashMap<>();
        for (var c : pm.classes) {
            int m = c.methods.size();
            int t = (int) Math.ceil(0.10 * Math.max(1, m));
            var list = c.methods.stream()
                    .sorted(Comparator.comparingInt((MethodInfo mi) -> mi.loc).reversed())
                    .limit(t)
                    .map(mi -> mi.name + "(" + mi.loc + ")")
                    .collect(Collectors.toList());
            topMethByLOCPerClass.put(c.name, list);
        }
        r.put("12.top10pctLongestMethodsPerClass", topMethByLOCPerClass);

        // ---- 13 ----
        int maxParams = pm.classes.stream()
                .flatMap(c -> c.methods.stream())
                .mapToInt(mi -> mi.paramCount)
                .max()
                .orElse(0);

        r.put("13.maxParamsAmongAllMethods", maxParams);

        return r;
    }
}
