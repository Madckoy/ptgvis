package com.devone.bot;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.devone.bot.core.math.MathMaxFunction;
import com.devone.bot.patterns.generator.GeneratorParams;
import com.devone.bot.patterns.generator.PointGenerator;
import com.devone.bot.utils.BotCoordinate3D;
import com.devone.bot.utils.ConfigLoader;
import com.devone.bot.utils.HtmlPlotExporter;
import com.googlecode.aviator.AviatorEvaluator;

public class Main {

    public static void main(String[] args) throws Exception {

        AviatorEvaluator.addFunction(new com.googlecode.aviator.runtime.function.math.MathAbsFunction());
        AviatorEvaluator.addFunction(new MathMaxFunction());

        GeneratorParams params = ConfigLoader.load("config.yml");

        try (InputStream in = new FileInputStream(params.patternName)) {
            PointGenerator generator = PointGenerator.loadFromYaml(in);

            List<BotCoordinate3D> innerPts = generator.generateInnerPoints(params);

            List<BotCoordinate3D> outerPts =  generator.generateOuterPoints(params);

            List<BotCoordinate3D> removedPts = new ArrayList<>(outerPts);
            removedPts.removeAll(innerPts);

            BotCoordinate3D observerPos = new BotCoordinate3D(params.x, params.y, params.z); // 

            BotCoordinate3D figureCenter = new BotCoordinate3D(params.x + params.offsetX, params.y + params.offsetY, params.z + params.offsetZ);
            
            HtmlPlotExporter.export(params.patternName, outerPts, removedPts, innerPts, observerPos, figureCenter);
        }
    }

}
