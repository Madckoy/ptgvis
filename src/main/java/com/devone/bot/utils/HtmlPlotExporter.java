package com.devone.bot.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.devone.bot.utils.blocks.BotLocation;

public class HtmlPlotExporter {

    public static void export(String ptrnName, List<BotLocation> outerPts, List<BotLocation> innerPts, 
                            List<BotLocation> substractedPts, BotLocation observerPos, BotLocation figureCenter) throws IOException {

        StringBuilder html = new StringBuilder();
        html.append("<html><head><script src='https://cdn.plot.ly/plotly-latest.min.js'></script></head><body>");
        html.append("<div id='plot' style='width:100%;height:100vh;'></div><script>\n");

        // Преобразуем точки в блоки (для каждой группы: innerPts, outerPts, removedPts)
        addMesh3dSection(html, outerPts, "outerBlocks", "#000000",0.25); // Цвет для внешних блоков (например, gray)
        addMesh3dSection(html, innerPts, "innerBlocks", "#90EE90", 0.7);
        addMesh3dSection(html, substractedPts, "substract", "#DDDDDD", 0.5); // Цвет для удаленных точек

        // Добавление наблюдателя и центра как блоков с красным и синим цветами через addMesh3dSection
        List<BotLocation> observerList = new ArrayList<>();
        observerList.add(observerPos); // Добавляем точку для наблюдателя
        addMesh3dSection(html, observerList, "observer", "red", 0.5); // Наблюдатель красный

        List<BotLocation> centerList = new ArrayList<>();
        centerList.add(figureCenter); // Добавляем точку для центра
        addMesh3dSection(html, centerList, "figureCenter", "blue", 0.5); // Центр синий

        // Визуализация 3D
        html.append("Plotly.newPlot('plot', [outerBlocks, innerBlocks, substract, observer, figureCenter], {")
        .append("margin:{l:0,r:0,b:0,t:30},")
        .append("scene:{")
        .append("    xaxis:{title:'X'},")  // Ось X будет горизонтальной
        .append("    yaxis:{title:'Y'},")  // Ось Y будет вертикальной
        .append("    zaxis:{title:'Z'},")  // Ось Z будет глубиной
        .append("    camera: {")
        .append("        up: {x: 0, y: 1, z: 0},") // Ось Y вертикальная
        .append("        eye: {x: 1.5, y: 1.5, z: 3.5},") // Позиция камеры для правильного обзора
        .append("        center: {x: 0, y: 0, z: 0}") // Центр сцены
        .append("    }")
        .append("},")
        .append("title:'3D Pattern Visualization'});\n");

        html.append("</script></body></html>");

        // Сохранение результата
        try (FileWriter writer = new FileWriter(ptrnName + "_visualization.html")) {
            writer.write(html.toString());
        }

        System.out.println("Visualization saved to: " + ptrnName + "_visualization.html");
    }

    // Метод для добавления 3D-сетки
    private static void addMesh3dSection(StringBuilder html, List<BotLocation> points, String varName, String colorHex, double opacity) {
        if (points == null || points.isEmpty()) {
            System.err.println("Warning: No points to plot for " + varName);
            return;
        }
    
        html.append("var ").append(varName).append(" = {type:'mesh3d', x:[], y:[], z:[], i:[], j:[], k:[], ")
            .append("facecolor:[], text:[], opacity:").append(opacity).append(", name:'").append(varName)
            .append("', showlegend:true, hoverinfo:'text'};\n");
    
        html.append("var x = ").append(varName).append(".x, y = ").append(varName).append(".y, z = ").append(varName).append(".z;\n");
        html.append("var i = ").append(varName).append(".i, j = ").append(varName).append(".j, k = ").append(varName).append(".k;\n");
        html.append("var facecolor = ").append(varName).append(".facecolor;\n");
        html.append("var text = ").append(varName).append(".text;\n");
    
        int vertexOffset = 0;
        for (BotLocation point : points) {
            // Добавление куба для каждой точки
            double x0 = point.getX() - 0.5, x1 = point.getX() + 0.5;
            double y0 = point.getY() - 0.5, y1 = point.getY() + 0.5;
            double z0 = point.getZ() - 0.5, z1 = point.getZ() + 0.5;
    
            // Вершины куба
            html.append("x.push(").append(x0).append("); x.push(").append(x1).append("); x.push(").append(x1).append("); x.push(").append(x0).append(");");
            html.append("x.push(").append(x0).append("); x.push(").append(x1).append("); x.push(").append(x1).append("); x.push(").append(x0).append(");");
    
            html.append("y.push(").append(y0).append("); y.push(").append(y0).append("); y.push(").append(y1).append("); y.push(").append(y1).append(");");
            html.append("y.push(").append(y0).append("); y.push(").append(y0).append("); y.push(").append(y1).append("); y.push(").append(y1).append(");");
    
            html.append("z.push(").append(z0).append("); z.push(").append(z0).append("); z.push(").append(z0).append("); z.push(").append(z0).append(");");
            html.append("z.push(").append(z1).append("); z.push(").append(z1).append("); z.push(").append(z1).append("); z.push(").append(z1).append(");");
    
            // Треугольники
            int[][] faces = {
                {0, 1, 2}, {0, 2, 3},
                {4, 5, 6}, {4, 6, 7},
                {0, 1, 5}, {0, 5, 4},
                {2, 3, 7}, {2, 7, 6},
                {1, 2, 6}, {1, 6, 5},
                {3, 0, 4}, {3, 4, 7}
            };
    
            for (int[] face : faces) {
                html.append("i.push(").append(vertexOffset + face[0]).append(");");
                html.append("j.push(").append(vertexOffset + face[1]).append(");");
                html.append("k.push(").append(vertexOffset + face[2]).append(");");
            }
    
            // Цвет и текст
            for (int f = 0; f < faces.length; f++) {
                html.append("facecolor.push('").append(colorHex).append("');");
                html.append("text.push('").append("X: ").append(point.getX()).append("<br>Y: ").append(point.getY()).append("<br>Z: ").append(point.getZ()).append("');");
            }
    
            vertexOffset += 8;
        }
    }
}
