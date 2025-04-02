package com.devone.ptgvis;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.aviator.AviatorEvaluator;

public class Main {

    public static void main(String[] args) throws Exception {

        AviatorEvaluator.addFunction(new com.googlecode.aviator.runtime.function.math.MathAbsFunction());
        AviatorEvaluator.addFunction(new MathMaxFunction());

        GeneratorParams params = ConfigLoader.load("config.yml");

        try (InputStream in = new FileInputStream(params.patternName)) {
            PointGenerator generator = PointGenerator.loadFromYaml(in);

            List<Location3D> kept = generator.generateInnerPoints(params);

            List<Location3D> all =  generator.generateOuterPoints(params);

            List<Location3D> removed = new ArrayList<>(all);
            removed.removeAll(kept);

            Location3D observer = new Location3D(params.x, params.y, params.z);
            Location3D center = new Location3D(params.x + params.offsetX, params.y + params.offsetY, params.z + params.offsetZ);
            
            HtmlPlotExporter.export(params.patternName, all, removed, kept, observer, center);
        }
    }

}
