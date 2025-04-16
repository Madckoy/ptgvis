package com.devone.bot;

import com.devone.bot.utils.blocks.BotLocation;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.devone.bot.core.math.MathMaxFunction;
import com.devone.bot.patterns.generator.GeneratorParams;
import com.devone.bot.patterns.generator.PointGenerator;
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

            List<BotLocation> innerPts = generator.generateInnerPoints(params);

            List<BotLocation> outerPts =  generator.generateOuterPoints(params);

            List<BotLocation> removedPts = new ArrayList<>(outerPts);
            removedPts.removeAll(innerPts);

            BotLocation observerPos = new BotLocation(params.x, params.y, params.z); // 

            BotLocation figureCenter = new BotLocation(params.x + params.offsetX, params.y + params.offsetY, params.z + params.offsetZ);
            
            HtmlPlotExporter.export(params.patternName, outerPts, innerPts, removedPts, observerPos, figureCenter);
        }
    }

}
