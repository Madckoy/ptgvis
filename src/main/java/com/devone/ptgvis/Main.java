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

        String patternFile = null;
        int ox = 0, oy = 0, oz = 0;
        int cx = 0, cy = 0, cz = 0;
        int r = 5, radius = 10;
        int offset = -1;
        String facing = null;

        for (String arg : args) {
            if (arg.startsWith("--pattern=")) patternFile = arg.substring("--pattern=".length());
            else if (arg.startsWith("--ox=")) ox = Integer.parseInt(arg.substring("--ox=".length()));
            else if (arg.startsWith("--oy=")) oy = Integer.parseInt(arg.substring("--oy=".length()));
            else if (arg.startsWith("--oz=")) oz = Integer.parseInt(arg.substring("--oz=".length()));
            else if (arg.startsWith("--r="))   r = Integer.parseInt(arg.substring("--r=".length()));
            else if (arg.startsWith("--radius=")) radius = Integer.parseInt(arg.substring("--radius=".length()));
            else if (arg.startsWith("--offset=")) offset = Integer.parseInt(arg.substring("--offset=".length()));
            else if (arg.startsWith("--facing=")) facing = arg.substring("--facing=".length());
        }

        if (patternFile == null) {
            System.err.println("Pattern file required via --pattern=...");
            return;
        }

        try (InputStream in = new FileInputStream(patternFile)) {
            PointGenerator generator = PointGenerator.loadFromYaml(in);

            List<Location3D> kept = generator.generateInnerPointsFromObserver(ox, oy, oz, radius, facing, r, null);

            List<Location3D> all =  generator.generateOuterPointsFromObserver(ox, oy, oz, radius, facing, null);

            List<Location3D> removed = new ArrayList<>(all);
            removed.removeAll(kept);

            HtmlPlotExporter.export(all, removed, kept);
        }
    }

}
