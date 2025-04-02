package com.devone.ptgvis;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class PointGenerator {
    private final List<Expression> filterExpressions;
    private final Expression sortExpression;

    public PointGenerator(List<String> filterExpressions, String sortExpression) {
        this.filterExpressions = filterExpressions.stream()
                .map(expr -> AviatorEvaluator.compile(expr, true))
                .collect(Collectors.toList());

        this.sortExpression = AviatorEvaluator.compile(sortExpression, true);
    }

    public static PointGenerator loadFromYaml(InputStream input) {
        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(input);
        List<String> filters = (List<String>) data.getOrDefault("filters", new ArrayList<>());
        List<String> sortList = (List<String>) data.getOrDefault("sort", Collections.singletonList("y"));
        String sort = sortList.get(0);

        return new PointGenerator(filters, sort);
    }

    public List<Location3D> generateInnerPoints(GeneratorParams params) {
        int ox = params.x;
        int oy = params.y;
        int oz = params.z;
        
        int innerRadius = params.innerRadius;
        
        int offsetX = params.offsetX;
        int offsetY = params.offsetY;
        int offsetZ = params.offsetZ;

        String direction = params.breakDirection;

        int[] center = computeFigureCenter(ox, oy, oz, offsetX, offsetY, offsetZ, direction);

        return generateInnerPoints(center[0], center[1], center[2], innerRadius);
    }

    private int[] computeFigureCenter(int ox, int oy, int oz, int offsetX, int offsetY, int offsetZ, String direction) {

        int cx = ox + offsetX;
        int cy = oy + offsetY;
        int cz = oz + offsetZ;

        return new int[]{cx, cy, cz};
    }

    
    private List<Location3D> generateInnerPoints(int cx, int cy, int cz, int inner_radius) {
        List<Location3D> result = new ArrayList<>();
        Map<String, Object> env = new HashMap<>();

        for (int y = cy - inner_radius; y <= cy + inner_radius; y++) {
            for (int x = cx - inner_radius; x <= cx + inner_radius; x++) {
                for (int z = cz - inner_radius; z <= cz + inner_radius; z++) {
                    env.put("x", x);
                    env.put("y", y);
                    env.put("z", z);
                    env.put("cx", cx);
                    env.put("cy", cy);
                    env.put("cz", cz);
                    env.put("r", inner_radius);

                    if (applyFilters(env)) {
                        result.add(new Location3D(x, y, z));
                    }
                }
            }
        }

        result.sort(Comparator.comparingDouble(loc -> {
            env.put("x", loc.x);
            env.put("y", loc.y);
            env.put("z", loc.z);
            return ((Number) sortExpression.execute(env)).doubleValue();
        }));

        return result;
    }

    public List<Location3D> generateOuterPoints(GeneratorParams params) {
        int ox = params.x;
        int oy = params.y;
        int oz = params.z;
        

        int outerRadius = params.outerRadius;
        
        int offsetX = params.offsetX;
        int offsetY = params.offsetY;
        int offsetZ = params.offsetZ;

        String direction = params.breakDirection;

        int[] center = computeFigureCenter(ox, oy, oz, offsetX, offsetY, offsetZ, direction);

        return generateOuterPoints(center[0], center[1], center[2], outerRadius);
    }

    private List<Location3D> generateOuterPoints(int cx, int cy, int cz, int radius) {
        List<Location3D> result = new ArrayList<>();
        for (int y = cy - radius; y <= cy + radius; y++) {
            for (int x = cx - radius; x <= cx + radius; x++) {
                for (int z = cz - radius; z <= cz + radius; z++) {
                    result.add(new Location3D(x, y, z));
                }
            }
        }
        return result;
    }



    private boolean applyFilters(Map<String, Object> env) {

        for (Expression expr : filterExpressions) {
            Object result = expr.execute(env);
            if (!(result instanceof Boolean) || !(Boolean) result) {
                return false;
            }
        }
        return true;
    }
}
